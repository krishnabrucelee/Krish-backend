package ck.panda.rabbitmq.util;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import ck.panda.constants.CloudStackConstants;
import ck.panda.constants.EmailConstants;
import ck.panda.constants.EventTypes;
import ck.panda.domain.entity.Event;
import ck.panda.domain.entity.Event.EventType;
import ck.panda.domain.entity.Event.Status;
import ck.panda.service.AsynchronousJobService;
import ck.panda.service.ConvertEntityService;
import ck.panda.service.EmailJobService;
import ck.panda.service.SyncService;
import ck.panda.util.CloudStackServer;
import ck.panda.util.infrastructure.AuthenticatedExternalWebService;
import ck.panda.util.infrastructure.externalwebservice.ExternalWebServiceStub;

/**
 * Action event listener will listen and update resource data to our App DB when an event handled directly in CS server.
 */
public class ActionListener implements MessageListener {
    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionListener.class);

    /** Response event entity. */
    private ResponseEvent eventResponse = null;

    /** Sync service. */
    private SyncService syncService;

    /** Asynchronous service. */
    private AsynchronousJobService asyncService;

    /** Cloud stack server service. */
    private CloudStackServer cloudStackServer;

    /** Reference of the convert entity service. */
    private ConvertEntityService convertEntityService;

    /** Admin username. */
    private String backendAdminUsername;

    /** Admin role. */
    private String backendAdminRole;

    /** Email job service. */
    private EmailJobService emailJobService;

    /**
     * Inject SyncService.
     *
     * @param syncService syncService object.
     * @param asyncService asynchronous Service object.
     * @param cloudStackServer cloudStackServer object.
     * @param backendAdminUsername backend admin user name.
     * @param backendAdminRole backend admin user role.
     */
    public ActionListener(SyncService syncService, AsynchronousJobService asyncService, ConvertEntityService convertEntityService,
            CloudStackServer cloudStackServer, String backendAdminUsername, String backendAdminRole, EmailJobService emailJobService) {
        this.syncService = syncService;
        this.asyncService = asyncService;
        this.cloudStackServer = cloudStackServer;
        this.backendAdminUsername = backendAdminUsername;
        this.backendAdminRole = backendAdminRole;
        this.convertEntityService = convertEntityService;
        this.emailJobService = emailJobService;
    }

    /** Action event listener . */
    @Override
    public void onMessage(Message message) {
        try {
            String eventName = "";
            JSONObject eventObject = new JSONObject(new String(message.getBody()));
            if (eventObject.has(CloudStackConstants.CS_EVENT_NAME)
                    && eventObject.has(CloudStackConstants.CS_EVENT_STATUS)) {
                    if (eventObject.getString(CloudStackConstants.CS_EVENT_NAME) != null) {
                        eventName = eventObject.getString(CloudStackConstants.CS_EVENT_NAME);
                        String eventStart = eventName.substring(0, eventName.indexOf('.', 0)) + ".";
                        this.handleActionEvent(eventName, eventStart, new String(message.getBody()));
                    }
            }
        } catch (Exception e) {
            LOGGER.debug("Error on convert action event message", e.getMessage());
        }
    }

    /**
     * Handling VM events and updated those in our application DB according to the type of events.
     *
     * @param eventName event name.
     * @param eventStart event name start with.
     * @param eventMessage event message.
     * @throws Exception exception.
     */
    public void handleActionEvent(String eventName, String eventStart, String eventMessage) throws Exception {
        syncService.init(cloudStackServer);
        ExternalWebServiceStub externalWebService = new ExternalWebServiceStub();
        AuthenticatedExternalWebService authenticatedExternalWebService = new AuthenticatedExternalWebService(
                backendAdminUsername, null, AuthorityUtils.commaSeparatedStringToAuthorityList(backendAdminRole));
        authenticatedExternalWebService.setExternalWebService(externalWebService);
        SecurityContextHolder.getContext().setAuthentication(authenticatedExternalWebService);
        JSONObject eventObject = new JSONObject(eventMessage);
        Thread.sleep(5000);
        // Event record from action listener call.
        ObjectMapper eventmapper = new ObjectMapper();
        eventResponse = eventmapper.readValue(eventMessage, ResponseEvent.class);
        Event actionEvent = new Event();
        actionEvent.setEvent(eventResponse.getEvent());
        actionEvent.setEventDateTime(
                convertEntityService.getTimeService().convertDateAndTime(eventResponse.getEventDateTime()));
        actionEvent.setEventOwnerId(convertEntityService.getOwnerByUuid(eventResponse.getUser()));
        actionEvent.setEventType(EventType.ACTION);
        actionEvent.setResourceUuid(eventResponse.getEntityuuid());
        actionEvent.setMessage(eventResponse.getDescription());
        if (eventResponse.getStatus().equalsIgnoreCase(CloudStackConstants.CS_EVENT_COMPLETE)) {
            actionEvent.setStatus(Status.INFO);
        } else {
            actionEvent.setStatus(Status.valueOf(eventResponse.getStatus().toUpperCase()));
        }
        if (eventResponse.getDescription().contains(CloudStackConstants.CS_STATUS_ERROR)) {
            actionEvent.setStatus(Status.ERROR);
        }
        // save the event get from action listener.
        convertEntityService.getWebsocketService().handleEventAction(actionEvent);
        if (eventObject.getString(CloudStackConstants.CS_EVENT_STATUS)
                .equalsIgnoreCase(CloudStackConstants.CS_EVENT_COMPLETE)) {
            switch (eventStart) {
            case EventTypes.EVENT_USER:
                if (eventName.equals(EventTypes.EVENT_USER_LOGIN) || eventName.equals(EventTypes.EVENT_USER_LOGOUT)) {
                    LOGGER.debug("User sync", eventMessage);// TODO: Will do login event.
                } else {
                    Thread.sleep(3000); // Delay sync call for user to get success CRUD.
                    syncService.syncUser();
                    if (eventName.equals(EventTypes.EVENT_USER_CREATE)) {
                        syncService.syncUpdateUserRole();
                        ObjectMapper mapper = new ObjectMapper();
                        eventResponse = mapper.readValue(eventMessage, ResponseEvent.class);
                        EmailEvent emailEvent = new EmailEvent();
                        emailEvent.setEntityuuid(eventResponse.getEntityuuid());
                        emailEvent.setResourceUuid(eventResponse.getEntityuuid());
                        emailEvent.setEvent(EventTypes.EVENT_USER_CREATE);
                        emailEvent.setEventType(EmailConstants.ACCOUNT);
                        emailEvent.setEventDateTime(eventResponse.getEventDateTime());
                        emailEvent.setUser(convertEntityService.getOwnerByUuid(eventResponse.getEntityuuid()).toString());
                        emailEvent.setSubject(EmailConstants.SUBJECT_ACCOUNT_SIGNUP);
                        emailJobService.sendMessageToQueue(emailEvent);
                    } else if (eventName.equals(EventTypes.EVENT_USER_DELETE)) {
                        syncService.syncUpdateUserRole();
                        ObjectMapper mapper = new ObjectMapper();
                        eventResponse = mapper.readValue(eventMessage, ResponseEvent.class);
                        EmailEvent emailEvent = new EmailEvent();
                        emailEvent.setEntityuuid(eventResponse.getEntityuuid());
                        emailEvent.setResourceUuid(eventResponse.getEntityuuid());
                        emailEvent.setEvent(EventTypes.EVENT_USER_DELETE);
                        emailEvent.setEventType(EmailConstants.ACCOUNT);
                        emailEvent.setEventDateTime(eventResponse.getEventDateTime());
                        emailEvent.setUser(convertEntityService.getDeletedOwnerByUuid(eventResponse.getEntityuuid()).toString());
                        emailEvent.setSubject(EmailConstants.SUBJECT_ACCOUNT_DELETE);
                        emailJobService.sendMessageToQueue(emailEvent);
                    } else if (eventName.equals(EventTypes.EVENT_USER_UPDATE)) {
                        syncService.syncUpdateUserRole();
                        ObjectMapper mapper = new ObjectMapper();
                        eventResponse = mapper.readValue(eventMessage, ResponseEvent.class);
                        EmailEvent emailEvent = new EmailEvent();
                        emailEvent.setEntityuuid(eventResponse.getEntityuuid());
                        emailEvent.setResourceUuid(eventResponse.getEntityuuid());
                        emailEvent.setEvent(EventTypes.EVENT_USER_UPDATE);
                        emailEvent.setEventType(EmailConstants.ACCOUNT);
                        emailEvent.setEventDateTime(eventResponse.getEventDateTime());
                        emailEvent.setUser(convertEntityService.getOwnerByUuid(eventResponse.getEntityuuid()).toString());
                        emailEvent.setSubject(EmailConstants.SUBJECT_ACCOUNT_PASSWORD);
                        emailJobService.sendMessageToQueue(emailEvent);
                    }
                }
                break;
            case EventTypes.EVENT_REGISTER_SSH:
                LOGGER.debug("Register SSH/API sync", eventMessage);
                break;
            case EventTypes.EVENT_ACCOUNT:
                LOGGER.debug("Account sync", eventMessage);
                syncService.syncDepartment();
                break;
            case EventTypes.EVENT_DISK:
                LOGGER.debug("Storage offer sync", eventMessage);
                syncService.syncStorageOffering();
                break;
            case EventTypes.EVENT_DOMAIN:
                LOGGER.debug("Domain sync", eventMessage);
                syncService.syncDomain();
                if (eventName.equals(EventTypes.EVENT_DOMAIN_CREATE)
                        || eventName.equals(EventTypes.EVENT_DOMAIN_UPDATE)) {
                    ObjectMapper mapper = new ObjectMapper();
                    eventResponse = mapper.readValue(eventMessage, ResponseEvent.class);
                    syncService.syncResourceLimitActionEvent(eventResponse);
                }
                break;
            case EventTypes.EVENT_ZONE:
                LOGGER.debug("Zone sync", eventMessage);
                syncService.syncZone();
                break;
            case EventTypes.EVENT_GUEST:
                LOGGER.debug("OSType sync", eventMessage);
                syncService.syncOsCategory();
                syncService.syncOsTypes();
                break;
            case EventTypes.EVENT_ISO:
                if (!eventName.contains(EventTypes.EVENT_ISO_TEMPLATE_DELETE)) {
                    LOGGER.debug("ISO sync", eventMessage);
                    syncService.syncTemplates();
                }
                break;
            case EventTypes.EVENT_NETWORK:
                if (eventName.contains(EventTypes.EVENT_NETWORK_OFFERING)) {
                    LOGGER.debug("Network Offering sync", eventMessage);
                    if (eventName.contains(EventTypes.EVENT_NETWORK_EDIT)
                            || eventName.contains(EventTypes.EVENT_NETWORK_DELETE)) {
                        ObjectMapper mapper = new ObjectMapper();
                        eventResponse = mapper.readValue(eventMessage, ResponseEvent.class);
                        asyncService.asyncNetworkOffering(eventResponse);
                    } else {
                        syncService.syncNetworkOffering();
                    }
                } else if (eventName.contains(EventTypes.EVENT_NETWORK_CREATE)
                        && eventName.contains(EventTypes.EVENT_NETWORK_DELETE)) {
                    LOGGER.debug("Network sync", eventMessage);
                    syncService.syncNetwork();
                }
                break;
            case EventTypes.EVENT_PHYSICAL:
                LOGGER.debug("Physical Network sync", eventMessage);
                break;
            case EventTypes.EVENT_POD:
                LOGGER.debug("POD sync", eventMessage);
                break;
            case EventTypes.EVENT_HOST:
                LOGGER.debug("Host sync", eventMessage);
                break;
            case EventTypes.EVENT_PROXY:
                LOGGER.debug("Proxy sync", eventMessage);
                break;
            case EventTypes.EVENT_ROUTER:
                LOGGER.debug("Router sync", eventMessage);
                break;
            case EventTypes.EVENT_SERVICE:
                LOGGER.debug("Compute Offering sync", eventMessage);
                syncService.syncComputeOffering();
                break;
            case EventTypes.EVENT_SNAPSHOT:
                LOGGER.debug("Volume snapshot sync", eventMessage);
                break;
            case EventTypes.EVENT_SNAPSHOT_POLICY:
                if (eventName.equals(EventTypes.EVENT_SNAPSHOT_POLICY_CREATE)) {
                    LOGGER.debug("Volume snapshot policy sync", eventMessage);
                    syncService.syncSnapshotPolicy();
                }
                break;
            case EventTypes.EVENT_VOLUME:
                if (eventName.contains(EventTypes.EVENT_VOLUME_DELETE)) {
                    LOGGER.debug("Volume sync", eventMessage);
                    ObjectMapper mapper = new ObjectMapper();
                    eventResponse = mapper.readValue(eventMessage, ResponseEvent.class);
                    asyncService.asyncVolume(eventResponse);
                }
                break;
            case EventTypes.EVENT_TEMPLATE:
                if (!eventName.contains(EventTypes.EVENT_TEMPLATE_DELETE)) {
                    LOGGER.debug("templates sync", eventMessage);
                    syncService.syncTemplates();
                }
                break;
            case EventTypes.EVENT_VM_SNAPSHOT:
                LOGGER.debug("VM snapshot sync", eventMessage);
                // syncService.syncVmSnapshots();
                break;
            case EventTypes.EVENT_VM:
                LOGGER.debug("VM update sync", eventMessage);
                if (eventName.contains(EventTypes.EVENT_VM_UPDATE)) {
                    ObjectMapper mapper = new ObjectMapper();
                    eventResponse = mapper.readValue(eventMessage, ResponseEvent.class);
                    asyncService.syncVMUpdate(eventResponse.getEntityuuid());
                }
                break;
            case EventTypes.EVENT_VNC:
                LOGGER.debug("VNC sync", eventMessage);
                break;
            case EventTypes.EVENT_PROJECT:
                LOGGER.debug("Project", eventMessage);
                syncService.syncProject();
                if (eventName.equals(EventTypes.EVENT_PROJECT_CREATE)) {
                    ObjectMapper mapper = new ObjectMapper();
                    eventResponse = mapper.readValue(eventMessage, ResponseEvent.class);
                    syncService.syncResourceLimitActionEventProject(eventResponse);
                }
                break;
            case EventTypes.EVENT_VPC:
                LOGGER.debug("VPC sync", eventMessage);
                break;
            case EventTypes.EVENT_STATIC_NAT:
                LOGGER.debug("Static nat sync", eventMessage);
                syncService.syncIpAddress();
                break;
            default:
                LOGGER.debug("No sync required", eventMessage);
            }
        }
    }
}
