package ck.panda.domain.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import ck.panda.domain.entity.Project;

/**
 * JPA repository for Project entity.
 * Project related crud and pagination are handled by this Repository.
 */
public interface ProjectRepository extends PagingAndSortingRepository<Project, Long> {

}
