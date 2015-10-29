package ck.panda.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ck.panda.domain.entity.ComputeOffering;
import ck.panda.util.domain.CRUDService;

/**
 * Service class for Compute Offering.
 *
 */
@Service
public interface ComputeOfferingService  extends CRUDService<ComputeOffering>  {

    /**
     * To get list of compute offerings from cloudstack server.
     *
     * @return domain list from server
     * @throws Exception unhandled errors.
     */
    List<ComputeOffering> findAllFromCSServer() throws Exception;
}

