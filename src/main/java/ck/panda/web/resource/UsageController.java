package ck.panda.web.resource;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.wordnik.swagger.annotations.Api;
import ck.panda.domain.entity.User;
import ck.panda.service.ConvertEntityService;
import ck.panda.util.PingService;
import ck.panda.util.TokenDetails;
import ck.panda.util.web.ApiController;

/**
 * Department controller.
 *
 */
@RestController
@RequestMapping("/api/usage")
@Api(value = "Usages", description = "Operations with usages", produces = "application/json")
public class UsageController implements ApiController {

    @Autowired
    private PingService pingService;
    
    /** Service reference to Conver entity . */
    @Autowired
    private ConvertEntityService convertEntityService;
    
    /** Autowired TokenDetails. */
    @Autowired
    private TokenDetails tokenDetails;

    /**
     * Find the list of active usages.
     *
     * @return projects project list.
     * @throws Exception error occurs.
     */
    @RequestMapping(value = "listUsageByPeriod", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected String getUsageStatistics(@RequestParam("fromDate") String fromDate, @RequestParam("toDate") String toDate,
            @RequestParam("groupingType") String groupingType, @RequestParam("domainUuid") String domainUuid) throws Exception {
        return pingService.getUsageStatistics(fromDate, toDate, groupingType, domainUuid);
    }

    /**
     * Find the list of invoices by domain.
     *
     * @return projects project list.
     * @throws Exception error occurs.
     */
    @RequestMapping(value = "invoice/listByDomain", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected String listInvoiceByDomainId(@RequestParam("type") String type, @RequestParam("sortBy") String sortBy, @RequestParam("domainUuid") String domainUuid,
            @RequestParam("status") String status,
            @RequestHeader(value = RANGE) String range, @RequestParam(required = false) Integer limit) throws Exception {
        if(type.equals("invoice"))
            return pingService.listInvoiceByDomainId(sortBy, domainUuid, status, range, limit.toString());
        else {
            return pingService.listPaymentByDomainId(sortBy, domainUuid, status, range, limit.toString());
        }
    }

    /**
     * Find the list of invoices.
     *
     * @return projects project list.
     * @throws Exception error occurs.
     */
    @RequestMapping(value = "invoice", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected String listInvoice(@RequestParam("type") String type, @RequestParam("sortBy") String sortBy, @RequestHeader(value = RANGE) String range, @RequestParam(required = false) Integer limit) throws Exception {
        if(type.equals("invoice")) {
            return pingService.listInvoice(sortBy, range, limit.toString());
        } else {
            return pingService.listPayment(sortBy, range, limit.toString());
        }
    }
    
    /**
     * Get the usage total by domain.
     *
     * @throws Exception if error occurs.
     *
     */
    @RequestMapping(value = "usageTotalByDomain", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String getUsageTotalForAYearByDomain()
                    throws Exception {
    	User user = convertEntityService.getOwnerById(Long.valueOf(tokenDetails.getTokenDetails("id")));
        return pingService.getUsageTotalForAYearByDomain(user.getDomain().getUuid());
    }
    
    /**
     * Get the usage details by project.
     *
     * @throws Exception if error occurs.
     *
     */
    @RequestMapping(value = "usageByProject", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String getUsageProjectTotalByDomain(HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
    	User user = convertEntityService.getOwnerById(Long.valueOf(tokenDetails.getTokenDetails("id")));
        return pingService.getUsageTotalByProjectAndDomain(user.getDomain().getUuid());
    }
    
    /**
     * Get the usage details by account.
     *
     * @throws Exception if error occurs.
     *
     */
    @RequestMapping(value = "usageByAccount", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String getUsageAccountTotalByDomain(HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
    	User user = convertEntityService.getOwnerById(Long.valueOf(tokenDetails.getTokenDetails("id")));
    	return pingService.getUsageTotalByAccountAndDomain(user.getDomain().getUuid());
    }

}
