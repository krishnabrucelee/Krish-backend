package ck.panda.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Role;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.domain.vo.PagingAndSorting;

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

    /**
     * Find all the roles without full permission.
     *
     * @param pagingAndSorting paging and sorting information.
     * @return list of user.
     * @throws Exception if error occurs.
     */
    Page<Role> findAllRolesWithoutFullPermissionAndActive(PagingAndSorting pagingAndSorting) throws Exception;

    /**
     * Soft delete method for role.
     * 
     * @param role name.
     * @return role.
     * @throws Exception unhandled errors.
     */
    Role softDelete(Role role) throws Exception;


   /**
     * Find role by Department.
     * 
     * @param id of the department.
     * @return id.
     * @throws Exception unhandled errors.
     */
	List<Role> findByDepartment(Department department) throws Exception;
}
