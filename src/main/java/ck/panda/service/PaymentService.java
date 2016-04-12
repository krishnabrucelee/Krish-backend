package ck.panda.service;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Payment;
import ck.panda.util.domain.CRUDService;

/**
 * Service class for Payment.
 *
 * This service provides basic CRUD related business actions.
 */
@Service
public interface PaymentService extends CRUDService<Payment> {

    /**
     * Get payment response and stored in our database.
     *
     * @param request payment response
     * @throws Exception if error occurs
     * @return payment.
     */
    Payment savePayment(HttpServletRequest request) throws Exception;

    /**
     * Get payment details by order no.
     *
     * @param orderNo order number.
     * @return payment
     * @throws Exception unhandled exception.
     */
    Payment getPaymentDetailByOrderNo(String orderNo) throws Exception;

}
