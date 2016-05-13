package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.VpcAcl;

/**
 * JPA repository for VPC ACL entity.
 */
public interface VpcAclRepository extends PagingAndSortingRepository<VpcAcl, Long> {

    /**
     * Get the vpc acl  based on the vpc id.
     *
     * @param vpcId of the vpc acl.
     * @param isActive true/false.
     * @return vpc acl.
     */
    @Query(value = "SELECT vpcAcl FROM VpcAcl vpcAcl WHERE (vpcAcl.vpcId = :vpcId OR vpcAcl.vpcId IS NULL) AND vpcAcl.isActive = :isActive")
    List<VpcAcl> findByVpcIdAndIsActive(@Param("vpcId") Long vpcId, @Param("isActive") Boolean isActive);

}
