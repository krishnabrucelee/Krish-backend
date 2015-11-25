package ck.panda.domain.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;

import ck.panda.domain.entity.AccountUsage;

/**
 * JPA repository for GuestNetwork entity.
 */
public interface AccountUsageRepository extends PagingAndSortingRepository<AccountUsage, Long> {

}

