package ck.panda.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.VmSnapshot;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.domain.vo.PagingAndSorting;

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

    /**
     * Find vm snapshot by domain id.
     *
     * @param domainId domain id of the vm snapshot
     * @param pagingAndSorting parameters
     * @return Vm snapshot
     * @throws Exception if error occurs
     */
    Page<VmSnapshot> findAllByDomainId(Long domainId, PagingAndSorting pagingAndSorting) throws Exception;

    /**
     * Find all snapshot by is active status.
     *
     * @param pagingAndSorting for pagination.
     * @param userId of the user.
     * @return vm snapshots.
     * @throws Exception if error occurs.
     */
    Page<VmSnapshot> findAllByActive(PagingAndSorting pagingAndSorting, Long userId) throws Exception;

    /**
     * Find all snapshots by search text and domain id.
     *
     * @param domainId of the domain.
     * @param pagingAndSorting for pagination.
     * @param searchText for snaphsot.
     * @return vm snapshot.
     * @throws Exception if error occurs.
     */
    Page<VmSnapshot> findAllByDomainIdAndSearchText(Long domainId, PagingAndSorting pagingAndSorting, String searchText)
            throws Exception;
}
