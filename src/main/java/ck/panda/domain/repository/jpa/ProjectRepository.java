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
 * JPA repository for Project entity.
 * Project related crud and pagination are handled by this Repository.
 */
public interface ProjectRepository extends PagingAndSortingRepository<Project, Long> {

    /**
     * Find the project already exist for the same department.
     *
     * @param name of the project
     * @return project name
     */
    @Query(value = "select prjct from Project prjct where prjct.isActive IS TRUE AND prjct.name=:name AND prjct.department=:department AND prjct.id!=:projectId)")
    Project findByNameAndDepartment(@Param("name") String name, @Param("department") Department department, @Param("projectId") Long projectId);

    /**
     * find all the project with active status.
     *
     * @param pageable
     * @return
     */
    @Query(value = "select prjct from Project prjct where prjct.isActive IS TRUE")
    Page<Project> findAllByActive(Pageable pageable);

    /**
     * find all the department with active status with query.
     *
     * @param query
     * @return
     */
    @Query(value = "select prrjct from Project prrjct where prrjct.isActive IS TRUE AND lower(prrjct.name) LIKE '%' || lower(:query) || '%' ")
    List<Project> findByName(@Param("query") String query);

    /**
     * find all the project with active status.
     *
     * @return
     */
    @Query(value = "select prjct from Project prjct where prjct.isActive IS TRUE")
    List<Project> findAllByActive();

}
