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

/** JPA repository for user CRUD operations. */
public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    /**
     * Find user by active and query.
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
    @Query(value = "select user from User user where user.userName=:userName AND user.domain=:domain)")
    User findByUserNameAndDomain(@Param("userName") String userName, @Param("domain") Domain domain);

    /**
     * Find user by department.
     *
     * @param department department object.
     * @return list of users.
     */
    @Query(value = "select user from User user where user.isActive IS TRUE AND user.department=:department")
    List<User> findByDepartment(Department department);

    /**
     * Find the user for login authentication.
     *
     * @param userName login user name
     * @param domain object
     * @return user details
     */
    @Query(value = "select user from User user where user.userName = :userName AND user.domain=:domain")
    User findByUser(@Param("userName") String userName, @Param("domain") Domain domain);

    /**
     * Find the user from account.
     *
     * @param accountId of the user.
     * @return user.
     */
    @Query(value = "select user from User user where user.accountId =:accountId")
    List<User> findByAccountId(@Param("accountId") Long accountId);

    /**
     * find all the user by domain.
     *
     * @param pageable pagination information.
     * @param domain domain object.
     * @return list of user.
     */
    @Query(value = "select user from User user where user.domain =:domain ")
    Page<User> findAllUserByDomain(Pageable pageable, @Param("domain") Domain domain);

    /**
     * find all the user by domain.
     *
     * @param domain domain object.
     * @return list of user.
     */
    @Query(value = "select user from User user where user.domain =:domain ")
    List<User> findAllUserByDomain(@Param("domain") Domain domain);
}
