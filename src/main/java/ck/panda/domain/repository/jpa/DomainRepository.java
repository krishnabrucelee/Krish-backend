package ck.panda.domain.repository.jpa;

import ck.panda.domain.entity.Domain;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;


/**
 * Jpa Repository for Domain entity.
 *
 */
@Service
public interface DomainRepository extends PagingAndSortingRepository<Domain, Long> {

}
