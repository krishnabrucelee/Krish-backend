package ck.panda.domain.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.EventLiterals;

/**
 * Jpa Repository for Event Literals entity.
 *
 */
@Service
public interface EventLiteralsRepository extends PagingAndSortingRepository<EventLiterals, Long> {

}
