package ck.panda.domain.repository.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import ck.panda.domain.entity.Payment;

/**
 * JPA Repository for Payment entity.
 */
public interface PaymentRepository extends PagingAndSortingRepository<Payment, Long> {

	/**
	 * Get payment details by order id.
	 *
	 * @param orderNo order id.
	 * @return payment.
	 */
	@Query(value="SELECT pay FROM Payment pay WHERE pay.orderId = :orderNo")
	Payment getPaymentDetailByOrderNo(@Param("orderNo") String orderNo);
}
