/**
 *
 */
package ck.panda.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.ResourceLimitDepartment;
import ck.panda.util.domain.CRUDService;

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
}
