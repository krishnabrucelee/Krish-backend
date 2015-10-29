package ck.panda.domain.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import ck.panda.domain.entity.User;

/** JPA repository for user CRUD operations. */
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
	/**
	 * Find user by active and query.
	 * @param query
	 * @return
	 */
	@Query(value = "select user from User user where user.isActive IS TRUE and lower(user.userName) LIKE '%' || lower(:query) || '%' ")
    List<User> findAllByActive(@Param("query") String query);

}
