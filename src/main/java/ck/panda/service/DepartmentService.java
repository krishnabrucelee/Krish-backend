package ck.panda.service;

import ck.panda.domain.entity.Department;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.domain.vo.PagingAndSorting;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

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
    List<Department> findAllFromCSServerByDomain(String domainUuid) throws Exception;

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
     * Find by domain using Domain Id and IsActive Status.
     *
     * @param domainId for department.
     * @param isActive department status Active/Inactive
     * @return Department.
     */
    List<Department> findByDomain(Long domainId, Boolean isActive);

    /**
     * Find by Username using IsActive Status.
     *
     * @param name of the status.
     * @param isActive department status Active/Inactive
     * @return Department.
     */
    Department findByUsername(String name, Boolean isActive);

}
