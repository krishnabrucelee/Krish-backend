package ck.panda.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.PaymentGateway;
import ck.panda.domain.repository.jpa.PaymentGatewayRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;

@Service
public class PaymentGatewayServiceImpl  implements PaymentGatewayService {

     /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** PaymentGateway repository reference. */
    @Autowired
    private PaymentGatewayRepository paymentRepo;

    /** PaymentGateway string literal. */
    public static final String ORGANIZATION = "payment";

    @Override
    public PaymentGateway save(PaymentGateway payment) throws Exception {
        Errors errors = validator.rejectIfNullEntity(ORGANIZATION, payment);
        errors = validator.validateEntity(payment, errors);
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
        	payment.setIsActive(true);
            return paymentRepo.save(payment);
        }
    }

    @Override
    public PaymentGateway update(PaymentGateway payment) throws Exception {
        return paymentRepo.save(payment);
    }

    @Override
    public void delete(PaymentGateway payment) throws Exception {
        paymentRepo.delete(payment);
    }

    @Override
    public void delete(Long id) throws Exception {
        paymentRepo.delete(id);
    }

    @Override
    public Page<PaymentGateway> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return paymentRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<PaymentGateway> findAll() throws Exception {
        return (List<PaymentGateway>) paymentRepo.findAll();
    }

    @Override
    public PaymentGateway find(Long id) throws Exception {
        return paymentRepo.findOne(id);
    }

	@Override
	public PaymentGateway getActivePaymentGateway(Boolean isActive) {
		return paymentRepo.findByStatus(isActive);
	}
}


