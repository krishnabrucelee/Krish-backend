package ck.panda.domain.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import ck.panda.domain.entity.VpcOffering;

/**
 * JPA repository for VPC offering entity.
 */
public interface VpcOfferingRepository extends PagingAndSortingRepository<VpcOffering, Long> {

}
