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
import ck.panda.domain.entity.PaymentGateway;
import ck.panda.service.PaymentGatewayService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * PaymentGateway controller.
 *
 */
@RestController
@RequestMapping("/api/paymentgateway")
@Api(value = "PaymentGateway", description = "Operations with PaymentGateway", produces = "application/json")
public class PaymentGatewayController extends CRUDController<PaymentGateway> implements ApiController {

	/** Service reference to PaymentGateway. */
	@Autowired
	private PaymentGatewayService paymentService;

	@ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new PaymentGateway.", response = PaymentGateway.class)
	@Override
	public PaymentGateway create(@RequestBody PaymentGateway cost) throws Exception {
		return paymentService.save(cost);
	}

	@ApiOperation(value = SW_METHOD_READ, notes = "Read an existing PaymentGateway.", response = PaymentGateway.class)
	@Override
	public PaymentGateway read(@PathVariable(PATH_ID) Long id) throws Exception {
		return paymentService.find(id);
	}

	@ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing PaymentGateway.", response = PaymentGateway.class)
	@Override
	public PaymentGateway update(@RequestBody PaymentGateway cost, @PathVariable(PATH_ID) Long id) throws Exception {
		return paymentService.update(cost);
	}

	@Override
	public List<PaymentGateway> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
			@RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, PaymentGateway.class);
		Page<PaymentGateway> pageResponse = paymentService.findAll(page);
		response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
		return pageResponse.getContent();
	}

	/**
	 * List all payment gateway details.
	 *
	 * @return payment gateway.
	 * @throws Exception
	 *             unhandled errors.
	 */
	@RequestMapping(value = "paylist", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	protected List<PaymentGateway> getSearch() throws Exception {
		return paymentService.findAll();
	}
}
