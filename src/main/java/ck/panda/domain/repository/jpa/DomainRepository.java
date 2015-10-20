package ck.panda.domain.repository.jpa;

import ck.panda.domain.entity.Domain;

import org.springframework.data.repository.PagingAndSortingRepository;


/**
 * Jpa Repository for Domain entity.
 *
 */
public interface DomainRepository extends PagingAndSortingRepository<Domain, Long> {

}
