package ck.panda.domain.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import ck.panda.domain.entity.ComputeOffering;


/**
 *  ComputeOfferingRepository interface that extends PagingAndSortingRepository along with sorting
 *  and pagination.
 */
public interface ComputeOfferingRepository extends PagingAndSortingRepository<ComputeOffering, Long> {

}

