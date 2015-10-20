package ck.panda.domain.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import ck.panda.domain.entity.NetworkOffering;

/**
 * JPA repository for Zone entity.
 */
public interface NetworkOfferingRepository extends PagingAndSortingRepository<NetworkOffering, Long>{

}
