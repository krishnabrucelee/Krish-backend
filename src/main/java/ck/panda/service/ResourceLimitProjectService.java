/**
 *
 */
package ck.panda.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.ResourceLimitProject;
import ck.panda.util.domain.CRUDService;

/**
 * @author Assistanz
 *
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
      */
     List<ResourceLimitProject> findAllByProjectIdAndIsActive(Long projectId, Boolean isActive);

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
     Long findByResourceCountByProjectAndResourceType(Long departmentId, ResourceLimitProject.ResourceType resourceType, Long projectId, Boolean isActive) throws Exception;

    }
