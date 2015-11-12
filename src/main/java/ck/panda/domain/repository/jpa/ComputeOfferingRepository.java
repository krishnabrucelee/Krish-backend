package ck.panda.domain.repository.jpa;

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
}
