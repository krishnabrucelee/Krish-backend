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

}
