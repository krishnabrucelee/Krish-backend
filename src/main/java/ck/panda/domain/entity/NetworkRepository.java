package ck.panda.domain.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import ck.panda.domain.entity.Network;

/**
 * JPA repository for GuestNetwork entity.
 */
public interface NetworkRepository extends PagingAndSortingRepository<Network, Long> {

}
