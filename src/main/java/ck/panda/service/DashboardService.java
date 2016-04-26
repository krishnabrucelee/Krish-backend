package ck.panda.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import ck.panda.domain.entity.ResourceLimitDomain;

/**
 * Service class for Dashboard. This service provides basic CRUD and essential api's for Domain related business actions.
 *
 */
@Service
public interface DashboardService {

	/**
	 * Get the infrastructure.
	 * 
	 * @return infrastructure response.
	 * @throws Exception if error.
	 */
	HashMap<String, Integer> getInfrastructure() throws Exception;
    
    
    /**
     * Resource limit domain.
     * 
     * @return domain resource limit.
     * @throws Exception if error.
     */
    List<ResourceLimitDomain> findByDomainQuota() throws Exception;

}
