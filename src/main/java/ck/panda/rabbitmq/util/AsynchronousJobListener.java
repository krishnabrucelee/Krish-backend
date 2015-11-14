package ck.panda.rabbitmq.util;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import ck.panda.service.SyncService;

/**
 * Asynchronous Job listener will listen and update resource data to our App DB
 * when an event handled directly in CS server.
 *
 */
public class AsynchronousJobListener implements MessageListener {
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
    public AsynchronousJobListener(SyncService syncService) {
        this.syncService = syncService;
    }

    @Override
    public void onMessage(Message message) {
        try {
            JSONObject instance = new JSONObject(new String(message.getBody())).getJSONObject("queryasyncjobresultresponse");
            this.handleStatusEvent(instance);
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
    public void handleStatusEvent(JSONObject eventObject) throws Exception {
        if(eventObject.has("jobresulttype")){
            if(eventObject.getJSONObject("jobresult").has("virtualmachine")){
                syncService.init();
                syncService.syncResourceStatus(eventObject.getJSONObject("jobresult").getJSONObject("virtualmachine"));
            }
        }
    }

}
