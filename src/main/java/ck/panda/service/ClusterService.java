package ck.panda.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Cluster;
import ck.panda.util.domain.CRUDService;

/**
 * Service interface for cluster entity.
 *
 */
@Service
public interface ClusterService extends CRUDService<Cluster> {

    /**
     * To get list of clusters from cloudstack server.
     *
     * @return cluster list from server
     * @throws Exception unhandled errors.
     */
    List<Cluster> findAllFromCSServer() throws Exception;

    /**
     * To get cluster from cloudstack server.
     *
     * @param uuid uuid of cluster.
     * @return zone from server
     * @throws Exception unhandled errors.
     */
    Cluster findByUUID(String uuid) throws Exception;
}

