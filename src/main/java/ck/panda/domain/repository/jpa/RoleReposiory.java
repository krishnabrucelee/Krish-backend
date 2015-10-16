package ck.panda.domain.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;

import ck.panda.domain.entity.Role;

/**
 * Jpa Repository for Subject entity.
 */
public interface RoleReposiory extends PagingAndSortingRepository<Role, Long> {

}
