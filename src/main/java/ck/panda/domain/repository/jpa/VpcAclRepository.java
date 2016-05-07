package ck.panda.domain.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import ck.panda.domain.entity.VpcAcl;

/**
 * JPA repository for VPC ACL entity.
 */
public interface VpcAclRepository extends PagingAndSortingRepository<VpcAcl, Long> {

}
