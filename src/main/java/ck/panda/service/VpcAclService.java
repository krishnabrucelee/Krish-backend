package ck.panda.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.VpcAcl;
import ck.panda.util.domain.CRUDService;

/**
 * Service interface for VPC ACL. This service provides basic list and save business actions.
 */
@Service
public interface VpcAclService extends CRUDService<VpcAcl> {

    /**
     * To get list of VPC ACL from cloudstack server.
     *
     * @return VPC ACL list from server
     * @throws Exception unhandled errors.
     */
    List<VpcAcl> findAllFromCSServer() throws Exception;

    /**
     * Get the VPC ACL by id.
     *
     * @param id VPC ACL id
     * @return VPC ACL from server
     * @throws Exception unhandled errors.
     */
    VpcAcl findVpcAclById(Long id) throws Exception;

    /**
     * Add network acl for vpc.
     *
     * @param vpcAcl vpc acl
     * @param vpcId vpc id
     * @return network acl
     * @throws Exception exception
     */
    VpcAcl addVpcAcl(VpcAcl vpcAcl, Long vpcId) throws Exception;

    /**
     * List network acl list from vpc id.
     *
     * @param vpcId vpc id
     * @return network acl
     * @throws Exception exception
     */
    List<VpcAcl> findByVpcIdAndIsActive(Long vpcId) throws Exception;

    /**
     * Soft delete for vpc acl.
     *
     * @param vpcAcl vpc acl
     * @return vpc acl
     * @throws Exception exception
     */
    VpcAcl softDelete(VpcAcl vpcAcl) throws Exception;

    /**
     * Find all vpc acl by active.
     *
     * @param isActive true / false
     * @return vpc acl
     * @throws Exception exception
     */
    List<VpcAcl> findAllByIsActive(Boolean isActive) throws Exception;

    /**
     * Find vpc acl by uuid.
     *
     * @param uuid unique id of acl
     * @return vpc acl
     * @throws Exception exception
     */
    VpcAcl findbyUUID(String uuid) throws Exception;

    /**
     * To get vpc acl from cloudstack server.
     *
     * @param uuid network uuid.
     * @return vpc acl from server
     * @throws Exception unhandled errors.
     */
    VpcAcl findByUUID(String uuid) throws Exception;

}
