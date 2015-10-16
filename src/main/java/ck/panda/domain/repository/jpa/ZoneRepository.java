package ck.panda.domain.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import ck.panda.domain.entity.Zone;

/**
 * JPA repository for Zone entity.
 */
public interface ZoneRepository extends PagingAndSortingRepository<Zone, Long> {

}