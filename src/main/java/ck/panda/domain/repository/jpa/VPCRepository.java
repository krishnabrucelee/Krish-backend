package ck.panda.domain.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import ck.panda.domain.entity.VPC;

/**
 * JPA repository for VPC entity.
 */
public interface VPCRepository extends PagingAndSortingRepository<VPC, Long> {

}
