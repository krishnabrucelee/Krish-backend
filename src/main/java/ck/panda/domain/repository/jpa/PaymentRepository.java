package ck.panda.domain.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import ck.panda.domain.entity.Payment;

/**
 * JPA Repository for Payment entity.
 */
public interface PaymentRepository extends PagingAndSortingRepository<Payment, Long> {

}
