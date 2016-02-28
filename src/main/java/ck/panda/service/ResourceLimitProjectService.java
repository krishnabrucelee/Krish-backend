/**
 *
 */
package ck.panda.service;

import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.ResourceLimitDepartment;
import ck.panda.domain.entity.ResourceLimitProject;
import ck.panda.domain.entity.ResourceLimitProject.ResourceType;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.error.exception.ApplicationException;

/**
 * Resource Limit Project Service.
 */
@Service
public interface ResourceLimitProjectService extends CRUDService<ResourceLimitProject> {

    /**
     * Create the quota limit for Project.
     *
     * @param resourceLimits resource limits
     * @return created resource limits
     * @throws Exception error.
     */
    List<ResourceLimitProject> createResourceLimits(List<ResourceLimitProject> resourceLimits) throws Exception;

    /**
     * Find the quota limit for Project.
     *
     * @param projectId project id.
     * @param isActive true/false
     * @return project
     * @throws Exception error
     * @throws ApplicationException application error
     */
    List<ResourceLimitProject> findAllByProjectIdAndIsActive(Long projectId, Boolean isActive) throws ApplicationException, Exception;

    /**
     * Find by resource count by project and resourceType.
     *
     * @param departmentId project id.
     * @param isActive true/false
     * @param resourceType resource type.
     * @param projectId project id.
     * @return department resource count.
     * @throws Exception error
     */
    Long findByResourceCountByProjectAndResourceType(Long departmentId, ResourceType resourceType,
            Long projectId, Boolean isActive) throws Exception;

    /**
     * To get list of Volume from cloudstack server.
     *
     * @param projectId project id.
     * @return ResourceLimit list from server
     * @throws Exception unhandled errors.
     */
    List<ResourceLimitProject> findAllFromCSServerProject(String projectId) throws Exception;

    /**
     * Find resourceType by project.
     *
     * @param projectId project
     * @param isActive true/false
     * @param resourceType resource type.
     * @throws Exception error
     */
    ResourceLimitProject findByProjectAndResourceType(Long projectId,
            ResourceLimitProject.ResourceType resourceType, Boolean isActive) throws Exception;

    void deleteResourceLimitByProject(Long projectId);

    /**
     * Find resource by project and resourceType.
     *
     * @param projectId project id.
     * @param isActive true/false
     * @param resourceType resource type.
     * @return project resource count.
     * @throws Exception error
     */
    ResourceLimitProject findResourceByProjectAndResourceType(Long projectId,
            ResourceType resourceType, Boolean isActive) throws Exception;

    /**
     * Find the quota limit for Project.
     *
     * @param projectId project id.
     * @param isActive true/false
     * @return project quota
     * @throws Exception error
     * @throws ApplicationException application error
     */
    List<ResourceLimitProject> findAllByProjectAndIsActive(Long projectId, Boolean isActive) throws ApplicationException, Exception;

    /**
     * @param domainId
     * @return
     */
    HashMap<String, String> getResourceLimitsOfProject(Long domainId);

    /**
     * @param departmentId
     * @return
     */
    HashMap<String, String> getResourceLimitsOfDepartment(Long departmentId);

}
