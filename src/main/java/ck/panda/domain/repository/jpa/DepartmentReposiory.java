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
     * @param domainId Domain reference.
     * @param isActive get the department list based on active/inactive status.
     * @return department name.
     */
    @Query(value = "select dpt from Department dpt where dpt.userName=:userName AND  dpt.domainId =:domainId AND dpt.isActive =:isActive")
    Department findByNameAndDomainAndIsActive(@Param("userName") String userName, @Param("domainId") Long domainId,
            @Param("isActive") Boolean isActive);

    /**
     * Find all the active or inactive departments with pagination.
     *
     * @param pageable to get the list with pagination.
     * @param type for account type.
     * @param isActive get the department list based on active/inactive status.
     * @return list of departments.
     */
    @Query(value = "select dpt from Department dpt where dpt.isActive =:isActive AND dpt.type=:type")
    Page<Department> findAllByIsActive(Pageable pageable, @Param("isActive") Boolean isActive,
            @Param("type") AccountType type);

    /**
     * Find all the department with active status.
     *
     * @param type for account.
     * @param isActive get the department list based on active/inactive status.
     * @return list of departments.
     */
    @Query(value = "select dpt from Department dpt where dpt.isActive =:isActive AND dpt.type=:type")
    List<Department> findAllByIsActive(@Param("isActive") Boolean isActive, @Param("type") AccountType type);

    /**
     * Find the department by uuid.
     *
     * @param uuid department uuid.
     * @param isActive get the department list based on active/inactive status.
     * @return Department.
     */
    @Query(value = "select dpt from Department dpt where dpt.isActive =:isActive AND dpt.uuid=:uuid")
    Department findByUuidAndIsActive(@Param("uuid") String uuid, @Param("isActive") Boolean isActive);

    /**
     * Find department by domain id and isActive status.
     *
     * @param id domain id
     * @param isActive get the department list based on active/inactive status.
     * @return department list
     */
    @Query(value = "select dpt from Department dpt where dpt.domainId=:domainId and dpt.isActive =:isActive")
    List<Department> findDomainAndIsActive(@Param("domainId") Long id, @Param("isActive") Boolean isActive);

    /**
     * Find the department by Domain Id and IsActive.
     *
     * @param domainId for each domain.
     * @param type for account.
     * @param isActive get the department list based on active/inactive status.
     * @return Department.
     */
    @Query(value = "select dpt from Department dpt where dpt.isActive =:isActive AND dpt.domainId=:domainId AND dpt.type=:type")
    List<Department> findByDomainAndIsActive(@Param("domainId") Long domainId, @Param("isActive") Boolean isActive,
            @Param("type") AccountType type);

    /**
     * Find the department by username and isActive.
     *
     * @param userName for each users.
     * @param domainId domain id
     * @param isActive get the department list based on active/inactive status.
     * @return Department.
     */
    @Query(value = "select dpt from Department dpt where dpt.userName=:userName AND dpt.domainId=:domainId AND dpt.isActive =:isActive")
    Department findByUsername(@Param("userName") String userName, @Param("domainId") Long domainId, @Param("isActive") Boolean isActive);

    /**
     * Find the department by username and domain and isActive.
     *
     * @param domain for each domain.
     * @param userName for each users.
     * @param isActive get the department list based on active/inactive status.
     * @return Department.
     */
    @Query(value = "select dpt from Department dpt where dpt.isActive =:isActive AND dpt.userName=:userName AND dpt.domain =:domain ")
    Department findByUsernameAndDomain(@Param("userName") String userName, @Param("domain") Domain domain,
            @Param("isActive") Boolean isActive);

    /**
     * Find the department by Domain Id and IsActive.
     *
     * @param domainId for each domain.
     * @param isActive get the department list based on active/inactive status.
     * @param pageable for pagination.
     * @param type for account.
     * @return Department.
     */
    @Query(value = "select dpt from Department dpt where dpt.isActive =:isActive AND dpt.domainId=:domainId AND dpt.type=:type")
    Page<Department> findByDomainAndIsActive(@Param("domainId") Long domainId, @Param("isActive") Boolean isActive,
            Pageable pageable, @Param("type") AccountType type);

    /**
     * Find the department list by account types and isActive.
     *
     * @param types for each department.
     * @param isActive get the department list based on active/inactive status.
     * @return Department list.
     */
    @Query(value = "select dpt from Department dpt where dpt.isActive =:isActive AND dpt.type in (:types)")
    List<Department> findDepartmentsByAccountTypesAndActive(@Param("types") List<AccountType> types,
            @Param("isActive") Boolean isActive);

    /**
     * Find the department list by domainid and account types and isActive.
     *
     * @param domainId for each department.
     * @param types for each department.
     * @param isActive get the department list based on active/inactive status.
     * @return Department list.
     */
    @Query(value = "select dpt from Department dpt where dpt.domainId =:domainId AND dpt.isActive =:isActive AND dpt.type in (:types)")
    List<Department> findDepartmentsByDomainAndAccountTypesAndActive(@Param("domainId") Long domainId,
            @Param("types") List<AccountType> types, @Param("isActive") Boolean isActive);
}
