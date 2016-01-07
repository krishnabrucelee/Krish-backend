package ck.panda.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.VmSnapshot;
import ck.panda.util.domain.CRUDService;

/**
 * Service class for Virtual Machine Snapshot. This service provides Take VM snapshot, Restore Snapshot related actions.
 */
@Service
public interface VmSnapshotService extends CRUDService<VmSnapshot> {
    /**
     * Find vm snapshot by uuid.
     *
     * @param uuid instance uuid.
     * @return VmSnapshot.
     */
    VmSnapshot findByUUID(String uuid);

    /**
     * VM snapshot related events are handled.
     *
     * @param snapshotId snapshot Id.
     * @param event event message.
     * @return VmSnapshot.
     * @throws Exception unhandled errors.
     */
    VmSnapshot vmSnapshotEventHandle(String snapshotId, String event) throws Exception;

    /**
     * To get list of vm snapshot from cloudstack server.
     *
     * @return snapshot list from server
     * @throws Exception unhandled errors.
     */
    List<VmSnapshot> findAllFromCSServer() throws Exception;

    /**
     * Find vm snapshot by instance.
     *
     * @param vmId instance id.
     * @param isRemoved check whether removed or not.
     * @return Vm snapshot.
     */
    List<VmSnapshot> findByVmInstance(Long vmId, Boolean isRemoved);

}
