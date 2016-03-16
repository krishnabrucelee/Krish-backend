package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ck.panda.domain.entity.VmSnapshot;
import ck.panda.domain.entity.VmSnapshot.Status;

/**
 * Jpa Repository for VmSnapshot entity.
 */
@Repository
public interface VmSnapshotRepository extends PagingAndSortingRepository<VmSnapshot, Long> {
    /**
     * Find vm snapshot by uuid.
     *
     * @param uuid snapshot uuid.
     * @return Vm snapshot.
     */
    @Query(value = "select snapshot from VmSnapshot snapshot where snapshot.uuid LIKE :uuid ")
    VmSnapshot findByUUID(@Param("uuid") String uuid);

    /**
     * Find vm snapshot by instance.
     *
     * @param vmId instance id.
     * @param isRemoved check whether removed or not.
     * @return Vm snapshot.
     */
    @Query(value = "select snapshot from VmSnapshot snapshot where snapshot.vmId=:vmId AND snapshot.isRemoved IS :isRemoved )")
    List<VmSnapshot> findByVmInstance(@Param("vmId") Long vmId, @Param("isRemoved") Boolean isRemoved);

    /**
     * Find all vm snapshot without expunging status by active.
     *
     * @param pageable paging and sorting.
     * @param status of the snapshot
     * @param isRemoved check whether removed or not.
     * @return Vm snapshot.
     */
    @Query(value = "select snapshot from VmSnapshot snapshot LEFT JOIN snapshot.vm where snapshot.isRemoved IS :isRemoved AND snapshot.status <>:status)")
    Page<VmSnapshot> findAllByActiveAndExpunging(Pageable pageable, @Param("isRemoved") Boolean isRemoved, @Param("status") Status status);

    /**
     * Find all domain based vm snapshot without expunging status by active.
     *
     * @param domainId domain id of the vm snapshot
     * @param pageable paging and sorting.
     * @param isRemoved check whether removed or not.
     * @param status of the snapshot
     * @return Vm snapshot.
     */
    @Query(value = "select snapshot from VmSnapshot snapshot LEFT JOIN snapshot.vm where snapshot.domainId =:domainId AND snapshot.isRemoved IS :isRemoved AND snapshot.status <>:status)")
    Page<VmSnapshot> findAllByDomainIdAndIsActive(@Param("domainId") Long domainId, @Param("isRemoved") Boolean isRemoved, @Param("status") Status status, Pageable pageable);

}
