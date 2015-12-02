package ck.panda.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Pod;
import ck.panda.util.domain.CRUDService;

/**
 * Service interface for pod entity.
 *
 */
@Service
public interface PodService extends CRUDService<Pod> {

    /**
     * To get list of pods from cloudstack server.
     *
     * @return pod list from server
     * @throws Exception unhandled errors.
     */
    List<Pod> findAllFromCSServer() throws Exception;

    /**
     * To get pod from cloudstack server.
     *
     * @param uuid uuid of pod.
     * @return zone from server
     * @throws Exception unhandled errors.
     */
    Pod findByUUID(String uuid) throws Exception;

    /**
     * Soft delete for pod
     *
     * @param pod object
     * @return pod.
     * @throws Exception unhandled errors.
     */
	Pod softDelete(Pod pod) throws Exception;
}
