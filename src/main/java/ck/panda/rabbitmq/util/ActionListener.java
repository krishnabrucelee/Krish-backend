package ck.panda.rabbitmq.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import ck.panda.constants.EventTypes;
import ck.panda.service.SyncService;

/**
 * Action event listener will listen and update resource data to our App DB when an event handled directly in
 * CS server.
 */
public class ActionListener implements MessageListener {
    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionListener.class);

    /** Response event entity. */
    private ResponseEvent eventResponse = null;

    /** Sync service. */
    private SyncService syncService;

    /**
     * Inject SyncService.
     *
     * @param syncService syncService object.
     */
    public ActionListener(SyncService syncService) {
        this.syncService = syncService;
    }

    /** Action event listener . */
    @Override
    public void onMessage(Message message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            eventResponse = mapper.readValue(new String(message.getBody()), ResponseEvent.class);
            this.handleActionEvent(eventResponse);
        } catch (Exception e) {
            LOGGER.debug("Error on convert action event message", e);
            e.printStackTrace();
        }
    }

    /**
     * Handling VM events and updated those in our application DB according to the type of events.
     *
     * @param eventObject event object.
     * @throws Exception exception.
     */
    public void handleActionEvent(ResponseEvent eventObject) throws Exception {
        syncService.init();
        switch (eventObject.getEventStart()) {
        case EventTypes.EVENT_VM:
            LOGGER.debug("VM Sync", eventObject.getEntityuuid() + "===" + eventObject.getId());
            syncService.syncInstances();
            break;
        case EventTypes.EVENT_USER:
            LOGGER.debug("User sync", eventObject.getEntityuuid() + "===" + eventObject.getId());
            if (!eventObject.getEvent().equals(EventTypes.EVENT_USER_LOGIN)
                    || eventObject.getEvent().equals(EventTypes.EVENT_USER_LOGOUT)) {
                LOGGER.debug("Account sync", eventObject.getEntityuuid() + "===" + eventObject.getId());
            } else {
                syncService.syncUser();
            }
            break;
        case EventTypes.EVENT_REGISTER_SSH:
            LOGGER.debug("Register SSH/API sync", eventObject.getEntityuuid() + "===" + eventObject.getId());
            break;
        case EventTypes.EVENT_ACCOUNT:
            LOGGER.debug("Account sync", eventObject.getEntityuuid() + "===" + eventObject.getId());
            if (!eventObject.getEvent().equals(EventTypes.EVENT_USER_LOGIN)
                    || eventObject.getEvent().equals(EventTypes.EVENT_USER_LOGOUT)) {
                LOGGER.debug("Account sync", eventObject.getEntityuuid() + "===" + eventObject.getId());
            } else {
                syncService.syncUser();
            }
            break;
        case EventTypes.EVENT_DISK:
            LOGGER.debug("Storage offer sync", eventObject.getEntityuuid() + "===" + eventObject.getId());
            syncService.syncStorageOffering();
            break;
        case EventTypes.EVENT_DOMAIN:
            LOGGER.debug("Domain sync", eventObject.getEntityuuid() + "===" + eventObject.getId());
            syncService.syncDomain();
            break;
        case EventTypes.EVENT_ZONE:
            LOGGER.debug("Zone sync", eventObject.getEntityuuid() + "===" + eventObject.getId());
            syncService.syncZone();
            break;
        case EventTypes.EVENT_GUEST:
            LOGGER.debug("OSType sync", eventObject.getEntityuuid() + "===" + eventObject.getId());
            syncService.syncOsCategory();
            syncService.syncOsTypes();
            break;
        case EventTypes.EVENT_ISO:
            LOGGER.debug("ISO sync", eventObject.getEntityuuid() + "===" + eventObject.getId());
            break;
        case EventTypes.EVENT_NETWORK:
            if (eventObject.getEvent().contains("OFFERING")) {
                LOGGER.debug("Network Offering sync", eventObject.getEntityuuid() + "===" + eventObject.getId());
                syncService.syncNetworkOffering();
            } else {
                LOGGER.debug("Network sync", eventObject.getEntityuuid() + "===" + eventObject.getId());
                syncService.syncNetwork();
            }
            break;
        case EventTypes.EVENT_PHYSICAL:
            LOGGER.debug("Physical Network sync", eventObject.getEntityuuid() + "===" + eventObject.getId());
            break;
        case EventTypes.EVENT_POD:
            LOGGER.debug("POD sync", eventObject.getEntityuuid() + "===" + eventObject.getId());
            break;
        case EventTypes.EVENT_PROXY:
            LOGGER.debug("Proxy sync", eventObject.getEntityuuid() + "===" + eventObject.getId());
            break;
        case EventTypes.EVENT_ROUTER:
            LOGGER.debug("Router sync", eventObject.getEntityuuid() + "===" + eventObject.getId());
            break;
        case EventTypes.EVENT_SERVICE:
            LOGGER.debug("Compute Offering sync", eventObject.getEntityuuid() + "===" + eventObject.getId());
            syncService.syncComputeOffering();
            break;
        case EventTypes.EVENT_SNAPSHOT:
            LOGGER.debug("Volume snapshot sync", eventObject.getEntityuuid() + "===" + eventObject.getId());
            break;
        case EventTypes.EVENT_VOLUME:
            LOGGER.debug("Volume sync", eventObject.getEntityuuid() + "===" + eventObject.getId());
            break;
        case EventTypes.EVENT_TEMPLATE:
            LOGGER.debug("templates sync", eventObject.getEntityuuid() + "===" + eventObject.getId());
            syncService.syncTemplates();
            break;
        case EventTypes.EVENT_VM_SNAPSHOT:
            LOGGER.debug("VM snapshot sync", eventObject.getEntityuuid() + "===" + eventObject.getId());
            break;
        case EventTypes.EVENT_VNC:
            LOGGER.debug("VNC sync", eventObject.getEntityuuid() + "===" + eventObject.getId());
            break;
        case EventTypes.EVENT_VPC:
            LOGGER.debug("VPC sync", eventObject.getEntityuuid() + "===" + eventObject.getId());
            break;
        default:
            LOGGER.debug("No sync required", eventObject.getEvent());
        }
    }
}
