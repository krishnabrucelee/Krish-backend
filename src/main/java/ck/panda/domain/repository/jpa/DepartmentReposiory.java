package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Domain;

/**
 * JPA Repository for Department entity.
 */
@Repository
public interface DepartmentReposiory extends PagingAndSortingRepository<Department, Long> {

    /**
     * Find the department already exist for the same domain.
     *
     * @param name of the department
     * @return department name
     */
    @Query(value = "select dpt from Department dpt where dpt.isActive IS TRUE AND dpt.userName=:name AND dpt.domain=:domain AND dpt.id!=:departmentId)")
    Department findByNameAndDomain(@Param("name") String name, @Param("domain") Domain domain, @Param("departmentId") Long departmentId);

    /**
     * find all the departmen with active status.
     *
     * @param pageable
     * @return
     */
    @Query(value = "select dpt from Department dpt where dpt.isActive IS TRUE")
    Page<Department> findAllByActive(Pageable pageable);

    /**
     * find all the department with active status with query.
     * @param query
     * @return
     */
    @Query(value = "select dept from Department dept where dept.isActive IS TRUE AND lower(dept.userName) LIKE '%' || lower(:query) || '%' ")
    List<Department> findAllByActive(@Param("query") String query);

    /**
     * find all the departmen with active status.
     *
     * @return
     */
    @Query(value = "select dpt from Department dpt where dpt.isActive IS TRUE")
    List<Department> findAllByActive();

    /**
     * find the department by uuid.
     * @param uuid
     * @return Department
     */
    @Query(value = "select dpt from Department dpt where dpt.isActive IS TRUE AND dpt.uuid=:uuid)")
    Department findByUuid(@Param("uuid") String uuid);

}
