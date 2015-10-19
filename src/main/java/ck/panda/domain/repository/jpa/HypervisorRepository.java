package ck.panda.domain.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;

import ck.panda.domain.entity.Hypervisor;

/**
 * JPA repository for Hypervisor entity.
 */
public interface HypervisorRepository extends PagingAndSortingRepository<Hypervisor, Long> {

}
