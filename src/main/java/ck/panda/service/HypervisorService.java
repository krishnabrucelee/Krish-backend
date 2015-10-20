package ck.panda.service;

import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Hypervisor;
import ck.panda.util.domain.CRUDService;

/**
 * Service interface for Hypervisor.
 * This service provides basic list and save business actions.
 */
@Service
public interface HypervisorService extends CRUDService<Hypervisor> {

}
