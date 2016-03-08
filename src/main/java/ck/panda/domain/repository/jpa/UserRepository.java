package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.User.UserType;

/** JPA repository for user CRUD operations. */
public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    /**
     * Find user by active and query.
     *
     * @param query for user name
     * @return user list
     */
    @Query(value = "select user from User user where user.isActive IS TRUE and lower(user.userName) LIKE '%' || lower(:query) || '%' ")
    List<User> findAllByActive(@Param("query") String query);

    /**
     * Find the department already exist for the same domain.
     *
     * @param userName userName of the user
     * @param domain domain of the user
     * @return user
     */
    @Query(value = "select user from User user where user.userName=:userName AND user.domain=:domain")
    User findByUserNameAndDomain(@Param("userName") String userName, @Param("domain") Domain domain);

    /**
     * Find user by department.
     *
     * @param department department object.
     * @return list of users.
     */
    @Query(value = "select user from User user where user.isActive IS TRUE AND user.department=:department")
    List<User> findByDepartment(@Param("department") Department department);

    /**
     * Find the user for login authentication.
     *
     * @param userName login user name
     * @param domain object
     * @param isActive active users
     * @return user details
     */
    @Query(value = "select user from User user where user.userName = :userName AND user.domain=:domain AND user.isActive =:isActive")
    User findByUser(@Param("userName") String userName, @Param("domain") Domain domain,
            @Param("isActive") Boolean isActive);

    /**
     * find all the user by domain.
     *
     * @param pageable pagination information.
     * @param domain domain object.
     * @param isActive true/false
     * @return list of user.
     */
    @Query(value = "select user from User user where user.domain =:domain AND user.isActive =:isActive")
    Page<User> findAllUserByDomain(Pageable pageable, @Param("domain") Domain domain,
            @Param("isActive") Boolean isActive);

    /**
     * find all the user by domain.
     *
     * @param domain domain object.
     * @return list of user.
     */
    @Query(value = "select user from User user where user.domain =:domain ")
    List<User> findAllUserByDomain(@Param("domain") Domain domain);

    /**
     * Find the user from account.
     *
     * @param userName username .
     * @param domain domain object.
     * @return user.
     */
    @Query(value = "select user from User user where user.domain =:domain and user.userName =:userName ")
    User findAllByActiveAndName(@Param("userName") String userName, @Param("domain") Domain domain);

    /**
     * find all the root admin.
     *
     * @param type user type.
     * @return list of user.
     */
    @Query(value = "select user from User user where user.type =:type ")
    List<User> findAllRootAdminUser(@Param("type") UserType type);

    /**
     * Find the user list by account types and isActive.
     *
     * @param types for each user.
     * @param isActive get the user list based on active/inactive status.
     * @return user list.
     */
    @Query(value = "select user from User user where user.isActive =:isActive AND user.type in (:types)")
    List<User> findUsersByTypesAndActive(@Param("types") List<UserType> types, @Param("isActive") Boolean isActive);

    /**
     * Find the user by given uuid and the status.
     *
     * @param uuId of the user.
     * @param isActive status of the user.
     * @return user.
     */
    @Query(value = "select user from User user where user.uuid =:uuId AND user.isActive =:isActive")
    User findByUuIdAndIsActive(@Param("uuId") String uuId, @Param("isActive") Boolean isActive);

    /**
     * Find user role whether assigned or not.
     *
     * @param roleId role id
     * @param isActive user status Active/Inactive
     * @return users
     */
    @Query(value = "SELECT user FROM User user WHERE user.roleId = :roleId AND user.isActive = :isActive")
    List<User> findByRole(@Param("roleId") Long roleId, @Param("isActive") Boolean isActive);

    /**
     * Find all user by department not belongs to project.
     *
     * @param departmentId department object.
     * @param userList users list.
     * @param isActive user status Active/Inactive.
     * @return list of users.
     */
    @Query(value = "SELECT user FROM User user WHERE user.isActive IS :isActive AND user.departmentId = :departmentId AND user NOT IN :userList")
    List<User> findAllByDepartmentAndIsActive(@Param("isActive") Boolean isActive, @Param("departmentId") Long departmentId, @Param("userList") List<User> userList);

    /**
     * find all the user by domain.
     *
     * @param domainId domain id of the user.
     * @param pageable pagination information.
     * @return list of user.
     */
    @Query(value = "select user from User user where user.domainId =:domainId")
    Page<User> findAllByDomainId(@Param("domainId") Long domainId, Pageable pageable);

}
