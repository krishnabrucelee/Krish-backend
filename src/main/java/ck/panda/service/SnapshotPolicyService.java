package ck.panda.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.SnapshotPolicy;
import ck.panda.util.domain.CRUDService;

/**
 * Service interface for pod entity.
 *
 */
@Service
public interface SnapshotPolicyService extends CRUDService<SnapshotPolicy> {

    /**
     * To get list of pods from cloudstack server.
     *
     * @return pod list from server
     * @throws Exception unhandled errors.
     */
    List<SnapshotPolicy> findAllFromCSServer() throws Exception;

    /**
     * To get pod from cloudstack server.
     *
     * @param uuid uuid of pod.
     * @return zone from server
     * @throws Exception unhandled errors.
     */
    SnapshotPolicy findByUUID(String uuid) throws Exception;

    /**
     * Soft delete for pod.
     *
     * @param pod object
     * @return pod.
     * @throws Exception unhandled errors.
     */
      SnapshotPolicy softDelete(Long id) throws Exception;

      /**
       * Find by volume and is active status of snapshot policy.
       *
       * @param volumeId of the volume.
       * @param isActive status of the snapshot policy.
       * @return snapshot policy.
       * @throws Exception if error occurs.
       */
     List<SnapshotPolicy> findAllByVolumeAndIsActive(Long volumeId, Boolean isActive) throws Exception;


}
