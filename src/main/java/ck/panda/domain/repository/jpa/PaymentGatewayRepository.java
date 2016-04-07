package ck.panda.domain.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import ck.panda.domain.entity.PaymentGateway;

/** Repository for PaymentGateway. */
public interface PaymentGatewayRepository extends PagingAndSortingRepository<PaymentGateway, Long> {


}


