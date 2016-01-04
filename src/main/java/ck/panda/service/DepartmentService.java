package ck.panda.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.Department.AccountType;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Service class for Department.
 *
 * This service provides basic CRUD and essential api's for Department related business actions.
 */
@Service
public interface DepartmentService  extends CRUDService<Department>  {

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
      * @return list of departments with pagination.
      * @throws Exception error occurs
      */
     Page<Department> findAllByActive(PagingAndSorting pagingAndSorting) throws Exception;

     /**
     * Find all the departments with active status.
     *
     * @param isActive department status Active/Inactive
     * @return list of departments with active status
     * @throws Exception error occurs.
     */
    List<Department> findAllByIsActive(Boolean isActive) throws Exception;

    /**
     * To get list of domains from cloudstack server.
     *
     * @return domain list from server
     * @throws Exception unhandled errors.
     */
    List<Department> findAllFromCSServerByDomain() throws Exception;

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
    List<Department> findByDomainAndIsActive(Long domainId, Boolean isActive);

    /**
     * Find the departments user name and isActive status.
     *
     * @param name department name.
     * @param isActive department status Active/Inactive
     * @return department.
     */
    Department findByUsername(String name, Boolean isActive);

    /**
     * Find the departments user name and domain and isActive status.
     *
     * @param name department name.
     * @param isActive department status Active/Inactive
     * @return department.
     */
    Department findByUsernameAndDomain(String name, Domain domain, Boolean isActive);

    /**
     * Find the departments based on the isActive status.
     *
     * @throws Exception error occurs.
     * @return department.
     */
    List<Department> findByAll() throws Exception;

    /**
     * Find the departments based on the isActive status.
     *
     * @param id for domain.
     * @param isActive department status Active/Inactive
     * @throws Exception error occur
     * @return departments.
     */
    List<Department> findDomainAndIsActive(Long id, Boolean isActive) throws Exception;

    /**
     * Find the departments based on the isActive status.
     *
     * @param types for each department.
     * @param isActive department status Active/Inactive
     * @throws Exception error occur
     * @return departments.
     */
    List<Department> findDepartmentsByAccountTypesAndActive(List<AccountType> types, Boolean isActive) throws Exception;

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
    List<Department> findDepartmentsByDomainAndAccountTypesAndActive(Long domainId, List<AccountType> types, Boolean isActive) throws Exception;

    /**
     * Find department by uuid.
     *
     * @param uuid uuid of department.
     * @return department object.
     * @throws Exception unhandled errors.
     */
    Department findbyUUID(String uuid) throws Exception;

}
