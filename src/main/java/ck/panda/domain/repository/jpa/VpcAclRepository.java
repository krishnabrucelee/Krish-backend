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
    @Query(value = "SELECT vpcAcl FROM VpcAcl vpcAcl WHERE (vpcAcl.vpcId = :vpcId OR vpcAcl.vpcId IS NULL) AND vpcAcl.isActive = :isActive ")
    List<VpcAcl> findByVpcIdAndIsActive(@Param("vpcId") Long vpcId, @Param("isActive") Boolean isActive);

    /**
     * Find all vpc acl by active.
     *
     * @param isActive true / false
     * @return vpc acl
     * @throws Exception exception
     */
    @Query(value = "SELECT vpcAcl FROM VpcAcl vpcAcl WHERE vpcAcl.isActive = :isActive")
    List<VpcAcl> findAllByIsActive(@Param("isActive") Boolean isActive);

    /**
     * Find vpc acl by uuid.
     *
     * @param uuid unique id of acl
     * @return vpc acl
     * @throws Exception exception
     */
    @Query(value = "SELECT vpcAcl FROM VpcAcl vpcAcl WHERE vpcAcl.uuid = :uuid AND vpcAcl.isActive = :isActive")
    VpcAcl findByUuid(@Param("uuid") String uuid, @Param("isActive") Boolean isActive);

    /**
     * Find Vpc acl by uuid.
     *
     * @param uuid Vpc acl uuid.
     * @return uuid
     */
    @Query(value = "SELECT vpcAcl FROM VpcAcl vpcAcl WHERE vpcAcl.uuid LIKE :uuid ")
    VpcAcl findByUUID(@Param("uuid") String uuid);

}
