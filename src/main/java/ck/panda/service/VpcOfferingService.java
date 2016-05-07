package ck.panda.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.VpcOffering;
import ck.panda.util.domain.CRUDService;

/**
 * Service interface for VPC offering. This service provides basic list and save business actions.
 */
@Service
public interface VpcOfferingService extends CRUDService<VpcOffering> {

    /**
     * To get list of VPC offering from cloudstack server.
     *
     * @return VPC offering list from server
     * @throws Exception unhandled errors.
     */
    List<VpcOffering> findAllFromCSServer() throws Exception;

    /**
     * To get vpc offering by uuid.
     *
     * @return VPC offering from server
     * @throws Exception unhandled errors.
     */
    VpcOffering findByUUID(String uuid) throws Exception;

}
