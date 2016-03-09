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
public interface RoleRepository extends PagingAndSortingRepository<Role, Long> {

    /**
     * Find role with permissions by name and department.
     *
     * @param name of the role
     * @param departmentId department id
     * @param isActive state of the role active/inactive.
     * @return role.
     */
    @Query(value = "SELECT DISTINCT(role) FROM Role role LEFT JOIN FETCH role.permissionList WHERE role.name = :name "
            + "AND role.departmentId = :departmentId AND role.isActive = :isActive ")
    Role findWithPermissionsByNameDepartmentAndIsActive(@Param("name") String name, @Param("departmentId") Long departmentId,
            @Param("isActive") Boolean isActive);

    /**
     * Method to find list of roles by department.
     *
     * @param department department entity
     * @param isActive state of the role active/inactive.
     * @param name of the role.
     * @return List of roles
     */
    @Query(value = "SELECT role FROM Role AS role WHERE role.department = :department AND role.isActive = :isActive AND role.name != :name")
    List<Role> findAllByDepartmentAndIsActiveExceptName(@Param("department") Department department, @Param("isActive") Boolean isActive, @Param("name") String name);

    /**
     * Method to find list of roles by department.
     *
     * @param department reference of the department
     * @param isActive state of the role true/false.
     * @param pageable pagination information.
     * @return List of roles
     */
    @Query(value = "SELECT role FROM Role AS role WHERE role.department = :department AND role.isActive = :isActive")
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
     * @param isActive true/false.
     * @return vmInstance list.
     */
    @Query(value = "SELECT role FROM Role role WHERE role.departmentId = :id AND role.isActive = :isActive ")
    List<Role> findByDepartmentAndIsActive(@Param("id") Long departmentId, @Param("isActive") Boolean isActive);

    /**
     * Method to find role by name and department id and active.
     *
     * @param name of the role
     * @param departmentId department id
     * @param isActive state of the role true/false.
     * @return role
     */
    @Query(value = "SELECT role FROM Role AS role WHERE role.name = :name AND role.departmentId = :departmentId AND role.isActive = :isActive")
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
    @Query(value = "SELECT role FROM Role role WHERE role.isActive = :isActive AND role.domainId = :domainId AND role.name != 'FULL_PERMISSION'")
    Page<Role> findByDomainAndIsActive(@Param("domainId") Long domainId, @Param("isActive") Boolean isActive,
            Pageable pageable);

    /**
     * find all the domain based roles without full permission.
     *
     * @param domainId domain id of the role.
     * @param isActive get the role list based on active/inactive status.
     * @param pageable pagination information.
     * @return list of role.
     */
    @Query(value = "SELECT role FROM Role AS role WHERE role.domainId = :domainId AND role.isActive = :isActive AND role.name != 'FULL_PERMISSION'")
    Page<Role> findAllByDomainIdAndIsActive(@Param("domainId") Long domainId, @Param("isActive") Boolean isActive, Pageable pageable);

}
