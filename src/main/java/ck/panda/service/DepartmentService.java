package ck.panda.service;

import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Domain;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.domain.vo.PagingAndSorting;

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
     * Method to find name of the department.
     *
     * @param name of the department
     * @param domain - Domain of the department
     * @return name
     * @throws Exception if error occurs
     */
     Department findByNameAndDomain(String name, Domain domain) throws Exception;

     /**
      * Method to soft delete department.
      *
      * @param department
      * @return
      * @throws Exception error occurs
      */
     Department softDelete(Department department) throws Exception;

     /**
      * Find all the departments with active status.
      *
      * @param pagingAndSorting
      * @return
      * @throws Exception
      */
     Page<Department> findAllByActive(PagingAndSorting pagingAndSorting) throws Exception;
}
