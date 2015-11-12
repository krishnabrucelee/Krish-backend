package ck.panda.domain.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Host;

/**
 * Jpa Repository for Host entity.
 *
 */
@Service
public interface HostRepository extends PagingAndSortingRepository<Host, Long> {

}
