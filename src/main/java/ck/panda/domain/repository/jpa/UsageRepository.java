package ck.panda.domain.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;

import ck.panda.domain.entity.Usage;

/**
 * JPA repository for GuestNetwork entity.
 */
public interface UsageRepository extends PagingAndSortingRepository<Usage, Long> {

}
