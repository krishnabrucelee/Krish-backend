package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Project;

/**
 * JPA repository for Project entity. Project related crud and pagination are handled by this Repository.
 */
public interface ProjectRepository extends PagingAndSortingRepository<Project, Long> {
    /**
     * Find the project already exist for the same department.
     *
     * @param name project name.
     * @param department department object.
     * @param isActive true/false.
     * @param projectId project id.
     * @return project.
     */
    @Query(value = "SELECT project FROM Project project WHERE project.isActive IS :isactive AND project.name = :name AND project.department = :department AND project.id <> :projectId)")
    Project findByNameAndDepartment(@Param("isactive") Boolean isActive, @Param("name") String name,
            @Param("department") Department department, @Param("projectId") Long projectId);

    /**
     * Find all project by status.
     *
     * @param pageable pagination information.
     * @param isActive true/false.
     * @return list of project.
     */
    @Query(value = "SELECT project FROM Project project LEFT JOIN project.projectOwner WHERE project.isActive IS :isactive ")
    Page<Project> findAllByStatus(Pageable pageable, @Param("isactive") Boolean isActive);

    /**
     * Find all project by status.
     *
     * @param isActive true/false.
     * @return list of project.
     */
    @Query(value = "SELECT project FROM Project project WHERE project.isActive IS :isactive ")
    List<Project> findAllByIsActive(@Param("isactive") Boolean isActive);

    /**
     * Find all project by domain and status.
     *
     * @param domainId domain id.
     * @param isActive true/false.
     * @return list of project.
     */
    @Query(value = "SELECT project FROM Project project WHERE project.isActive IS :isactive AND project.domainId = :domain")
    List<Project> findAllByDomain(@Param("domain") Long domainId, @Param("isactive") Boolean isActive);

    /**
     * Find all project using Uuid.
     *
     * @param uuid of the project.
     * @return Project.
     */
    @Query(value = "SELECT project FROM Project project WHERE  project.uuid = :uuid")
    Project findByUuid(@Param("uuid") String uuid);

    /**
     * Find all project by department and status.
     *
     * @param departmentId department id.
     * @param isActive active/inactive status.
     * @return list of project.
     */
    @Query(value = "SELECT project FROM Project project WHERE project.isActive = :isActive AND project.departmentId = :departmentId")
    List<Project> findAllByDepartmentAndIsActive(@Param("departmentId") Long departmentId,
            @Param("isActive") Boolean isActive);

    /**
     * Find all project by user ans status.
     *
     * @param userId department id.
     * @param isActive active/inactive status.
     * @return list of project.
     */
    @Query(value = "SELECT DISTINCT project FROM Project project JOIN project.userList users WHERE project.isActive = :isActive AND users.id = :id")
    List<Project> findAllByUserAndIsActive(@Param("id") Long userId, @Param("isActive") Boolean isActive);

    /**
     * Find all project by domain with status.
     *
     * @param pageable pagination information.
     * @param domainId domain id.
     * @param isActive true/false.
     * @return list of project.
     */
    @Query(value = "SELECT project FROM Project project LEFT JOIN project.projectOwner WHERE project.isActive IS :isActive AND project.domainId = :domainId ")
    Page<Project> findAllByDomain(@Param("domainId") Long domainId, Pageable pageable,
            @Param("isActive") Boolean isActive);

    /**
     * Find all domain based project with active status.
     *
     * @param domainId domain id of the project
     * @param pageable pagination information.
     * @param isActive true/false.
     * @return list of project.
     */
    @Query(value = "SELECT project FROM Project project LEFT JOIN project.projectOwner WHERE project.domainId = :domainId AND project.isActive IS :isactive")
    Page<Project> findAllByDomainIdAndIsActive(@Param("domainId") Long domainId, @Param("isactive") Boolean isActive, Pageable pageable);

    /**
     * Find all project by department and status.
     *
     * @param departmentId department id.
     * @param isActive active/inactive status.
     * @param pageable pagination information.
     * @return list of project.
     */
    @Query(value = "SELECT project FROM Project project LEFT JOIN project.projectOwner WHERE project.isActive = :isActive AND project.departmentId = :departmentId")
    Page<Project> findAllByDepartmentAndIsActiveAndPage(@Param("departmentId") Long departmentId,
            @Param("isActive") Boolean isActive, Pageable pageable);
}
