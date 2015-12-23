package ck.panda.service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

/**
 * Synchronization of all the asynchronous data from the cloudStack.
 */
@Service
public interface AsynchronousJobService {

    /**
     * Sync with CloudStack server list.
     *
     * @param eventObject response json event object.
     * @throws Exception cloudstack unhandled errors
     */
    void syncResourceStatus(JSONObject eventObject) throws Exception;
}
