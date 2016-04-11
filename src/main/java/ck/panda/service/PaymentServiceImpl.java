package ck.panda.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import ck.panda.constants.PingConstants;
import ck.panda.domain.entity.Payment;
import ck.panda.domain.entity.Payment.PaymentStatus;
import ck.panda.domain.repository.jpa.PaymentRepository;
import ck.panda.payment.util.AlipayNotify;
import ck.panda.util.PingService;
import ck.panda.util.audit.DateTimeService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.exception.EntityNotFoundException;

/** Payment service implementation class. */
@Service
public class PaymentServiceImpl implements PaymentService {

	/** Payment repository reference. */
	@Autowired
	private PaymentRepository paymentRepo;

	/** Date time service reference. */
	@Autowired
	private DateTimeService dateTimeService;

	/** Mr.ping service reference. */
	@Autowired
	private PingService pingService;

	/** Convert Entity Service reference. */
	@Autowired
	private ConvertEntityService convertEntityService;

	/** Payment repository reference. */
	@Autowired
	private AlipayNotify aliPayNotify;

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
	public Payment savePayment(HttpServletRequest request) {
		Payment payment = new Payment();
		Map<String, String> params = new HashMap<String, String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
			}
			params.put(name, valueStr);
		}
		try {
			String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
			String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
			String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
			payment = getPaymentDetailByOrderNo(out_trade_no);
			if (payment == null) {
				payment = new Payment();
			}
			payment.setOrderId(out_trade_no);
			payment.setTransactionId(trade_no);
			if (aliPayNotify.verify(params)) {
				// Authentication is successful
				if (trade_status.equals("TRADE_FINISHED")) {
					payment.setTradeStatus(trade_status);
					payment.setPaymentStatus(PaymentStatus.COMPLETED);
					// Note :
					// Refund after the date exceeds the refund period ( such as
					// three months refundable ) , Alipay system sends the
					// transaction status notifications
				} else if (trade_status.equals("TRADE_SUCCESS")) {
					// Determine whether the sum of the order processing has
					// been done in the merchant site
					// If not done processing , according to the order number
					// (out_trade_no) found in the merchant's website order
					// system detailed pen orders and execute business
					// operational procedures
					// Make sure to request the judgment total_fee , seller_id
					// notification acquired total_fee , seller_id a consistent
					// If there is done processing , merchant services program
					// is not executed
					payment.setTradeStatus(trade_status);
					payment.setPaymentStatus(PaymentStatus.COMPLETED);
				}
				payment.setTradeStatus(trade_status);
				payment.setIsSuccess(new String(request.getParameter("is_success").getBytes("ISO-8859-1"), "UTF-8"));
				payment.setPaymentType(
						new String(request.getParameter("payment_type").getBytes("ISO-8859-1"), "UTF-8"));
				payment.setService(new String(request.getParameter("exterface").getBytes("ISO-8859-1"), "UTF-8"));
				payment.setNotifyId(new String(request.getParameter("notify_id").getBytes("ISO-8859-1"), "UTF-8"));
				payment.setNotifyTime(new String(request.getParameter("notify_time").getBytes("ISO-8859-1"), "UTF-8"));
				payment.setBuyerEmail(new String(request.getParameter("buyer_email").getBytes("ISO-8859-1"), "UTF-8"));
				payment.setBuyerId(new String(request.getParameter("buyer_id").getBytes("ISO-8859-1"), "UTF-8"));
				payment.setCreatedDateTime(dateTimeService.getCurrentDateAndTime());
				payment.setDomainId(convertEntityService.getDomainId(
						new String(request.getParameter("extra_common_param").getBytes("ISO-8859-1"), "UTF-8")));
				payment.setTotalFee(new String(request.getParameter("total_fee").getBytes("ISO-8859-1"), "UTF-8"));
				payment.setSubject(new String(request.getParameter("subject").getBytes("ISO-8859-1"), "UTF-8"));
				JSONObject optional = new JSONObject();
				SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = dt.parse(payment.getNotifyTime());
				optional.put("invoiceNumber", payment.getSubject());
				optional.put("paymentMethod", "ALI_PAY");
				optional.put("transactionReference", payment.getTransactionId());
				optional.put("paidOn", date.getTime());
				optional.put("status", "PAID");
				pingService.updateInvoiceToPing(optional);
			} else {
				payment.setPaymentStatus(PaymentStatus.FAILURE);
				payment.setDomainId(convertEntityService.getDomainId(
						new String(request.getParameter("extra_common_param").getBytes("ISO-8859-1"), "UTF-8")));
				payment.setTotalFee(new String(request.getParameter("total_fee").getBytes("ISO-8859-1"), "UTF-8"));
				payment.setSubject(new String(request.getParameter("subject").getBytes("ISO-8859-1"), "UTF-8"));
				payment.setService(new String(request.getParameter("exterface").getBytes("ISO-8859-1"), "UTF-8"));
				JSONObject optional = new JSONObject();
				optional.put("invoiceNumber", payment.getSubject());
				optional.put("paymentMethod", "ALI_PAY");
				optional.put("transactionReference", payment.getTransactionId());
				SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = dt.parse(payment.getNotifyTime());
				optional.put("paidOn", date.getTime());
				optional.put("status", "UNPAID");
				pingService.updateInvoiceToPing(optional);
			}
		} catch (Exception ce) {
			payment = new Payment();
			return payment;
		}
		return paymentRepo.save(payment);
	}

	@Override
	public Payment getPaymentDetailByOrderNo(String orderNo) throws Exception {
		return paymentRepo.getPaymentDetailByOrderNo(orderNo);
	}

}
