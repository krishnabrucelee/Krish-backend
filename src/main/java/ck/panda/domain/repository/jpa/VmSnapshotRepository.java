package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ck.panda.domain.entity.VmSnapshot;

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
    List<VmSnapshot> findByVmInstance(@Param("vmId")Long vmId, @Param("isRemoved")Boolean isRemoved);


    /**
     * Find all vm snapshot by active.
     *
     * @param pageable paging and sorting.
     * @param isRemoved check whether removed or not.
     * @return Vm snapshot.
     */
    @Query(value = "select snapshot from VmSnapshot snapshot where snapshot.isRemoved IS :isRemoved )")
    Page<VmSnapshot> findAllByActive(Pageable pageable, @Param("isRemoved") Boolean isRemoved);

}
