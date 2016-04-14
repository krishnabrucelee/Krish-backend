package ck.panda.service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Event;

/**
 * Websocket service implementation.
 *
 */
@Service
public interface WebsocketService {

    /**
     * Save the event from async and action listener.
     *
     * @param event event object.
     * @param eventObject event object.
     * @throws Exception unhandled error.
     */
    void handleEventAction(Event event, JSONObject eventObject) throws Exception;
}
