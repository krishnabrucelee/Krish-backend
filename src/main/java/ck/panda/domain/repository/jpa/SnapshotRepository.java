package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Snapshot;

/**
 * Jpa Repository for Snapshot entity.
 *
 */
@Service
public interface SnapshotRepository extends PagingAndSortingRepository<Snapshot, Long> {

    /**
     * Find all the active or inactive snapshots with pagination.
     *
     * @param pageable to get the list with pagination.
     * @param isActive get the snapshot list based on active/inactive status.
     * @return list of snapshots.
     */
    @Query(value = "select snap from Snapshot snap where snap.isActive =:isActive")
    Page<Snapshot> findAllByIsActive(Pageable pageable, @Param("isActive") Boolean isActive);

    /**
     * Find the snapshot for same domain with username and is active status.
     *
     * @param name of the snapshot.
     * @param isActive get the snapshot list based on active/inactive status.
     * @return snapshot name.
     */
    @Query(value = "select snap from Snapshot snap where snap.name=:name AND snap.isActive =:isActive")
    Snapshot findByNameAndIsActive(@Param("name") String name, @Param("isActive") Boolean isActive);

    /**
     * Find Snapshot by uuid.
     *
     * @param uuid snapshot uuid.
     * @return uuid of the snapshot.
     */
    @Query(value = "SELECT snap FROM Snapshot snap WHERE snap.uuid LIKE :uuid ")
    Snapshot findByUUID(@Param("uuid") String uuid);

    /**
     * Get active snapshot list.
     *
     * @param isActive get the snapshot list based on active/inactive status.
     * @return Vm snapshot.
     */
    @Query(value = "select snap from Snapshot snap where snap.policyIsActive = :isActive")
    List<Snapshot> findAllByIsActive(@Param("isActive") Boolean isActive);

    /**
     * Find all domain based vm snapshot without expunging status by active.
     *
     * @param domainId domain id of the vm snapshot
     * @param isActive get the snapshot list based on active/inactive status.
     * @param pageable paging and sorting.
     * @param search search text
     * @return Vm snapshot.
     */
    @Query(value = "select snapshot from Snapshot snapshot where (snapshot.domainId =:domainId OR 0L = :domainId) AND snapshot.isActive =:isActive AND (snapshot.name LIKE %:search% OR snapshot.volume.name LIKE %:search% "
            + "OR snapshot.createdDateTime LIKE %:search% )")
    Page<Snapshot> findAllByDomainIdAndIsActiveSearchText(@Param("domainId") Long domainId, @Param("isActive") Boolean isActive, Pageable pageable, @Param("search") String search);

}
