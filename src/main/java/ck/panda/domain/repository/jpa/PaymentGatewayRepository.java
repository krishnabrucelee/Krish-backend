package ck.panda.domain.repository.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import ck.panda.domain.entity.PaymentGateway;

/** Repository for PaymentGateway. */
public interface PaymentGatewayRepository extends PagingAndSortingRepository<PaymentGateway, Long> {

    /**
     * Get payment gateway details by status.
     *
     * @param isActive true/false.
     * @return payment gateway.
     */
    @Query(value = "SELECT pay FROM PaymentGateway pay WHERE pay.isActive = :isActive")
    PaymentGateway findByStatus(@Param("isActive") Boolean isActive);
}
