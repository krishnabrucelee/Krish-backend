package ck.panda.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Role;
import ck.panda.util.domain.CRUDService;

/**
 * Service class for Role.
 *
 * This service provides basic CRUD and essential api's for Role related business actions.
 */
@Service
public interface RoleService  extends CRUDService<Role> {

    /**
     * Method to find name uniqueness from department in adding Roles.
     *
     * @param name - name of the role
     * @param department - department name
     * @return role name
     * @throws Exception - if error occurs
     */
    Role findByName(String name, Department department) throws Exception;

    /**
     * Method to find list of roles by department.
     *
     * @param department - department entity
     * @return list of roles
     * @throws Exception - if error occurs
     */
    List<Role> getRolesByDepartment(Department department) throws Exception;
}
