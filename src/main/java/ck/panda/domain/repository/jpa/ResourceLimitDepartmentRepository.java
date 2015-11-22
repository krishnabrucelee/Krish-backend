package ck.panda.domain.repository.jpa;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.ResourceLimitDepartment;
import ck.panda.domain.entity.ResourceLimitDepartment.ResourceType;

/**
 * Jpa Repository for ResourceLimit department entity.
 */
public interface ResourceLimitDepartmentRepository extends PagingAndSortingRepository<ResourceLimitDepartment, Long>  {

    /**
     * method to find list of entities having active status.
     *
     * @param pageable volume list page
     * @return lists Active state ResourceLimit
     */
    @Query(value = "select resource from ResourceLimitDepartment resource where resource.isActive IS TRUE")
    Page<ResourceLimitDepartment> findAllByActive(Pageable pageable);

    /**
     * Find all the active resource limits based on the department id.
     * @param departmentId department id.
     * @param isActive true/false
     * @return department.
     */
    @Query(value = "select resource from ResourceLimitDepartment resource where resource.isActive =:isActive AND resource.departmentId =:departmentId")
    List<ResourceLimitDepartment> findAllByDepartmentIdAndIsActive(@Param("departmentId") Long departmentId, @Param("isActive") Boolean isActive);

    /**
     * Find by resource count by domain and resourceType.
     *
     * @param domainId domain id.
     * @param isActive true/false.
     * @param resourceType resource type.
     * @return resource count.
     */
    @Query(value = "select coalesce(sum(resource.max),0) from ResourceLimitDepartment resource where resource.isActive =:isActive AND resource.domainId =:domainId AND resource.resourceType =:resourceType AND resource.departmentId !=:departmentId ")
    Long findByResourceCountByDepartmentAndResourceType(@Param("domainId") Long domainId, @Param("resourceType") ResourceLimitDepartment.ResourceType resourceType, @Param("departmentId") Long departmentId,
            @Param("isActive") Boolean isActive);

    /**
     * Find all the active resource limits based on the domain id.
     *
     * @param departmentId department id.
     * @param resourceType resource type
     * @param isActive true/false.
     * @return department resource type.
     */
    @Query(value = "select resource from ResourceLimitDepartment resource where resource.isActive =:isActive AND resource.departmentId =:departmentId AND resource.resourceType =:resourceType")
    ResourceLimitDepartment findByDepartmentAndResourceType(@Param("departmentId") Long departmentId, @Param("resourceType") ResourceType resourceType, @Param("isActive") Boolean isActive);

}
