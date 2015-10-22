package ck.panda.service;

import java.util.List;

import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Hypervisor;
import ck.panda.util.domain.CRUDService;

/**
 * Service interface for Hypervisor.
 * This service provides basic list and save business actions.
 */
@Service
public interface HypervisorService extends CRUDService<Hypervisor> {

    /**
     * To get list of hypervisor from cloudstack server.
     *
     * @return hypervisor list from server
     * @throws Exception unhandled errors.
     */
    List<Hypervisor> findAllFromCSServer() throws Exception;
}
