package ck.panda.domain.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import ck.panda.domain.entity.NetworkOfferingServiceList;

/**
 * JPA repository for NetworkOfferingServiceList entity.
 */
public interface NetworkOfferingServiceListRepository extends PagingAndSortingRepository<NetworkOfferingServiceList, Long> {

}
