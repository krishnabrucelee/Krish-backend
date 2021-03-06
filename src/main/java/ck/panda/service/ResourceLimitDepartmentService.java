/**
 *
 */
package ck.panda.service;

import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.ResourceLimitDepartment;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.error.exception.ApplicationException;

/**
 * Resource Limit Department Service.
 */
@Service
public interface ResourceLimitDepartmentService extends CRUDService<ResourceLimitDepartment> {

    /**
     * Create the quota limit for Department.
     *
     * @param resourceLimits resource limits
     * @return created resource limits
     * @throws Exception error.
     */
    List<ResourceLimitDepartment> createResourceLimits(List<ResourceLimitDepartment> resourceLimits) throws Exception;

    /**
     * List resource limits based on the department id.
     *
     * @param id department id.
     * @param isActive true/false
     * @return department
     * @throws Exception error
     */
    List<ResourceLimitDepartment> findAllByDepartmentIdAndIsActive(Long id, Boolean isActive) throws Exception;

    /**
     * Find by resource count by domain and resourceType.
     *
     * @param domainId domain id.
     * @param isActive true/false
     * @param resourceType resource type.
     * @param departmentId department id.
     * @return department resource count.
     * @throws Exception error
     */
    Long findByResourceCountByDepartmentAndResourceType(Long domainId,
            ResourceLimitDepartment.ResourceType resourceType, Long departmentId, Boolean isActive) throws Exception;

    /**
     * Find resourceType by department.
     *
     * @param departmentId department
     * @param isActive true/false
     * @param resourceType resource type.
     * @return department resource count.
     * @throws Exception error
     */
    ResourceLimitDepartment findByDepartmentAndResourceType(Long departmentId,
            ResourceLimitDepartment.ResourceType resourceType, Boolean isActive);

    /**
     * Get resource limits of department.
     *
     * @param domainId domain id
     * @return max values of resources
     */
    HashMap<String, String> getResourceLimitsOfDepartment(Long domainId);

    /**
     * Get resource counts of department.
     *
     * @param departmentId department id
     * @return max values of resources
     */
    HashMap<String, String> getResourceCountsOfDepartment(Long departmentId);

    /**
     * Get resource counts of domain.
     *
     * @param domainId department id
     * @return max values of resources
     */
    HashMap<String, String> getResourceCountsOfDomain(Long domainId);

    /**
     * Gety the resource limits of projects.
     * @param projectId project id.
     * @return resource limits of project
     */
    HashMap<String, String> getResourceLimitsOfProject(Long projectId);

    /**
     * List all project max resource limits by department id.
     *
     * @param id department id
     * @return resource service
     * @throws Exception error
     */
    HashMap<String, Long> getSumOfDepartmentMin(Long id) throws Exception;

    HashMap<String, Long> getSumOfDepartmentMax(Long id) throws Exception;
}
