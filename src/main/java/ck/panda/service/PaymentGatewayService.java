package ck.panda.service;

import org.springframework.stereotype.Service;
import ck.panda.domain.entity.PaymentGateway;
import ck.panda.util.domain.CRUDService;

/**
 * Service interface for PaymentGateway entity.
 *
 */
@Service
public interface PaymentGatewayService extends CRUDService<PaymentGateway> {

}

