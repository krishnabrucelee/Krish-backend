package ck.panda.rabbitmq.util;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import ck.panda.service.AsynchronousJobService;
import ck.panda.service.SyncService;
import ck.panda.util.CloudStackServer;
import ck.panda.util.infrastructure.AuthenticatedExternalWebService;
import ck.panda.util.infrastructure.externalwebservice.ExternalWebServiceStub;

/**
 * Asynchronous Job listener will listen and update resource data to our App DB
 * when an event handled directly in CS server.
 *
 */
public class AsynchronousJobListener implements MessageListener {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionListener.class);

    /** Sync service. */
    private SyncService syncService;

    /** Asynchronous service. */
    private AsynchronousJobService asynchService;

    /** Cloud stack server service. */
    private CloudStackServer cloudStackServer;

    /** Admin username. */
    @Value("${backend.admin.username}")
    private String backendAdminUsername;

    /** Admin role. */
    @Value("${backend.admin.role}")
    private String backendAdminRole;

    /**
     * Inject SyncService.
     *
     * @param syncService synchronous service object.
     * @param asynchService asynchronous Service object.
     * @param cloudStackServer cloudStackServer object.
     */
    public AsynchronousJobListener(SyncService syncService, AsynchronousJobService asynchService, CloudStackServer cloudStackServer) {
        this.syncService = syncService;
        this.asynchService = asynchService;
        this.cloudStackServer = cloudStackServer;
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
        if (eventObject.has("status")) {
            if (eventObject.getString("status").equalsIgnoreCase("SUCCEEDED")) {
                syncService.init(cloudStackServer);
                ExternalWebServiceStub externalWebService = new ExternalWebServiceStub();
                AuthenticatedExternalWebService authenticatedExternalWebService = new AuthenticatedExternalWebService("admin", null,
                        AuthorityUtils.commaSeparatedStringToAuthorityList("BACKEND_ADMIN"));
                authenticatedExternalWebService.setExternalWebService(externalWebService);
                SecurityContextHolder.getContext().setAuthentication(authenticatedExternalWebService);
                asynchService.syncResourceStatus(eventObject);
            }
        }
    }
}
