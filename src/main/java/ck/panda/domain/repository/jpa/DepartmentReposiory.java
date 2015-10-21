package ck.panda.domain.repository.jpa;

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
     * @param domain object to check duplication
     * @return department name
     */
    @Query(value = "select dpt from Department dpt where dpt.isActive IS FALSE AND dpt.name=:name AND dpt.domain=:domain")
    Department findByNameAndDomain(@Param("name") String name, @Param("domain") Domain domain);
}
