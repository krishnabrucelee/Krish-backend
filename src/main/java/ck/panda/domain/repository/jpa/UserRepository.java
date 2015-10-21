package ck.panda.domain.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import ck.panda.domain.entity.User;

/** JPA repository for user CRUD operations. */
public interface UserRepository extends PagingAndSortingRepository<User, Long> {

}
