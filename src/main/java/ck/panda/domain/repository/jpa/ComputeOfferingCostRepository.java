package ck.panda.domain.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import ck.panda.domain.entity.ComputeOfferingCost;

/**
 * ComputeOfferingRepository interface that extends PagingAndSortingRepository along with sorting and pagination.
 */
public interface ComputeOfferingCostRepository extends PagingAndSortingRepository<ComputeOfferingCost, Long> {

}
