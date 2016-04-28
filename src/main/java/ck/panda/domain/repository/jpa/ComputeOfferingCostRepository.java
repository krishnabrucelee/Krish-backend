package ck.panda.domain.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.ComputeOfferingCost;

/**
 * ComputeOfferingRepository interface that extends PagingAndSortingRepository along with sorting and pagination.
 */
public interface ComputeOfferingCostRepository extends PagingAndSortingRepository<ComputeOfferingCost, Long> {

    /**
     * Find Computeoffering cost by total cost and compute offering id.
     *
     * @param computeId compute offering id.
     * @param totalCost of the compute offering.
     * @return compute offering cost.
     */
    @Query(value = "SELECT cost FROM ComputeOfferingCost cost WHERE cost.computeId= :computeId AND cost.totalCost = :totalcost")
    ComputeOfferingCost findByComputeAndTotalCost(@Param("computeId") Long computeId,
            @Param("totalcost") Double totalCost);

    /**
     *  Find Compute offering cost by id.
     *
     * @param computeId of the compute offering.
     * @return compute offering cost.
     */
    @Query(value = "SELECT cost FROM ComputeOfferingCost cost WHERE cost.computeId = :computeId")
    List<ComputeOfferingCost> findByComputeId(@Param("computeId") Long computeId);
}
