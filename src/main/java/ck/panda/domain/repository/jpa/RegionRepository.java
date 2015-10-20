package ck.panda.domain.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import ck.panda.domain.entity.Region;

/**
 * Jpa Repository for Region entity.
 *
 */
public interface RegionRepository extends PagingAndSortingRepository<Region, Long> {

}
