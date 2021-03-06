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

/**
 * JPA Repository for Department entity.
 */
@Repository
public interface DepartmentRepository extends PagingAndSortingRepository<Department, Long> {

    /**
     * Find all the departments with pagination based on the active/inactive state and type.
     *
     * @param pageable to get the list with pagination.
     * @param isActive get the department list based on active/inactive status.
     * @return list of departments.
     */
    @Query(value = "SELECT dpt FROM Department dpt WHERE dpt.isActive = :isActive")
    Page<Department> findAllByIsActive(Pageable pageable, @Param("isActive") Boolean isActive);

    /**
     * Find all the department with active status.
     *
     * @param isActive get the department list based on active/inactive status.
     * @return list of departments.
     */
    @Query(value = "SELECT dpt FROM Department dpt WHERE dpt.isActive = :isActive")
    List<Department> findAllByIsActive(@Param("isActive") Boolean isActive);

    /**
     * Find the department by uuid and active/inactive status.
     *
     * @param uuid department uuid.
     * @param isActive get the department list based on active/inactive status.
     * @return Department.
     */
    @Query(value = "SELECT dpt FROM Department dpt WHERE dpt.isActive = :isActive AND dpt.uuid = :uuid")
    Department findByUuidAndIsActive(@Param("uuid") String uuid, @Param("isActive") Boolean isActive);

    /**
     * Find departments by domain id and isActive status.
     *
     * @param id domain id
     * @param isActive get the department list based on active/inactive status.
     * @return department list
     */
    @Query(value = "SELECT dpt FROM Department dpt WHERE dpt.domainId = :domainId AND dpt.isActive = :isActive")
    List<Department> findDomainAndIsActive(@Param("domainId") Long id, @Param("isActive") Boolean isActive);

    /**
     * Find all the departments based on the active/inactive state and type.
     *
     * @param domainId for each domain.
     * @param types for account.
     * @param isActive get the department list based on active/inactive status.
     * @return Department.
     */
    @Query(value = "SELECT dpt FROM Department dpt WHERE dpt.isActive = :isActive AND dpt.domainId= :domainId "
            + "AND dpt.type in (:types)")
    List<Department> findByDomainAndIsActive(@Param("domainId") Long domainId, @Param("isActive") Boolean isActive,
            @Param("types") List<AccountType> types);

    /**
     * Find the department by username and isActive.
     *
     * @param userName for each users.
     * @param domainId domain id
     * @param isActive get the department list based on active/inactive status.
     * @return Department.
     */
    @Query(value = "SELECT dpt FROM Department dpt WHERE dpt.userName = :userName AND dpt.domainId = :domainId "
            + "AND dpt.isActive =:isActive")
    Department findByUsernameDomainAndIsActive(@Param("userName") String userName, @Param("domainId") Long domainId,
            @Param("isActive") Boolean isActive);

    /**
     * Find all the departments based on the domain id, active/inactive state and type.
     *
     * @param domainId for each domain.
     * @param isActive get the department list based on active/inactive status.
     * @param pageable for pagination.
     * @param types for account.
     * @return Department.
     */
    @Query(value = "SELECT dpt FROM Department dpt WHERE dpt.isActive = :isActive AND dpt.domainId = :domainId "
            + "AND dpt.type in (:types)")
    Page<Department> findByDomainAndIsActive(@Param("domainId") Long domainId, @Param("isActive") Boolean isActive,
            Pageable pageable, @Param("types") List<AccountType> types);

    /**
     * Find the department list by account types and isActive.
     *
     * @param types for each department.
     * @param isActive get the department list based on active/inactive status.
     * @return Department list.
     */
    @Query(value = "SELECT dpt FROM Department dpt WHERE dpt.isActive = :isActive AND dpt.type in (:types)")
    List<Department> findByAccountTypesAndActive(@Param("types") List<AccountType> types,
            @Param("isActive") Boolean isActive);

    /**
     * Find the department list by domainid and account types and isActive.
     *
     * @param domainId for each department.
     * @param types for each department.
     * @param isActive get the department list based on active/inactive status.
     * @return Department list.
     */
    @Query(value = "SELECT dpt FROM Department dpt WHERE dpt.domainId = :domainId "
            + "AND dpt.isActive = :isActive  AND dpt.type in (:types)")
    List<Department> findByDomainAndAccountTypesAndActive(@Param("domainId") Long domainId,
            @Param("types") List<AccountType> types, @Param("isActive") Boolean isActive);

    /**
     * Find all the departments with pagination based on the domain, active/inactive state and type.
     *
     * @param domainId domain id of the department.
     * @param isActive get the department list based on active/inactive status.
     * @param type for account type.
     * @param pageable to get the list with pagination.
     * @return list of departments.
     */
    @Query(value = "SELECT dpt FROM Department dpt WHERE dpt.domainId = :domainId AND dpt.isActive = :isActive AND dpt.type = :type")
    Page<Department> findAllByDomainIdAndIsActive(@Param("domainId") Long domainId, @Param("isActive") Boolean isActive,
            @Param("type") AccountType type, Pageable pageable);

    /**
     * Find all the department based active or inactive departments with pagination.
     *
     * @param domainId domain id of the department
     * @param search search text.
     * @param types for account.
     * @param isActive get the department list based on active/inactive status
     * @param pageable to get the list with pagination
     * @return list of departments
     */
    @Query(value = "SELECT dpt FROM Department dpt WHERE (dpt.domainId = :domainId OR 0 = :domainId) AND dpt.type in (:types) AND dpt.isActive = :isActive AND (dpt.userName LIKE %:search% OR dpt.description LIKE %:search% OR dpt.domain.name LIKE %:search%)")
    Page<Department> findDomainBySearchTextWithType(@Param("domainId") Long domainId, Pageable pageable, @Param("types") List<AccountType> types, @Param("search") String search, @Param("isActive") Boolean isActive);

    /**
     * Find all the department based active or inactive departments with pagination.
     *
     * @param domainId domain id of the department
     * @param search search text.
     * @param isActive get the department list based on active/inactive status
     * @param pageable to get the list with pagination
     * @return list of departments
     */
    @Query(value = "SELECT dpt FROM Department dpt WHERE (dpt.domainId = :domainId OR 0 = :domainId) AND dpt.isActive = :isActive AND (dpt.userName LIKE %:search% OR dpt.description LIKE %:search% OR dpt.domain.name LIKE %:search%)")
    Page<Department> findDomainBySearchText(@Param("domainId") Long domainId, Pageable pageable, @Param("search") String search, @Param("isActive") Boolean isActive);

}
