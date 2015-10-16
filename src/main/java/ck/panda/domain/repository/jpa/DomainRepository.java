package ck.panda.domain.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;

import ck.panda.domain.entity.Domain;

/**
 * Jpa Repository for Domain entity.
 *
 * @author Krishna<krishnakumar@assistanz.com>
 */
public interface DomainRepository extends PagingAndSortingRepository<Domain, Long> {

}
