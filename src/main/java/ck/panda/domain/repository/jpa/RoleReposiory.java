package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Role;

/**
 * Jpa Repository for Role entity.
 */
public interface RoleReposiory extends PagingAndSortingRepository<Role, Long> {

    /**
     * Method to find list of entities having active status.
     *
     * @param pageable size
     * @return delete
     */
    @Query(value = "select role from Role role where role.isActive IS TRUE")
    Page<Role> findAllByActive(Pageable pageable);

    /**
     * Find role with permissions by name and department.
     *
     * @param name - name of the role
     * @param departmentId - department id
     * @param isActive - true/false.
     * @return role.
     */
    @Query(value = "select DISTINCT(role) from Role role left join fetch role.permissionList where role.name = :name and role.departmentId = :departmentId and role.isActive = :isActive ")
    Role findRoleWithPermissionsByNameAndDepartment(@Param("name") String name, @Param("departmentId") Long departmentId, @Param("isActive") Boolean isActive);

    /**
     * Method to find name uniqueness from department in adding Roles.
     *
     * @param name - name of the role
     * @param departmentId - department id
     * @param isActive
     * @return role name
     */
    @Query(value = "SELECT role FROM Role AS role WHERE role.name=:name AND role.departmentId=:departmentId AND role.isActive IS TRUE")
    Role findUniqueness(@Param("name") String name, @Param("departmentId") Long departmentId);

    /**
     * Method to find list of roles by department.
     *
     * @param department - department entity
     * @return List of roles
     */
    @Query(value = "SELECT role FROM Role AS role WHERE role.department=:department AND role.isActive IS TRUE AND role.name != 'FULL_PERMISSION'")
    List<Role> getRolesByDepartment(@Param("department") Department department);

    /**
     * Method to find list of roles by department.
     *
     * @param department - department entity
     * @param isActive - true/false.
     * @param pageable pagination information.
     * @return List of roles
     */
    @Query(value = "SELECT role FROM Role AS role WHERE role.department=:department AND role.isActive IS :isActive")
    Page<Role> findByDepartmentAndIsActive(@Param("department") Department department,
            @Param("isActive") Boolean isActive, Pageable pageable);

    /**
     * find all the roles without full permission.
     *
     * @param pageable pagination information.
     * @return list of user.
     */
    @Query(value = "SELECT role FROM Role AS role WHERE role.isActive IS TRUE AND role.name != 'FULL_PERMISSION'")
    Page<Role> findAllRolesWithoutFullPermissionAndActive(Pageable pageable);

    /**
     * Find all roles from department.
     *
     * @param departmentId department id.
     * @param isActive - true/false.
     * @return vmInstance list.
     */
    @Query(value = "select role from Role role where role.departmentId=:id and role.isActive =:isActive ")
    List<Role> findByDepartmentAndIsActive(@Param("id") Long departmentId, @Param("isActive") Boolean isActive);

    /**
     * Method to find role by name and department id and active.
     *
     * @param name - name of the role
     * @param departmentId - department id
     * @param isActive - true
     * @return role
     */
    @Query(value = "SELECT role FROM Role AS role WHERE role.name=:name AND role.departmentId=:departmentId AND role.isActive=:isActive")
    Role findByNameAndDepartmentIdAndIsActive(@Param("name") String name, @Param("departmentId") Long departmentId,
            @Param("isActive") Boolean isActive);

    /**
     * Find the Role by Domain Id and IsActive.
     *
     * @param domainId for each domain.
     * @param isActive get the volume list based on active/inactive status.
     * @param pageable page
     * @return Role.
     */
    @Query(value = "select role from Role role where role.isActive =:isActive AND role.domainId=:domainId AND role.name != 'FULL_PERMISSION'")
    Page<Role> findByDomainAndIsActive(@Param("domainId") Long domainId, @Param("isActive") Boolean isActive,
            Pageable pageable);

}
