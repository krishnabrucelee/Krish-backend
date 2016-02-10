package ck.panda.domain.repository.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.ComputeOfferingCost;

/**
 * ComputeOfferingRepository interface that extends PagingAndSortingRepository along with sorting and pagination.
 */
public interface ComputeOfferingCostRepository extends PagingAndSortingRepository<ComputeOfferingCost, Long> {

      /**
     * Find Network list by department.
     *
     * @param computeId compute offering id.
     * @param totalCost of the compute offering.
     * @return network list.
     */
    @Query(value = "SELECT cost FROM ComputeOfferingCost cost WHERE cost.computeId= :computeId AND cost.totalCost = :totalcost")
    ComputeOfferingCost findByComputeAndTotalCost(@Param("computeId") Long computeId,
            @Param("totalcost") Double totalCost);
}
