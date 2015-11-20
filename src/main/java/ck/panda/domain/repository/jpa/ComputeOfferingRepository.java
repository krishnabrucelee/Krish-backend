package ck.panda.domain.repository.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.ComputeOffering;

/**
 * ComputeOfferingRepository interface that extends PagingAndSortingRepository along with sorting and
 * pagination.
 */
public interface ComputeOfferingRepository extends PagingAndSortingRepository<ComputeOffering, Long> {
    /**
     * Get the compute offer based on the uuid.
     *
     * @param uuid of the compute offer.
     * @return compute offer
     */
    @Query(value = "select compute from ComputeOffering compute where compute.uuid = :uuid")
    ComputeOffering findByUUID(@Param("uuid") String uuid);

    /**
     * Find all the active or inactive snapshots with pagination.
     *
     * @param pageable to get the list with pagination.
     * @param isActive get the snapshot list based on active/inactive status.
     * @return list of snapshots.
     */
    @Query(value = "select compute from ComputeOffering compute where compute.isActive =:isActive")
    Page<ComputeOffering> findAllByIsActive(Pageable pageable, @Param("isActive") Boolean isActive);
}
