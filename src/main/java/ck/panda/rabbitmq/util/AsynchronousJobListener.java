package ck.panda.rabbitmq.util;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import ck.panda.constants.CloudStackConstants;
import ck.panda.constants.EventTypes;
import ck.panda.domain.entity.Event;
import ck.panda.domain.entity.Event.EventType;
import ck.panda.service.AsynchronousJobService;
import ck.panda.service.ConvertEntityService;
import ck.panda.service.SyncService;
import ck.panda.service.WebsocketService;
import ck.panda.util.CloudStackInstanceService;
import ck.panda.util.CloudStackServer;
import ck.panda.util.ConfigUtil;
import ck.panda.util.infrastructure.AuthenticatedExternalWebService;
import ck.panda.util.infrastructure.externalwebservice.ExternalWebServiceStub;

/**
 * Asynchronous Job listener will listen and update resource data to our App DB when an event handled directly in CS
 * server.
 *
 */
public class AsynchronousJobListener implements MessageListener {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AsynchronousJobListener.class);

    /** Asynchronous job id. */
    public static final String CS_ASYNC_JOB_ID = "jobId";

    /** Command type. */
    public static final String CS_COMMAND = "command";

    /** Sync service. */
    private SyncService syncService;

    /** Asynchronous service. */
    private AsynchronousJobService asyncService;

    /** CloudStack connector reference for instance. */
    private CloudStackInstanceService cloudStackInstanceService;

    /** Websocket service reference. */
    private WebsocketService websocketService;

    /** Convert entity service. */
    private ConvertEntityService convertEntityService;

    /** Cloud stack configuration reference. */
    private ConfigUtil configUtil;

    /** Cloud stack server service. */
    private CloudStackServer cloudStackServer;

    /** Admin username. */
    private String backendAdminUsername;

    /** Admin role. */
    private String backendAdminRole;

    /**
     * Inject SyncService.
     *
     * @param syncService synchronous service object.
     * @param asyncService asynchronous Service object.
     * @param cloudStackServer cloudStackServer object.
     * @param backendAdminUsername default admin name.
     * @param backendAdminRole default admin role.
     */
    public AsynchronousJobListener(SyncService syncService, AsynchronousJobService asyncService,
            CloudStackServer cloudStackServer, ConvertEntityService convertEntityService, ConfigUtil configUtil,
            String backendAdminUsername, String backendAdminRole) {
        this.syncService = syncService;
        this.asyncService = asyncService;
        this.cloudStackServer = cloudStackServer;
        this.backendAdminUsername = backendAdminUsername;
        this.backendAdminRole = backendAdminRole;
        this.convertEntityService = convertEntityService;
        this.configUtil = configUtil;
        this.websocketService = convertEntityService.getWebsocket();
        this.cloudStackInstanceService = convertEntityService.getCSInstanceService();
    }

    @Override
    public void onMessage(Message message) {
        try {
            JSONObject instance = new JSONObject(new String(message.getBody()));
            this.handleStatusEvent(instance);
        } catch (Exception e) {
            LOGGER.debug("Error on convert action event message", e);
            e.printStackTrace();
        }
    }

    /**
     * Handling all the CS events and updated those in our application DB according to the type of events.
     *
     * @param eventObject event object.
     * @throws Exception exception.
     */
    public void handleStatusEvent(JSONObject eventObject) throws Exception {
        if (eventObject.has(CloudStackConstants.CS_EVENT_STATUS)) {
            syncService.init(cloudStackServer);
            ExternalWebServiceStub externalWebService = new ExternalWebServiceStub();
            AuthenticatedExternalWebService authenticatedExternalWebService = new AuthenticatedExternalWebService(
                    backendAdminUsername, null, AuthorityUtils.commaSeparatedStringToAuthorityList(backendAdminRole));
            authenticatedExternalWebService.setExternalWebService(externalWebService);
            SecurityContextHolder.getContext().setAuthentication(authenticatedExternalWebService);
            Event asyncJobEvent = new Event();
            // Event record.
            configUtil.setServer(1L);
            String eventObjectResult = cloudStackInstanceService
                    .queryAsyncJobResult(eventObject.getString(CS_ASYNC_JOB_ID), CloudStackConstants.JSON);
            JSONObject jobResultResponse = new JSONObject(eventObjectResult)
                    .getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
            JSONObject jobResult = null;
            if (jobResultResponse.has(CloudStackConstants.CS_JOB_RESULT)) {
                jobResult = jobResultResponse.getJSONObject(CloudStackConstants.CS_JOB_RESULT);
            }
            // Event record for async call.
            asyncJobEvent.setEvent(eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE));
            asyncJobEvent.setEventDateTime(convertEntityService.getTimeService().getCurrentDateAndTime());
            asyncJobEvent.setEventOwnerId(
                    convertEntityService.getOwnerByUuid(eventObject.getString(CloudStackConstants.CS_USER)));
            asyncJobEvent.setEventType(EventType.ASYNC);
            JSONObject json = new JSONObject(eventObject.getString(CloudStackConstants.CS_CMD_INFO));
            if (eventObject.getString(CloudStackConstants.CS_STATUS)
                    .equalsIgnoreCase(CloudStackConstants.CS_STATUS_FAILED) && jobResult != null) {
                asyncJobEvent.setMessage(jobResult.getString(CloudStackConstants.CS_ERROR_TEXT));
                asyncJobEvent.setStatus(Event.Status.FAILED);
                if (json.has(CloudStackConstants.CS_UUID)) {
                    asyncJobEvent.setResourceUuid(json.getString(CloudStackConstants.CS_UUID));
                }
                switch (eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE)) {
                case EventTypes.EVENT_VM_SNAPSHOT_CREATE:
                    syncService.syncVmSnapshots();
                    break;
                default:
                    LOGGER.debug("No async required");
                }
            } else {
                if (eventObject.has(CloudStackConstants.CS_INSTANCE_UUID)) {
                    asyncJobEvent.setResourceUuid(eventObject.getString(CloudStackConstants.CS_INSTANCE_UUID));
                } else if (eventObject.has(CloudStackConstants.CS_UUID)) {
                    asyncJobEvent.setResourceUuid(json.getString(CloudStackConstants.CS_UUID));
                }
                asyncJobEvent.setStatus(
                        Event.Status.valueOf(eventObject.getString(CloudStackConstants.CS_STATUS).toUpperCase()));
            }
            asyncJobEvent.setEventStartId(json.getString(CloudStackConstants.CS_EVENT_ID));
            asyncJobEvent.setJobId(eventObject.getString(CloudStackConstants.CS_ASYNC_JOB_ID));
            // websocket record for async call.
            websocketService.handleEventAction(asyncJobEvent, eventObject);
        }
    }
}
