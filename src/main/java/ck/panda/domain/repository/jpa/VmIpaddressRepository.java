package ck.panda.domain.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.Pod;
import ck.panda.domain.entity.VmIpaddress;

/**
 * Jpa Repository for Pod entity.
 *
 */
@Service
public interface VmIpaddressRepository extends PagingAndSortingRepository<VmIpaddress, Long> {
	
}