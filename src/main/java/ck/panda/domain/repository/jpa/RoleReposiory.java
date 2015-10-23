package ck.panda.domain.repository.jpa;

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
}
