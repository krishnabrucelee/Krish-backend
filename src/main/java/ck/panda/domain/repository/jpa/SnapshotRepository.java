package ck.panda.domain.repository.jpa;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.Network;
import ck.panda.domain.entity.Nic;
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

    @Query(value = "select snap from Snapshot snap where snap.volumeId = :volumeId AND snap.isActive = :isActive")
    List<Snapshot> findByVolumeAndIsActive(@Param("volumeId") Long volumeId, @Param("isActive") Boolean isActive);

    @Query(value = "select snap from Snapshot snap where snap.isActive = :isActive")
    List<Snapshot> findAllByIsActive(@Param("isActive") Boolean isActive);

}
