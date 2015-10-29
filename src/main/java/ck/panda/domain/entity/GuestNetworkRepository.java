package ck.panda.domain.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import ck.panda.domain.entity.GuestNetwork;

/**
 * JPA repository for GuestNetwork entity.
 */
public interface GuestNetworkRepository extends PagingAndSortingRepository<GuestNetwork, Long>{

}
