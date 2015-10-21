package ck.panda.service;

import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Domain;
import ck.panda.util.domain.CRUDService;
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
     * @return name
     * @throws Exception if error occurs
     */
     Department findByNameAndDomain(String name, Domain domain) throws Exception;
}