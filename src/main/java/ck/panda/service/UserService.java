package ck.panda.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.User.Status;
import ck.panda.domain.entity.User.UserType;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.domain.vo.PagingAndSorting;

/** The UserService interface used for to perform CRUD operations and basic API's related business logic. */
public interface UserService extends CRUDService<User> {

    /**
     * Save the user.
     *
     * @param user User entity
     * @param id of the login user
     * @return User
     * @throws Exception error occurs
     */
    User save(User user, Long id) throws Exception;

    /**
     * To get list of user.
     *
     * @param pagingAndSorting parameters
     * @param id of the login user
     * @return user list with pagination
     * @throws Exception unhandled errors
     */
    Page<User> findAllByActive(PagingAndSorting pagingAndSorting, Long id) throws Exception;

    /**
     * To get list of users from cloudstack server.
     *
     * @return user list from server
     * @throws Exception unhandled errors.
     */
    List<User> findAllFromCSServerByDomain() throws Exception;

    /**
     * To get list of users by department.
     *
     * @param departmentId department id.
     * @return list of user.
     * @throws Exception if error occurs.
     */
    List<User> findByDepartment(Long departmentId) throws Exception;

    /**
     * To get list of users by project.
     *
     * @param projectId project id.
     * @return list of user.
     * @throws Exception if error occurs.
     */
    List<User> findAllByProject(Long projectId) throws Exception;

    /**
     * To get list of users by department and logged in user.
     *
     * @param departmentId department id.
     * @param userId user id.
     * @return list of user.
     * @throws Exception if error occurs.
     */
    List<User> findByDepartmentWithLoggedUser(Long departmentId, Long userId) throws Exception;

    /**
     * Find the User already exist for the same domain.
     *
     * @param userName userName of the user
     * @param domain domain of the user
     * @return user
     * @throws Exception unhandled exception.
     */
    User findByUserNameAndDomain(@Param("userName") String userName, @Param("domain") Domain domain) throws Exception;

    /**
     * Find the user for login authentication.
     *
     * @param userName login user name
     * @param password login password
     * @param domainName login domain
     * @return user details
     * @throws Exception raise if error
     */
    User findByUser(String userName, String password, String domainName) throws Exception;

    /**
     * Method to soft delete user.
     *
     * @param user user object.
     * @return user.
     * @throws Exception if error occurs.
     */
    User softDelete(User user) throws Exception;

    /**
     * Find user by uuid Id and status.
     *
     * @param uuid of the user.
     * @param isActive status of the user.
     * @return account.
     * @throws Exception if error occurs.
     */
    User findByUuIdAndIsActive(String uuid, Boolean isActive) throws Exception;

    /**
     * Find all the user by domain.
     *
     * @param pagingAndSorting paging and sorting information.
     * @param userId user id.
     * @param status user status
     * @return list of user.
     * @throws Exception if error occurs.
     */
    Page<User> findAllUserByDomain(PagingAndSorting pagingAndSorting, Long userId, Status status) throws Exception;

    /**
     * Find all the user by domain.
     *
     * @param userId user id.
     * @return list of user.
     * @throws Exception if error occurs.
     */
    List<User> findAllUserByDomain(Long userId) throws Exception;

    /**
     * Find the user by domain.
     *
     * @param owner user object.
     * @param domain domain object.
     * @return user.
     * @throws Exception if error occurs.
     */
    User findByNameAndDomain(String owner, Domain domain) throws Exception;

    /**
     * Find all root admin.
     *
     * @return list of root admin.
     * @throws Exception if error occurs.
     */
    List<User> findAllRootAdminUser() throws Exception;

    /**
     * Find the users based on the isActive status.
     *
     * @param types for each user.
     * @param isActive user status Active/Inactive
     * @throws Exception error occur
     * @return users.
     */
    List<User> findUsersByTypesAndActive(List<UserType> types, Boolean isActive) throws Exception;

    /**
     * Assign user to role.
     *
     * @param users List of users
     * @return users
     * @throws Exception unhandled errors.
     */
    List<User> assignUserRoles(List<User> users) throws Exception;

