package ck.panda.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Snapshot;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;

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


    Snapshot createVolume(Snapshot snapshot, Long userId) throws Exception;

    Snapshot findById(Long id);
}
