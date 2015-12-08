package ck.panda.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import com.google.common.base.Optional;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.User;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.domain.vo.PagingAndSorting;

/** The UserService interface used for to perform CRUD operations and basic API's related business logic. */
@Service
public interface UserService extends CRUDService<User> {

    /**
     * To get list of users from cloudstack server.
     *
     * @return user list from server
     * @throws Exception unhandled errors.
     */
    List<User> findAllFromCSServerByDomain() throws Exception;

    /**
     * @param query search term.
     * @return list of user.
     * @throws Exception
     */
    List<User> findByName(String query) throws Exception;

    /**
     * To get list of users by department.
     *
     * @param departmentId department id.
     * @return list of user.
     * @throws Exception if error occurs.
     */
    List<User> findByDepartment(Long departmentId) throws Exception;

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
     User findByUser(Optional<String> userName, Optional<String> password, Optional<String> domainName) throws Exception;


     /**
      * Method to soft delete user.
      *
      * @param user user object.
      * @return user.
      * @throws Exception if error occurs.
      */
     User softDelete(User user) throws Exception;

      /**
      * Find user by account Id.
      *
      * @param accountId of the user.
      * @return account.
      * @throws Exception if error occurs.
      */
     List<User> findByAccountId(Long accountId) throws Exception;

     /**
      * Find all the user by domain.
      *
      * @param pagingAndSorting paging and sorting information.
      * @return list of user.
      * @throws Exception if error occurs.
      */
     Page<User> findAllUserByDomain(PagingAndSorting pagingAndSorting) throws Exception;

     /**
      * Find all the user by domain.
      *
      * @return list of user.
      * @throws Exception if error occurs.
      */
     List<User> findAllUserByDomain() throws Exception;

     /**
      * Find all the user by domain.
      *
      * @return list of user.
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
}
