package ck.panda.domain.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import ck.panda.domain.entity.VpcNetworkAcl;

/**
 * JPA repository for VPC Network Acl entity.
 */
public interface VpcNetworkAclRepository extends PagingAndSortingRepository<VpcNetworkAcl, Long> {

    /**
     * Get the list of VPC Network ACL.
     *
     * @return VPC ACL list.
     * @throws Exception unhandled errors.
     */
    @Query(value = "SELECT acl FROM VpcNetworkAcl acl WHERE acl.vpcAclId = :vpcAclId AND acl.isActive = :isActive")
    List<VpcNetworkAcl> findByAclIdAndIsActive(@Param("vpcAclId") Long vpcAclId, @Param("isActive") Boolean isActive);

}
