package ck.panda.service;

import org.springframework.stereotype.Service;

import ck.panda.domain.entity.Department;
import ck.panda.util.domain.CRUDService;


/**
 * Service class for Department.
 *
 * This service provides basic CRUD and essential api's for Department related business actions.
 */
@Service
public interface DepartmentService  extends CRUDService<Department>  {

}
