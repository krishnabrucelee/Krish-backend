package ck.panda.service;

import java.util.List;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Application;
import ck.panda.domain.entity.Department;
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
	JSONObject getInfrastructure() throws Exception;
    
    /**
     * Resource limit domain.
     * 
     * @return domain resource limit.
     * @throws Exception if error.
     */
    List<ResourceLimitDomain> findByDomainQuota() throws Exception;
    
    /**
     * Find all the departments by domain.
     * 
     * @return list of departments
     * @throws Exception if error.
     */
    List<Department> findAllDepartmentByDomain() throws Exception;
    
    /**
     * Find all the applications by domain.
     * 
     * @return list of applicaitons
     * @throws Exception if error.
     */
    List<Application> findAllApplicationByDomain() throws Exception;
}
