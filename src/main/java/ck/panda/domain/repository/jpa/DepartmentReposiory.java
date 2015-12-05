package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Department.AccountType;
import ck.panda.domain.entity.Domain;

/**
 * JPA Repository for Department entity.
 */
@Repository
public interface DepartmentReposiory extends PagingAndSortingRepository<Department, Long> {

    /**
     * Find the department for same domain with username and is active status.
     *
     * @param userName user name of the department.
     * @param domain Domain reference.
     * @param isActive get the department list based on active/inactive status.
     * @return department name.
     */
    @Query(value = "select dpt from Department dpt where dpt.userName=:userName AND  dpt.domain =:domain AND dpt.isActive =:isActive")
    Department findByNameAndDomainAndIsActive(@Param("userName") String userName, @Param("domain") Domain domain, @Param("isActive")  Boolean isActive);

    /**
     * Find all the active or inactive departments with pagination.
     *
     * @param pageable to get the list with pagination.
     * @param isActive get the department list based on active/inactive status.
     * @return list of departments.
     */
    @Query(value = "select dpt from Department dpt where dpt.isActive =:isActive")
    Page<Department> findAllByIsActive(Pageable pageable, @Param("isActive") Boolean isActive);

    /**
     * Find all the department with active status.
     *
     * @param isActive get the department list based on active/inactive status.
     * @return list of departments.
     */
    @Query(value = "select dpt from Department dpt where dpt.isActive =:isActive")
    List<Department> findAllByIsActive(@Param("isActive") Boolean isActive);

    /**
     * Find the department by uuid.
     *
     * @param uuid department uuid.
     * @param isActive get the department list based on active/inactive status.
     * @return Department.
     */
    @Query(value = "select dpt from Department dpt where dpt.isActive =:isActive AND dpt.uuid=:uuid)")
    Department findByUuidAndIsActive(@Param("uuid") String uuid, @Param("isActive") Boolean isActive);

    /**
     *
     * @param id domain id
     * @return department list
     */
    @Query(value = "select dpt from Department dpt where dpt.domainId=:domainId)")
    List<Department> findByDomain(@Param("domainId") Long id);

    /**
     * Find the department by Domain Id and IsActive.
     *
     * @param domainId for each domain.
     * @param isActive get the department list based on active/inactive status.
     * @return Department.
     */
    @Query(value = "select dpt from Department dpt where dpt.isActive =:isActive AND dpt.domainId=:domainId)")
    List<Department> findByDomainAndIsActive(@Param("domainId") Long domainId, @Param("isActive") Boolean isActive);

    /**
     * Find the department by username and isActive.
     *
     * @param userName for each users.
     * @param isActive get the department list based on active/inactive status.
     * @return Department.
     */
    @Query(value = "select dpt from Department dpt where dpt.isActive =:isActive AND dpt.userName=:userName)")
    Department findByUsername(@Param("userName")String userName, @Param("isActive") Boolean isActive);

    /**
     * Find the department by Domain Id and IsActive.
     *
     * @param domainId for each domain.
     * @param isActive get the department list based on active/inactive status.
     * @param pageable for pagination.
     * @return Department.
     */
    @Query(value = "select dpt from Department dpt where dpt.isActive =:isActive AND dpt.domainId=:domainId)")
    Page<Department> findByDomainAndIsActive(@Param("domainId") Long domainId, @Param("isActive") Boolean isActive, Pageable pageable);

    /**
     * Find the department list by account types and isActive.
     *
     * @param types for each department.
     * @param isActive get the department list based on active/inactive status.
     * @return Department list.
     */
    @Query(value = "select dpt from Department dpt where dpt.isActive =:isActive AND dpt.type in (:types)")
    List<Department> findDepartmentsByAccountTypesAndActive(@Param("types") List<AccountType> types, @Param("isActive") Boolean isActive);
}
