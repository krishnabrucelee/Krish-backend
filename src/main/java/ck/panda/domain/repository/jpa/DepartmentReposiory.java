package ck.panda.domain.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;

import ck.panda.domain.entity.Department;

/**
 * Jpa Repository for Subject entity.
 */
public interface DepartmentReposiory extends PagingAndSortingRepository<Department, Long> {

}
