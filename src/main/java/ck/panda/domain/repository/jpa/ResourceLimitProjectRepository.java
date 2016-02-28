package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import ck.panda.domain.entity.ResourceLimitDepartment;
import ck.panda.domain.entity.ResourceLimitProject;
import ck.panda.domain.entity.ResourceLimitProject.ResourceType;

/**
 * Jpa Repository for ResourceLimit project entity.
 */
public interface ResourceLimitProjectRepository extends PagingAndSortingRepository<ResourceLimitProject, Long> {

    /**
     * method to find list of entities having active status.
     *
     * @param pageable volume list page
     * @return lists Active state ResourceLimit
     */
    @Query(value = "select resource from ResourceLimitProject resource where resource.isActive IS TRUE")
    Page<ResourceLimitProject> findAllByActive(Pageable pageable);

    /**
     * Find all the active resource limits based on the project id.
     *
     * @param projectId project id.
     * @param isActive true/false
     * @return projects.
     */
    @Query(value = "select resource from ResourceLimitProject resource where resource.isActive =:isActive AND resource.projectId =:projectId")
    List<ResourceLimitProject> findAllByProjectIdAndIsActive(@Param("projectId") Long projectId,
            @Param("isActive") Boolean isActive);

    /**
     * Find by resource count by domain and resourceType.
     *
     * @param departmentId department id.
     * @param isActive true/false
     * @param resourceType resource type
     * @param projectId project id.
     * @return resource count of project.
     */
    @Query(value = "select coalesce(sum(resource.max),0) from ResourceLimitProject resource where resource.isActive =:isActive AND resource.departmentId =:departmentId AND resource.resourceType =:resourceType AND resource.projectId !=:projectId")
    Long findByResourceCountByProjectAndResourceType(@Param("departmentId") Long departmentId,
            @Param("resourceType") ResourceLimitProject.ResourceType resourceType, @Param("projectId") Long projectId,
            @Param("isActive") Boolean isActive);

    /**
     * Find all the active resource limits based on the project id.
     *
     * @param projectId project id.
     * @param resourceType resource type
     * @param isActive true/false.
     * @return project resource type.
     */
    @Query(value = "select resource from ResourceLimitProject resource where resource.isActive =:isActive AND resource.projectId =:projectId AND resource.resourceType =:resourceType")
    ResourceLimitProject findByProjectAndResourceType(@Param("projectId") Long projectId,
            @Param("resourceType") ResourceType resourceType, @Param("isActive") Boolean isActive);

    /**
     * Find resource by project and resourceType.
     *
     * @param projectId project id.
     * @param isActive true/false
     * @param resourceType resource type.
     * @return project resource count.
     * @throws Exception error
     */
    @Query(value = "select resource from ResourceLimitProject resource where resource.isActive =:isActive AND resource.projectId =:projectId AND resource.resourceType in :resourceType")
    ResourceLimitProject findResourceByProjectAndResourceType(@Param("projectId") Long projectId,
            @Param("resourceType") ResourceLimitProject.ResourceType resourceType, @Param("isActive") Boolean isActive);

    /**
     * @param domainId
     * @param resourceType
     * @param isActive
     * @return
     */
    @Query(value = "select sum(resource.max) from ResourceLimitProject resource where resource.isActive = :isActive AND resource.domainId = :domainId AND resource.resourceType = :resourceType ")
    Long findTotalCountOfResourceProject(@Param("domainId") Long domainId, @Param("resourceType") ResourceLimitProject.ResourceType resourceType, @Param("isActive") Boolean isActive);

    /**
     * @param domainId
     * @param resourceType
     * @param isActive
     * @return
     */
    @Query(value = "select sum(resource.max) from ResourceLimitProject resource where resource.isActive = :isActive AND resource.departmentId = :departmentId AND resource.resourceType = :resourceType ")
    Long findTotalCountOfResourceDepartment(@Param("departmentId") Long departmentId, @Param("resourceType") ResourceLimitProject.ResourceType resourceType, @Param("isActive") Boolean isActive);

}
