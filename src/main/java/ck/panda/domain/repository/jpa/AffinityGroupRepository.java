package ck.panda.domain.repository.jpa;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.AffinityGroup;

/**
 * JPA Repository for affinity group.
 */
public interface AffinityGroupRepository extends PagingAndSortingRepository<AffinityGroup, Long> {

    /**
     * Get affinity group by status.
     *
     * @param isActive is active
     * @param pageable pagination object
     * @return affinity group
     */
    @Query(value = "SELECT affinityGroup FROM AffinityGroup affinityGroup WHERE affinityGroup.isActive =:isActive")
    Page<AffinityGroup> findByStatus(@Param("isActive") Boolean isActive, Pageable pageable);

    /**
     * Get affinity group by department id.
     *
     * @param departmentId department id
     * @param isActive is active
     * @return affinity group list
     */
    @Query(value = "SELECT affinityGroup FROM AffinityGroup affinityGroup WHERE affinityGroup.departmentId =:departmentId AND affinityGroup.isActive =:isActive")
    List<AffinityGroup> findByDepartment(@Param("departmentId") Long departmentId, @Param("isActive") Boolean isActive);

    /**
     * Get affinity group by department with pagination.
     *
     * @param departmentId department id
     * @param isActive is active
     * @param pageable pagination object
     * @return affinity group
     */
    @Query(value = "SELECT affinityGroup FROM AffinityGroup affinityGroup WHERE affinityGroup.departmentId =:departmentId AND affinityGroup.isActive =:isActive")
    Page<AffinityGroup> findByDepartmentAndPageable(@Param("departmentId") Long departmentId, @Param("isActive") Boolean isActive, Pageable pageable);

    /**
     * Get affinity group by domain with pagination.
     *
     * @param domainId domain id
     * @param isActive is active
     * @param pageable pagination object
     * @return affinity group
     */
    @Query(value = "SELECT affinityGroup FROM AffinityGroup affinityGroup WHERE affinityGroup.domainId =:domainId AND affinityGroup.isActive =:isActive")
    Page<AffinityGroup> findByDomainAndPageable(@Param("domainId") Long domainId, @Param("isActive") Boolean isActive, Pageable pageable);

    /**
     * Get affinity group by domain search with pagination.
     *
     * @param domainId domain id
     * @param isActive is active
     * @param searchText search text
     * @param pageable pagination object
     * @return affinity group
     */
    @Query(value = "SELECT affinityGroup FROM AffinityGroup affinityGroup WHERE (affinityGroup.domainId =:domainId OR 0 = :domainId) AND (affinityGroup.name LIKE %:search% OR affinityGroup.department.userName LIKE %:search% OR affinityGroup.domain.name LIKE %:search%"
            + " OR affinityGroup.affinityGroupType.type LIKE %:search%) AND affinityGroup.isActive =:isActive")
    Page<AffinityGroup> findByDomainSearchAndPageable(@Param("domainId") Long domainId, @Param("isActive") Boolean isActive, @Param("search") String searchText,
            Pageable pageable);
}
