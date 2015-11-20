package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Project;
import ck.panda.domain.entity.Project.Status;

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
     * @return project name
     */
    @Query(value = "select project from Project project where project.isActive IS :isactive AND project.name=:name AND project.department=:department AND project.id!=:projectId)")
    Project findByNameAndDepartment(@Param("isactive") Boolean isActive, @Param("name") String name,
            @Param("department") Department department, @Param("projectId") Long projectId);

    /**
     * find all the project with active status.
     *
     * @param pageable pagination information.
     * @param isActive true/false.
     * @param status of project.
     * @return list of project.
     */
    @Query(value = "select project from Project project where project.isActive IS :isactive and project.status = :status ")
    Page<Project> findAllByActive(Pageable pageable, @Param("isactive") Boolean isActive,
            @Param("status") Status status);

    /**
     * find all the project with active status.
     *
     * @param isActive true/false.
     * @return list of active project.
     */
    @Query(value = "select project from Project project where project.isActive IS :isactive ")
    List<Project> findAllByActive(@Param("isactive") Boolean isActive);

    /**
     * Find all the project with isActive and domain Id.
     *
     * @param id of the domain.
     * @return Domain.
     */
    @Query(value = "select project from Project project where project.isActive IS TRUE and project.domainId = :domain")
    List<Project> findbyDomain(@Param("domain")Long id);

    /**
     * Find all the project using Uuid and IsActive Status.
     *
     * @param uuid of the project.
     * @param isActive get Status of the project.
     * @return Project.
     */
    @Query(value = "select project from Project project where project.isActive =:isActive AND project.uuid =:uuid")
    Project findByUuidAndIsActive(@Param("uuid") String uuid, @Param("isActive") Boolean isActive);
}
