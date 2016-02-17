package ck.panda.service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.Network;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.Volume;
import ck.panda.rabbitmq.util.ResponseEvent;
import ck.panda.util.error.Errors;
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

    /**
     * Update Resource For Volume Deletion
     * @param volume Volume
     * @return volume
     * @throws Exception cloudstack unhandled errors
     */
    Volume updateResourceForVolumeDeletion(Volume volume) throws Exception;

    /**
     * Update Resource For Upload Volume Deletion
     * @param volume Volume
     * @return volume
     * @throws Exception cloudstack unhandled errors
     */
    Volume updateResourceForUploadVolumeDeletion(Volume volume) throws Exception;

    /**
     * Update Resource For Network Deletion
     * @param network Network
     * @return network
     * @throws Exception cloudstack unhandled errors
     */
    Network updateResourceForNetworkDeletion(Network network) throws Exception;

    /**
     * Update Resource For VmInstance Deletion
     * @param vmInstance VmInstance
     * @return vmInstance
     * @throws Exception cloudstack unhandled errors
     */
    VmInstance updateResourceForVmDeletion(VmInstance vmInstance) throws Exception;


    /**
     * Update Resource For Ip Deletion
     * @param network Network
     * @return network
     * @throws Exception cloudstack unhandled errors
     */
    Network updateResourceForIpDeletion(Network network) throws Exception;

    VmInstance updateResourceForVmExpunging(VmInstance vmInstance) throws Exception;

    VmInstance updateResourceForVmDestroy(VmInstance vmInstance) throws Exception;

    VmInstance updateResourceForVmRestore(VmInstance vmInstance) throws Exception;

}
