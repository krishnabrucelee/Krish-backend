package ck.panda.service;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Payment;
import ck.panda.domain.repository.jpa.PaymentRepository;
import ck.panda.util.JsonValidator;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.exception.EntityNotFoundException;

/** Payment service implementation class. */
@Service
public class PaymentServiceImpl implements PaymentService {

    /** Payment repository reference. */
    @Autowired
    private PaymentRepository paymentRepo;

    @Override
    public Payment save(Payment payment) throws Exception {
        return paymentRepo.save(payment);
    }

    @Override
    public Payment update(Payment payment) throws Exception {
        return paymentRepo.save(payment);
    }

    @Override
    public void delete(Payment payment) throws Exception {
        paymentRepo.delete(payment);
    }

    @Override
    public void delete(Long id) throws Exception {
        paymentRepo.delete(id);
    }

    @Override
    public Payment find(Long id) throws Exception {
        Payment payment = paymentRepo.findOne(id);
        if (payment == null) {
            throw new EntityNotFoundException("error.payment.not.found");
        }
        return payment;
    }

    @Override
    public Page<Payment> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return paymentRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<Payment> findAll() throws Exception {
        return (List<Payment>) paymentRepo.findAll();
    }

    @Override
    public void savePayment(HttpServletRequest request) throws Exception {
        Payment payment = new Payment();
        payment.setApCheckStatusReply_reasonCode(JsonValidator.stringToIntegerConvertion(request.getParameter("apCheckStatusReply_reasonCode")));
        payment.setApCheckStatusReply_reconciliationID(request.getParameter("apCheckStatusReply_reconciliationID"));
        payment.setApCheckStatusReply_paymentStatus(payment.getApCheckStatusReply_paymentStatus().valueOf(request.getParameter("apCheckStatusReply_paymentStatus")));
        payment.setApCheckStatusReply_processorTransactionID(request.getParameter("apCheckStatusReply_processorTransactionID"));
        payment.setApInitiateReply_merchantURL(request.getParameter("apInitiateReply_merchantURL"));
        payment.setApInitiateReply_reasonCode(JsonValidator.stringToIntegerConvertion(request.getParameter("apInitiateReply_reasonCode")));
        payment.setApInitiateReply_reconciliationID(request.getParameter("apInitiateReply_reconciliationID"));
        payment.setDecision(request.getParameter("decision"));
        payment.setMerchantReferenceCode(request.getParameter("merchantReferenceCode"));
        payment.setReasonCode(JsonValidator.stringToIntegerConvertion(request.getParameter("reasonCode")));
        payment.setRequest_id(request.getParameter("request_id"));
        payment.setPurchaseTotals_currency(request.getParameter("purchaseTotals_currency"));
        payment.setApRefundReply_returnRef(request.getParameter("apRefundReply_returnRef"));
        payment.setApRefundReply_reasonCode(JsonValidator.stringToIntegerConvertion(request.getParameter("apRefundReply_reasonCode")));
        payment.setApRefundReply_reconciliationID(request.getParameter("apRefundReply_reconciliationID"));
        payment.setApRefundReply_dateTime(request.getParameter("apRefundReply_dateTime"));
        payment.setApRefundReply_amount(JsonValidator.stringToDoubleConvertion(request.getParameter("apRefundReply_amount")));
        paymentRepo.save(payment);
    }

}
