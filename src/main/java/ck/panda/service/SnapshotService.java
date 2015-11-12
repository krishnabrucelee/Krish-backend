package ck.panda.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Snapshot;
import ck.panda.util.domain.CRUDService;

/**
 * Service class for Snapshot.
 * This service provides basic CRUD and essential api's for snapshot actions.
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
}
