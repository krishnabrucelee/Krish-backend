package ck.panda.domain.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.Snapshot;
import ck.panda.domain.entity.SnapshotPolicy;

/**
 * Jpa Repository for SnapshotPolicy entity.
 *
 */
@Service
public interface SnapshotPolicyRepository extends PagingAndSortingRepository<SnapshotPolicy, Long> {

    /**
     * Get the SnapshotPolicy based on the uuid.
     *
     * @param uuid of the SnapshotPolicy
     * @return SnapshotPolicy
     */
    @Query(value = "select policy from SnapshotPolicy policy where policy.uuid = :uuid")
    SnapshotPolicy findByUUID(@Param("uuid") String uuid);

    /**
     * Find by volume and is active status of snapshot policy.
     *
     * @param volumeId of the volume.
     * @param isActive status of the snapshot policy.
     * @return snapshot policy.
     * @throws Exception if error occurs.
     */
    @Query(value = "select policy from SnapshotPolicy policy where policy.volumeId = :volumeId AND policy.isActive = :isActive")
    List<SnapshotPolicy> findByVolumeAndIsActive(@Param("volumeId") Long volumeId, @Param("isActive") Boolean isActive);

}