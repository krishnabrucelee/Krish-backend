package ck.panda.service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import ck.panda.rabbitmq.util.ResponseEvent;
import ck.panda.util.error.exception.ApplicationException;

/**
 * Synchronization of all the asynchronous data from the cloudStack.
 */
@Service
public interface AsynchronousJobService {

    /**
     * Sync with CloudStack server list via Asynchronous Job.
     *
     * @param eventObject response json event object.
     * @throws Exception cloudstack unhandled errors
     */
    void syncResourceStatus(JSONObject eventObject) throws Exception;

    /**
     * Sync with CloudStack server vm update via action listener Job.
     *
     * @param uuid vm uuid.
     * @throws Exception cloudstack unhandled errors
     */
    void syncVMUpdate(String uuid) throws Exception;

    /**
     * Sync with CloudStack server Network offering.
     *
     * @param eventObject network offering response event
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors
     */
    void asyncNetworkOffering(ResponseEvent eventObject) throws ApplicationException, Exception;

    /**
     * Sync with CloudStack server volume.
     *
     * @param eventObject volume response event
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors
     */
    void asyncVolume(ResponseEvent eventObject) throws ApplicationException, Exception;

}
