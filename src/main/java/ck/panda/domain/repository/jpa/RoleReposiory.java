package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Role;
import ck.panda.domain.entity.VmInstance;

/**
 * Jpa Repository for Role entity.
 */
public interface RoleReposiory extends PagingAndSortingRepository<Role, Long> {

    /**
     * Method to find list of entities having active status.
     * @param pageable size
     * @return delete
     */
    @Query(value = "select role from Role role where role.isActive IS TRUE")
    Page<Role> findAllByActive(Pageable pageable);

    /**
     * Method to find name uniqueness from department in adding Roles.
     *
     * @param name - name of the role
     * @param department - department name
     * @return role name
     */
    @Query(value = "SELECT role FROM Role AS role WHERE role.name=:name AND role.department=:department AND role.isActive IS TRUE")
    Role findUniqueness(@Param("name") String name, @Param("department") Department department);

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
     * @return List of roles
     */
    @Query(value = "SELECT role FROM Role AS role WHERE role.department=:department AND role.isActive IS :isActive")
    Page<Role> findByDepartmentAndIsActive(@Param("department") Department department,@Param("isActive")  Boolean isActive, Pageable pageable);
    
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
     * @return vmInstance list.
     */
    @Query(value = "select role from Role role where role.department=:department ")
    List<Role> findByDepartment(@Param("department") Department department);
}
