package ck.panda.domain.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.VPNCustomerGateway;

/**
 * Jpa Repository for vpn customer gateway entity.
 *
 */
@Service
public interface VPNCustomerGatewayRepository extends PagingAndSortingRepository<VPNCustomerGateway, Long> {

}