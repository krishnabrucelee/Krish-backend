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
import ck.panda.domain.entity.User.Status;
import ck.panda.domain.entity.User.UserType;

/** JPA repository for user CRUD operations. */
public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    /**
     * Find user by active and query.
     *
     * @param query for user name
     * @return user list
     */
    @Query(value = "SELECT user FROM User user WHERE user.isActive IS TRUE AND lower(user.userName) LIKE '%' || lower(:query) || '%' ")
    List<User> findAllByActive(@Param("query") String query);

    /**
     * Find all the active Users with pagination.
     *
     * @param pageable to get the list with pagination
     * @param isActive get the User list based on active/inactive status
     * @return list of users
     */
    @Query(value = "SELECT user FROM User user LEFT JOIN user.role WHERE user.isActive =:isActive")
    Page<User> findAllByIsActive(Pageable pageable, @Param("isActive") Boolean isActive);

    /**
     * Find the user already exist for the same domain.
     *
     * @param userName userName of the user
     * @param domain domain of the user
     * @param isActive check whether users are removed or not.
     * @return user
     */
    @Query(value = "select user from User user where user.userName = :userName AND user.domain = :domain AND user.isActive = :isActive")
    User findByUserNameAndDomainAndActive(@Param("userName") String userName, @Param("domain") Domain domain, @Param("isActive") Boolean isActive);

    /**
     * Find the user already exist for the same domain.
     *
     * @param userName userName of the user
     * @param domain domain of the user
     * @param isActive check whether users are removed or not
     * @param id user id
     * @return user
     */
    @Query(value = "select user from User user where user.userName = :userName AND user.domain = :domain AND user.isActive = :isActive AND user.id <> :id")
    User findByUserNameAndDomainAndActiveAndUserId(@Param("userName") String userName, @Param("domain") Domain domain, @Param("isActive") Boolean isActive, @Param("id") Long id);

    /**
     * Find user by department.
     *
     * @param department department object.
     * @return list of users.
     */
    @Query(value = "SELECT user FROM User user WHERE user.isActive IS TRUE AND user.department=:department")
    List<User> findByDepartment(@Param("department") Department department);

    /**
     * Find the user for login authentication.
     *
     * @param userName login user name
     * @param domain object
     * @param isActive active users
     * @return user details
     */
    @Query(value = "SELECT user FROM User user WHERE user.userName = :userName AND user.domain=:domain AND user.isActive =:isActive")
    User findByUser(@Param("userName") String userName, @Param("domain") Domain domain,
            @Param("isActive") Boolean isActive);

    /**
     * find all the user by domain.
     *
     * @param pageable pagination information.
     * @param domain domain object.
     * @param status status of the user
     * @return list of user.
     */
    @Query(value = "SELECT user FROM User user LEFT JOIN user.role WHERE user.domain =:domain AND user.status <> :status")
    Page<User> findAllUserByDomain(Pageable pageable, @Param("domain") Domain domain, @Param("status") Status status);

    /**
     * find all the user by domain.
     *
     * @param domain domain object.
     * @return list of user.
     */
    @Query(value = "SELECT user FROM User user WHERE user.domain =:domain ")
    List<User> findAllUserByDomain(@Param("domain") Domain domain);

    /**
     * Find the user from account.
     *
     * @param userName username .
     * @param domain domain object.
     * @return user.
     */
    @Query(value = "SELECT user FROM User user WHERE user.domain =:domain AND user.userName =:userName ")
    User findAllByActiveAndName(@Param("userName") String userName, @Param("domain") Domain domain);

    /**
     * find all the root admin.
     *
     * @param type user type.
     * @return list of user.
     */
    @Query(value = "SELECT user FROM User user WHERE user.type =:type ")
    List<User> findAllRootAdminUser(@Param("type") UserType type);

    /**
     * Find the user list by account types and isActive.
     *
     * @param types for each user.
     * @param isActive get the user list based on active/inactive status.
     * @return user list.
     */
    @Query(value = "SELECT user FROM User user WHERE user.isActive =:isActive AND user.type in (:types)")
    List<User> findUsersByTypesAndActive(@Param("types") List<UserType> types, @Param("isActive") Boolean isActive);

    /**
     * Find the user by given uuid and the status.
     *
     * @param uuId of the user.
     * @param isActive status of the user.
     * @return user.
     */
    @Query(value = "SELECT user FROM User user WHERE user.uuid =:uuId AND user.isActive =:isActive")
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
     * Find all user by status .
     *
     * @param pageable pagination
     * @param status status of the user
     * @return list of users
     */
    @Query(value = "SELECT user FROM User user LEFT JOIN user.role WHERE user.status <> :status")
    Page<User> findAllUserByStatus(Pageable pageable,@Param("status") Status status);

    /**
     * Find all the user by domain in user panel.
     *
     * @param domainId domain id of the user.
     * @param searchText search text
     * @param deleted deleted status
     * @param isActive user status Active/Inactive.
     * @param pageable pagination information.
     * @return list of user.
     */
    @Query(value = "select user from User user LEFT JOIN user.role where (user.domainId = :domainId OR 0 = :domainId) AND (user.userName LIKE %:search% OR user.department.userName LIKE %:search% OR user.domain.name LIKE %:search% OR user.type LIKE %:search% OR user.role.name LIKE %:search%"
            + " OR user.email LIKE %:search% OR user.status LIKE %:search%) and user.status <> :status AND user.isActive = :isActive")
    Page<User> findAllByUserPanelAndDomainId(@Param("domainId") Long domainId, @Param("search") String searchText, @Param("status") Status deleted, Pageable pageable, @Param("isActive") Boolean isActive);

    /**
     * Find all the user by domain.
     *
     * @param domainId domain id of the user.
     * @param searchText search text
     * @param pageable pagination information.
     * @param isActive user status Active/Inactive.
     * @return list of user.
     */
    @Query(value = "select user from User user LEFT JOIN user.role where (user.domainId = :domainId OR 0 = :domainId) AND (user.userName LIKE %:search% OR user.department.userName LIKE %:search% OR user.domain.name LIKE %:search% OR user.type LIKE %:search% OR user.role.name LIKE %:search%"
            + " OR user.email LIKE %:search% OR user.status LIKE %:search%) AND user.isActive = :isActive")
    Page<User> findAllByDomainId(@Param("domainId") Long domainId, @Param("search") String searchText, Pageable pageable, @Param("isActive") Boolean isActive);

    /**
     * Find all the user by domain.
     *
     * @param domainId domain id of the user.
     * @param searchText search text
     * @param isActive user status Active/Inactive.
     * @param userType user type
     * @param pageable pagination information.
     * @return list of user.
     */
    @Query(value = "SELECT user FROM User user WHERE (user.domainId = :domainId OR 0 = :domainId) AND (user.userName LIKE %:search% OR user.department.userName LIKE %:search% OR user.domain.name LIKE %:search% OR user.type LIKE %:search% OR user.role.name LIKE %:search%"
            + " OR user.email LIKE %:search% OR user.status LIKE %:search%) AND user.type IN :type AND user.isActive = :isActive")
    Page<User> findAllByDomainId(@Param("domainId") Long domainId, @Param("search") String searchText, @Param("isActive") Boolean isActive, @Param("type") List<UserType> userType, Pageable pageable);

    /**
     * Get list of required parameter of user.
     *
     * @param id user id.
     * @return user.
     */
    @Query(value = "SELECT new map(user.id as id, user.userName as userName, user.email as email, user.type as type, user.firstName as firstName, user.lastName as lastName, user.uuid as uuid, user.status as status, user.domain as domain, user.role as role) FROM User user WHERE user.id = :id")
    User findByUserValidList(@Param("id") Long id);

    /**
     * Find all user by domain. departmetn id and isActive status.
     *
     * @param domainId of the user.
     * @param isActive status of the user.
     * @param departmentId of the user.
     * @param type type of the user.
     * @return user.
     * @throws Exception if error occurs.
     */
    @Query(value = "SELECT user FROM User user WHERE user.domainId = :domainId AND user.departmentId = :departmentId AND user.isActive = :isActive "
            + "AND user.type= :type")
    List<User> findByDomainAndIsActive(@Param("domainId") Long domainId, @Param("isActive") Boolean isActive,@Param("departmentId") Long departmentId,
            @Param("type") UserType type);

    /**
     * Find the user by given uuid.
     *
     * @param uuid of the user.
     * @return user.
     */
    @Query(value = "SELECT user FROM User user WHERE user.uuid =:uuid")
    User findByUuId(@Param("uuid") String uuid);

    /**
     * find all the root admin.
     *
     * @param type user type.
     * @param isActive true/false
     * @return list of user.
     */
    @Query(value = "SELECT user FROM User user WHERE user.type =:type AND user.isActive = :isActive")
    User findAllByUserTypeAndIsActive(@Param("type") UserType type, @Param("isActive") Boolean isActive);

    /**
     * Find the user from account.
     *
     * @param userName username .
     * @param isActive true/false
     * @return user.
     */
    @Query(value = "SELECT user FROM User user WHERE user.userName =:userName AND user.isActive = :isActive")
    User findByUserNameAndActive(@Param("userName") String userName, @Param("isActive") Boolean isActive);

    /**
     * Find by the root admin user.
     *
     * @param type user type.
     * @param isActive true/false
     * @return list of user.
     */
    @Query(value = "SELECT user FROM User user WHERE user.type = :type AND user.isActive = :isActive")
    List<User> findByRootAdminUser(@Param("type") UserType type, @Param("isActive") Boolean isActive);

    /**
     * Count of users for search.
     *
     * @param domainId domain id.
     * @param searchText search text.
     * @return user
     * @throws Exception unhandled errors.
     */
    @Query(value = "select user from User user LEFT JOIN user.role where (user.domainId = :domainId OR 0 = :domainId) AND (user.userName LIKE %:search% OR user.department.userName LIKE %:search% OR user.domain.name LIKE %:search% OR user.type LIKE %:search% OR user.role.name LIKE %:search%"
            + " OR user.email LIKE %:search% OR user.status LIKE %:search%)")
    List<User> findBySearchText(@Param("domainId") Long domainId, @Param("search") String searchText);

    /**
     * Find all the user by domain in user panel with search text.
     *
     * @param domainId domain id of the user.
     * @param searchText search text.
     * @param userType user type
     * @param deleted deleted status
     * @param isActive true/false
     * @param pageable pagination information.
     * @return list of user.
     */
    @Query(value = "select user from User user LEFT JOIN user.role where (user.domainId = :domainId OR 0 = :domainId) AND (user.userName LIKE %:search% OR user.department.userName LIKE %:search% OR user.domain.name LIKE %:search% OR user.type LIKE %:search% OR user.role.name LIKE %:search%"
            + " OR user.email LIKE %:search% OR user.status LIKE %:search% OR user.type IN :type) and user.status <> :status AND user.isActive = :isActive")
    Page<User> findAllByUserPanelAndDomainId(@Param("domainId") Long domainId, @Param("search") String searchText, @Param("type") List<UserType> userType, @Param("status") Status deleted, Pageable pageable, @Param("isActive") Boolean isActive);

    /**
     * Find all the user by domain in user panel, user type and user id.
     *
     * @param domainId domain id of the user.
     * @param searchText search text.
     * @param userType user type
     * @param deleted deleted status
     * @param isActive true/false
     * @param id user id
     * @param pageable pagination information.
     * @return list of user.
     */
    @Query(value = "select user from User user LEFT JOIN user.role where (user.domainId = :domainId OR 0 = :domainId) AND (user.userName LIKE %:search% OR user.department.userName LIKE %:search% OR user.domain.name LIKE %:search% OR user.type LIKE %:search% OR user.role.name LIKE %:search%"
            + " OR user.email LIKE %:search% OR user.status LIKE %:search% OR user.type IN :type) and user.status <> :status AND user.isActive = :isActive AND user.id = :id")
    Page<User> findAllByUserPanelAndDomainIdAndUserId(@Param("domainId") Long domainId, @Param("search") String searchText, @Param("type") List<UserType> userType, @Param("status") Status deleted, Pageable pageable, @Param("isActive") Boolean isActive, @Param("id") Long id);

    /**
     * Find all the user by domain in user panel with user id.
     *
     * @param domainId domain id of the user.
     * @param searchText search text.
     * @param deleted deleted status
     * @param isActive true/false
     * @param id user id
     * @param pageable pagination information.
     * @return list of user.
     */
    @Query(value = "select user from User user LEFT JOIN user.role where (user.domainId = :domainId OR 0 = :domainId) AND (user.userName LIKE %:search% OR user.department.userName LIKE %:search% OR user.domain.name LIKE %:search% OR user.type LIKE %:search% OR user.role.name LIKE %:search%"
            + " OR user.email LIKE %:search% OR user.status LIKE %:search%) and user.status <> :status AND user.isActive = :isActive AND user.id = :id")
    Page<User> findAllByUserPanelAndDomainIdAndUserId(@Param("domainId") Long domainId, @Param("search") String searchText, @Param("status") Status deleted, Pageable pageable, @Param("isActive") Boolean isActive, @Param("id") Long id);

    /**
     * Find all the user by domain in user panel.
     *
     * @param domainId domain id of the user.
     * @param searchText search text.
     * @param deleted deleted status
     * @param isActive true/false
     * @return list of user.
     */
    @Query(value = "select user from User user LEFT JOIN user.role where (user.domainId = :domainId OR 0 = :domainId) AND (user.userName LIKE %:search% OR user.department.userName LIKE %:search% OR user.domain.name LIKE %:search% OR user.type LIKE %:search% OR user.role.name LIKE %:search%"
            + " OR user.email LIKE %:search% OR user.status LIKE %:search%) and user.status <> :status AND user.isActive = :isActive")
    List<User> findAllByDomainIdAndSearchTextAndStatusCount(@Param("domainId") Long domainId, @Param("search") String searchText, @Param("status") Status deleted, @Param("isActive") Boolean isActive);

    /**
     * Find all the user by domain in user panel with search text.
     *
     * @param domainId domain id of the user.
     * @param searchText search text.
     * @param userType user type
     * @param deleted deleted status
     * @param isActive true/false
     * @return list of user.
     */
    @Query(value = "select user from User user LEFT JOIN user.role where (user.domainId = :domainId OR 0 = :domainId) AND (user.userName LIKE %:search% OR user.department.userName LIKE %:search% OR user.domain.name LIKE %:search% OR user.type LIKE %:search% OR user.role.name LIKE %:search%"
            + " OR user.email LIKE %:search% OR user.status LIKE %:search% OR user.type IN :type) and user.status <> :status AND user.isActive = :isActive")
    List<User> findAllByDomainIdAndUserTypeAndStatusCount(@Param("domainId") Long domainId, @Param("search") String searchText, @Param("type") List<UserType> userType, @Param("status") Status deleted, @Param("isActive") Boolean isActive);

    /**
     * Find all the user by domain in user panel, user type and user id.
     *
     * @param domainId domain id of the user.
     * @param searchText search text.
     * @param userType user type
     * @param deleted deleted status
     * @param isActive true/false
     * @param id user id
     * @return list of user.
     */
    @Query(value = "select user from User user LEFT JOIN user.role where (user.domainId = :domainId OR 0 = :domainId) AND (user.userName LIKE %:search% OR user.department.userName LIKE %:search% OR user.domain.name LIKE %:search% OR user.type LIKE %:search% OR user.role.name LIKE %:search%"
            + " OR user.email LIKE %:search% OR user.status LIKE %:search% OR user.type IN :type) and user.status <> :status AND user.isActive = :isActive AND user.id = :id")
    List<User> findAllByDomainIdAndSearchTextAndUserIdAndUserTypeCount(@Param("domainId") Long domainId, @Param("search") String searchText, @Param("type") List<UserType> userType, @Param("status") Status deleted, @Param("isActive") Boolean isActive, @Param("id") Long id);

    /**
     * Find all the user by domain in user panel with user id.
     *
     * @param domainId domain id of the user.
     * @param searchText search text.
     * @param deleted deleted status
     * @param isActive true/false
     * @param id user id
     * @return list of user.
     */
    @Query(value = "select user from User user LEFT JOIN user.role where (user.domainId = :domainId OR 0 = :domainId) AND (user.userName LIKE %:search% OR user.department.userName LIKE %:search% OR user.domain.name LIKE %:search% OR user.type LIKE %:search% OR user.role.name LIKE %:search%"
            + " OR user.email LIKE %:search% OR user.status LIKE %:search%) and user.status <> :status AND user.isActive = :isActive AND user.id = :id")
    List<User> findAllByDomainIdAndSearchTextAndUserIdCount(@Param("domainId") Long domainId, @Param("search") String searchText, @Param("status") Status deleted, @Param("isActive") Boolean isActive, @Param("id") Long id);

}
