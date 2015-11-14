package ck.panda.domain.repository.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Cluster;

/**
 * Jpa Repository for Cluster entity.
 */
@Service
public interface ClusterRepository extends PagingAndSortingRepository<Cluster, Long> {
    /**
     * Find Cluster by uuid.
     *
     * @param uuid uuid of cluster.
     * @return cluster object.
     */
    @Query(value = "select cluster from Cluster cluster where cluster.uuid = :uuid")
    Cluster findByUUID(@Param("uuid") String uuid);
}