    /**
     * Find user role whether assigned or not.
     *
     * @param roleId role id
     * @param isActive user status Active/Inactive
     * @return users
     * @throws Exception errors
     */
    List<User> findByRole(Long roleId, Boolean isActive) throws Exception;

    /**
     * Enable the User.
     *
     * @param userId id of the user
     * @return users
     * @throws Exception exceptions
     */
    User enableUser(Long userId) throws Exception;

    /**
     * Disable the User.
     *
     * @param userId id of the user
     * @return users
     * @throws Exception exceptions
     */
    User disableUser(Long userId) throws Exception;

    /**
     * Find all the user by domain in user panel.
     *
     * @param domainId domain id of the user.
     * @param searchText search text.
     * @param pagingAndSorting paging and sorting information.
     * @param userId user id
     * @return list of user.
     * @throws Exception if error occurs.
     */
    Page<User> findAllByUserPanelAndDomainId(Long domainId, String searchText, PagingAndSorting pagingAndSorting, Long userId) throws Exception;

    /**
     * Find all the user by domain.
     *
     * @param domainId domain id of the user.
     * @param pagingAndSorting paging and sorting information.
     * @param userId user id
     * @param searchText search text
     * @return list of user.
     * @throws Exception if error occurs.
     */
    Page<User> findAllByDomainId(Long domainId, String searchText, PagingAndSorting pagingAndSorting, Long userId) throws Exception;

    /**
     * Update password of user.
     *
     * @param user user.
     * @return users.
     * @throws Exception error
     */
    User updatePassword(User user) throws Exception;

    /**
     * Method to get list of required parameter of user.
     *
     * @param id user id
     * @return users
     * @throws Exception if error occurs
     */
    User findByUserValidList(Long id) throws Exception;

    /**
     * Find all user by domain. departmetn id and isActive status.
     *
     * @param domainId of the user.
     * @param isActive status of the user.
     * @param departmentId of the user.
     * @param domainAdmin type of the user.
     * @return user.
     * @throws Exception if error occurs.
     */
    List<User> findAllByDomainDepartmentIdUserTypeAndIsActive(Long domainId, Boolean isActive, Long departmentId,
            UserType domainAdmin) throws Exception;

    /**
     * Get user by uuid.
     * @param uuid unique id of user.
     * @return user
     * @throws Exception unhandled error.
     */
    User findByUuId(String uuid) throws Exception;

    /**
     * Find the user type based on the isActive status.
     *
     * @param rootAdmin for user type.
     * @param isActive user status Active/Inactive
     * @throws Exception error occur
     * @return users.
     */
    User findAllByUserTypeAndIsActive(Boolean isActive, UserType rootAdmin);

    /**
     * Update user to suspended status.
     *
     * @param user reference of the user.
     * @return user
     * @throws Exception if error.
     */
    User updateSuspended(User user) throws Exception;

    /**
     * Get user details by name and status.
     *
     * @param username user name
     * @param isActive user status Active/Inactive
     * @return user
     */
    User findByUserNameAndActive(String username, Boolean isActive);

    /**
     * Get user session details.
     *
     * @param id user id
     * @return session details
     * @throws Exception Exception if error.
     */
    String findByUserSessionDetails(Long id) throws Exception;

    /**
     * Find root admin user.
     *
     * @return user.
     * @throws Exception if error occurs.
     */
    List<User> findByRootAdminUser() throws Exception;

    /**
     * Find all the user by domain.
     *
     * @param domain domain reference.
     * @return list of user.
     * @throws Exception if error occurs.
     */
    List<User> findAllByDomain(Domain domain) throws Exception;

    /**
     * Count of users for search.
     *
     * @param domainId domain id.
     * @param searchText search text.
     * @param userId user id
     * @param flag value
     * @return user
     * @throws Exception unhandled errors.
     */
    List<User> findBySearchCount(Long domainId, String searchText, Long userId, String flag) throws Exception;

}
