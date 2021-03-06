package ck.panda.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Snapshot;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Service class for Snapshot. This service provides basic CRUD and essential api's for snapshot actions.
 *
 */
@Service
public interface SnapshotService extends CRUDService<Snapshot> {

    /**
     * To get list of snapshots from cloudstack server.
     *
     * @return snapshot list from server
     * @throws Exception unhandled errors.
     */
    List<Snapshot> findAllFromCSServer() throws Exception;

    /**
     * Soft delete for snapshot.
     *
     * @param snapshot get snapshot id.
     * @return deleted snapshot id.
     * @throws Exception unhandled errors.
     */
    Snapshot softDelete(Snapshot snapshot) throws Exception;

    /**
     * Paging and Sorting for displaying more number of elements in list.
     *
     * @param pagingAndSorting sortable method.
     * @return sorted values.
     * @throws Exception unhandled errors.
     */
    Page<Snapshot> findAllByActive(PagingAndSorting pagingAndSorting) throws Exception;

    /**
     * Find snapshot by Uuid
     *
     * @param uuid of the snapshot.
     * @return snapshot.
     * @throws Exception if error occurs.
     */
    Snapshot findByUUID(String uuid) throws Exception;

    /**
     * Create volume from snapshot.
     *
     * @param snapshot object which is used to create volume.
     * @param userId id of the user.
     * @return snapshot with created volume.
     * @throws Exception if error occurs.
     */
    Snapshot createVolume(Snapshot snapshot, Long userId) throws Exception;

    /**
     * Find snapshot by id.
     *
     * @param id of the snapshot.
     * @return snapshot.
     */
    Snapshot findById(Long id);

    /**
     * Revert snapshot to its inital state.
     *
     * @param snapshot to be reverted
     * @return snapshot.
     * @throws Exception if error occurs.
     */
    Snapshot revertSnapshot(Snapshot snapshot) throws Exception;

    /**
     * Find all snapshot by active .
     *
     * @param isActive status of the snapshot.
     * @return snapshot.
     * @throws Exception if error occurs.
     */
    List<Snapshot> findAllByActive(Boolean isActive) throws Exception;

    /**
     * Find all snapshots by search text and domain id.
     *
     * @param domainId of the domain.
     * @param pagingAndSorting for pagination.
     * @param searchText for snaphsot.
     * @return vm snapshot.
     * @throws Exception if error occurs.
     */
    Page<Snapshot> findAllByDomainIdAndSearchText(Long domainId, PagingAndSorting pagingAndSorting, String searchText)
            throws Exception;
}
