package ck.panda.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.Department.AccountType;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Service class for Department.
 *
 * This service provides basic CRUD and essential api's for Department related business actions.
 */
@Service
public interface DepartmentService extends CRUDService<Department> {

    /**
     * Delete the department.
     *
     * @param department Department entity.
     * @return Department.
     * @throws Exception error occurs
     */
    Department softDelete(Department department) throws Exception;

    /**
     * Find all the departments with active status.
     *
     * @param pagingAndSorting pagination and sorting values.
     * @param userId user id of the department.
     * @return list of departments with pagination.
     * @throws Exception error occurs
     */
    Page<Department> findAllByActive(PagingAndSorting pagingAndSorting, Long userId) throws Exception;

    /**
     * To get list of domains from cloudstack server.
     *
     * @return domain list from server
     * @throws Exception unhandled errors.
     */
    List<Department> findAllFromCSServer() throws Exception;

    /**
     * Find the departments based on the given Uuid and isActive status.
     *
     * @param uuid department uuid.
     * @param isActive department status Active/Inactive
     * @return department.
     * @throws Exception error occurs.
     */
    Department findByUuidAndIsActive(String uuid, Boolean isActive) throws Exception;

    /**
     * Find the departments based on the given domain id and isActive status.
     *
     * @param domainId domain id.
     * @param isActive department status Active/Inactive
     * @return department.
     */
    List<Department> findByDomainAndIsActive(Long domainId, Boolean isActive) throws Exception;

    /**
     * Find by username domain id and isActive status.
     *
     * @param username department name.
     * @param domainId domain id of the department.
     * @param isActive department status Active/Inactive
     * @return department.
     */
    Department findByUsernameDomainAndIsActive(String username, Long domainId, Boolean isActive);

    /**
     * Find the departments based on the account type and isActive status.
     *
     * @param types for each department.
     * @param isActive department status Active/Inactive
     * @throws Exception error occur
     * @return departments.
     */
    List<Department> findByAccountTypesAndActive(List<AccountType> types, Boolean isActive) throws Exception;

    /**
     * Find all the departments for sync.
     *
     * @throws Exception error occurs.
     * @return department.
     */
    List<Department> findAllBySync() throws Exception;

    /**
     * Find the departments based on the isActive status.
     *
     * @param domainId for each department.
     * @param types for each department.
     * @param isActive department status Active/Inactive
     * @throws Exception error occur
     * @return departments.
     */
    List<Department> findByDomainAndAccountTypesAndActive(Long domainId, List<AccountType> types,
            Boolean isActive) throws Exception;

    /**
     * Find department by uuid.
     *
     * @param uuid uuid of department.
     * @return department object.
     * @throws Exception unhandled errors.
     */
    Department findbyUUID(String uuid) throws Exception;


    /**
     * Save the department.
     *
     * @param department Department entity
     * @param userId id of the user
     * @return saved entity
     * @throws Exception error occurs
     */
    Department save(Department department, Long userId) throws Exception;

    /**
     * Find all department by domain id and isActive status of the department.
     *
     * @param domainId of the domain
     * @param isActive status of the deparmtent.
     * @return list of departments.
     * @throws Exception if error occurs.
     */
    List<Department> findAllByDomainAndIsActive(Long domainId, Boolean isActive) throws Exception;

    /**
     * Find all Department by domain id, Account type and status of the user .
     *
     * @param domainId of the user.
     * @param isActive status of the user.
     * @param domainAdmin  type of the user.
     * @return users
     * @throws Exception if error occurs.
     */
    List<Department> findAllByDomainAccountTypeAndIsActive(Long domainId, Boolean isActive, AccountType domainAdmin)
            throws Exception;

}
