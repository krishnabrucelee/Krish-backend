package ck.panda.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.VpcNetworkAcl;
import ck.panda.util.domain.CRUDService;

/**
 * Service interface for VPC NETWORK ACL. This service provides basic list and save business actions.
 */
@Service
public interface VpcNetworkAclService extends CRUDService<VpcNetworkAcl> {

    /**
     * Get the list of VPC Network ACL.
     *
     * @return VPC ACL list.
     * @throws Exception unhandled errors.
     */
    List<VpcNetworkAcl> findByAclIdAndIsActive(Long aclId);

    /**
     * Add network acl for vpc.
     *
     * @param vpcAcl vpc acl
     * @param aclId vpc id
     * @return network acl
     * @throws Exception exception
     */
    VpcNetworkAcl addVpcAcl(VpcNetworkAcl vpcNetworkAcl, Long aclId) throws Exception;

    /**
     * Soft delete for Network acl.
     *
     * @param Network acl Network acl
     * @param id Network acl id
     * @throws Exception error
     */
    VpcNetworkAcl softDelete(VpcNetworkAcl vpcNetworkAcl) throws Exception;

    /**
     * Find all from cloud stack server.
     *
     * @return vpc Network Acl
     * @throws Exception error
     */
    List<VpcNetworkAcl> findAllFromCSServer() throws Exception;

}
