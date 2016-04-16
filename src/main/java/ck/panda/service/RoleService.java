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
public interface RoleService extends CRUDService<Role> {

     /**
     * Save the role.
     *
     * @param role  entity
     * @param id of the login user
     * @return role
     * @throws Exception error occurs
     */
    Role save(Role role, Long id) throws Exception;

    /**
     * Find role with permission list.
     *
     * @param name of the role
     * @param departmentId id of the department
     * @param isActive state of the role active/inactive
     * @return role name
     * @throws Exception if error occurs
     */
    Role findWithPermissionsByNameDepartmentAndIsActive(String name, Long departmentId, Boolean isActive) throws Exception;

    /**
     * Method to find list of roles by department.
     *
     * @param department reference of the department
     * @param isActive state of the role active/inactive
     * @return list of roles
     * @throws Exception if error occurs
     */
    List<Role> findAllByDepartmentAndIsActiveExceptName(Department department, Boolean isActive, String name) throws Exception;

    /**
     * Find all the roles without full permission.
     *
     * @param pagingAndSorting paging and sorting information.
     * @return list of user.
     * @throws Exception if error occurs.
     */
    Page<Role> findAllRolesWithoutFullPermissionAndActive(PagingAndSorting pagingAndSorting) throws Exception;

    /**
     * Soft delete for roles.
     *
     * @param role reference of the role
     * @return role delete
     * @throws Exception if error occurs.
     */
    Role softDelete(Role role) throws Exception;

    /**
     * Method to find role by name and department id and active.
     *
     * @param name of the role
     * @param departmentId id of the department
     * @param isActive state of the role active/inactive
     * @return role
     * @throws Exception if error occurs
     */
    Role findByNameAndDepartmentIdAndIsActive(String name, Long departmentId, Boolean isActive) throws Exception;

    /**
     * Find role by Department id.
     *
     * @param departmentId of the department.
     * @param isActive state of the role active/inactive
     * @return role
     * @throws Exception if error occurs.
     */
    List<Role> findByDepartmentAndIsActive(Long departmentId, Boolean isActive) throws Exception;

    /**
     * Find all the roles with user id.
     *
     * @param pagingAndSorting pagination reference
     * @param userId id of the user
     * @return roles list
     * @throws Exception if error occurs.
     */
    Page<Role> findAllByUserId(PagingAndSorting pagingAndSorting, Long userId) throws Exception;

    /**
     * Find all the roles by domain.
     *
     * @param domainId domain id of the roles
     * @param pagingAndSorting pagination reference
     * @return roles list
     * @throws Exception if error occurs.
     */
    Page<Role> findAllByDomainId(Long domainId, PagingAndSorting pagingAndSorting) throws Exception;
}
