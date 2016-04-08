package ck.panda.web.resource;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.wordnik.swagger.annotations.Api;

import ck.panda.domain.entity.Payment;
import ck.panda.domain.entity.Payment.PaymentStatus;
import ck.panda.service.PaymentService;

@Controller
@RequestMapping("/panda/payment")
@Api(value = "Payments", description = "Operations with Payments", produces = "application/json")
public class AliPayController {
	/** Service reference to Payment. */
    @Autowired
    private PaymentService paymentService;

    /** The panda url. */
    @Value(value = "${panda.url}")
    private String pandaUrl;

	/**
     * Get payment response from Alipay payment gateway for notify.
     *
     * @param request http servlet request
     * @throws Exception if error occurs
     */
    @RequestMapping(value = "/notify", method = RequestMethod.GET)
    public ModelAndView nofity(HttpServletRequest request) throws Exception {
    	Payment payment = paymentService.savePayment(request);
    	payment.setUrl(pandaUrl);
    	if(payment == null){
    		return new ModelAndView("payment-invalid", "payment", payment);
    	}
    	if (payment.getPaymentStatus().equals(PaymentStatus.ABANDONED)){
    		return new ModelAndView("payment-failure", "payment", payment);
    	}
    	return new ModelAndView("payment-notify", "payment", payment);
    }

    /**
     * Get payment response from Alipay payment gateway for return url.
     *
     * @param request http servlet request
     * @throws Exception if error occurs
     */
    @RequestMapping(value = "/return", method = RequestMethod.GET)
    public ModelAndView getReturnUrl(HttpServletRequest request) throws Exception {
    	Payment payment = paymentService.savePayment(request);
    	payment.setUrl(pandaUrl);
    	if(payment == null){
    		return new ModelAndView("payment-invalid", "payment", payment);
    	}
    	if (payment.getPaymentStatus().equals(PaymentStatus.ABANDONED)){
    		return new ModelAndView("payment-failure", "payment", payment);
    	}
    	return new ModelAndView("payment-success", "payment", payment);
    }

}
