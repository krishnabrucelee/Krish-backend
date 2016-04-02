package ck.panda.web.resource;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.Payment;
import ck.panda.service.PaymentService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/** Payment controller. */
@RestController
@RequestMapping("/api/payment")
@Api(value = "Payments", description = "Operations with Payments", produces = "application/json")
public class PaymentController extends CRUDController<Payment> implements ApiController {

    /** Service reference to Payment. */
    @Autowired
    private PaymentService paymentService;

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
     * @throws Exception if error occurs
     */
    @RequestMapping(value = "/responseGateway", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void savePayment(HttpServletRequest request) throws Exception {
        paymentService.savePayment(request);
    }

}
