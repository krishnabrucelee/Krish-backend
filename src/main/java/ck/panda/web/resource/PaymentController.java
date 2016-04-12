package ck.panda.web.resource;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import ck.panda.constants.GenericConstants;
import ck.panda.constants.PaymentConstants;
import ck.panda.domain.entity.Payment;
import ck.panda.domain.entity.PaymentGateway;
import ck.panda.payment.util.AlipaySubmit;
import ck.panda.service.PaymentGatewayService;
import ck.panda.service.PaymentService;
import ck.panda.util.PingService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.exception.CustomGenericException;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/** Payment controller. */
@Controller
@RequestMapping("/api/payment")
@Api(value = "Payments", description = "Operations with Payments", produces = "application/json")
public class PaymentController extends CRUDController<Payment> implements ApiController {

    /** Service reference to Payment. */
    @Autowired
    private PaymentService paymentService;

    /** Mr.ping service reference. */
    @Autowired
    private PingService pingService;

    /** Alipay service reference. */
    @Autowired
    private AlipaySubmit alipay;

    /** Payment Gateway Service reference. */
    @Autowired
    private PaymentGatewayService paymentGatewayService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new payment.", response = Payment.class)
    @Override
    public Payment create(@RequestBody Payment payment) throws Exception {
        return paymentService.save(payment);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing payment.", response = Payment.class)
    @Override
    public Payment read(@PathVariable(PATH_ID) Long id) throws Exception {
        return paymentService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing payment.", response = Payment.class)
    @Override
    public Payment update(@RequestBody Payment payment, @PathVariable(PATH_ID) Long id) throws Exception {
        return paymentService.update(payment);
    }

    @Override
    public List<Payment> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Payment.class);
        Page<Payment> pageResponse = paymentService.findAll(page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * Get payment response from Alipay payment gateway.
     *
     * @param request http servlet request
     * @throws Exception if error occurs.
     * @return payment result.
     */
    @RequestMapping(value = "/pay", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String makePayment(HttpServletRequest request) throws Exception {
        PaymentGateway paymentGateway = paymentGatewayService.getActivePaymentGateway(true);
        if (paymentGateway == null) {
            throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, "Payment gateway not yet configured");
        }
        if (pingService.apiConnectionCheck(null)) {
            throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, "Mr.Ping Server is not Reachable. please try again later.");
        }
        Map<String, String> sParaTemp = new HashMap<String, String>();
        sParaTemp.put("service", paymentGateway.getServiceType().name());
        sParaTemp.put("partner", paymentGateway.getPartner());
        sParaTemp.put("seller_id", paymentGateway.getSellerEmail());
        sParaTemp.put("_input_charset", PaymentConstants.INPUT_CHARSET);
        sParaTemp.put("payment_type", PaymentConstants.PAYMENT_TYPE);
        sParaTemp.put("notify_url", paymentGateway.getNotifyURL());
        sParaTemp.put("return_url", paymentGateway.getReturnURL());
        sParaTemp.put("anti_phishing_key", PaymentConstants.ANTI_PHISHING_KEY);
        sParaTemp.put("exter_invoke_ip", PaymentConstants.EXTER_INVOKE_IP);
        SimpleDateFormat dt = new SimpleDateFormat("yyyyMMddhhmmss");
        String out_trade_no = request.getParameter("invoice") + dt.format(new Date());
        sParaTemp.put("out_trade_no", out_trade_no);
        sParaTemp.put("subject", request.getParameter("invoice"));
        sParaTemp.put("total_fee", request.getParameter("totalfee"));
        sParaTemp.put("body", request.getParameter("invoice"));
        sParaTemp.put("extra_common_param", request.getParameter("client"));
        String sHtmlText = alipay.buildRequest(sParaTemp, "get", "confirm");
        return sHtmlText;
    }

}
