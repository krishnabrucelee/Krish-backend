package ck.panda.web.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.wordnik.swagger.annotations.Api;
import ck.panda.util.PingService;
import ck.panda.util.web.ApiController;

/**
 * Department controller.
 *
 */
@RestController
@RequestMapping("/api/usage")
@Api(value = "Usages", description = "Operations with usages", produces = "application/json")
public class UsageController implements ApiController {

    /** Service for Ping.*/
    @Autowired
    private PingService pingService;

    /**
     * Find the list of active usages.
     *
     * @param fromDate Start Date
     * @param toDate End Date
     * @param groupingType grouping type
     * @param domainUuid domain uuid
     * @return projects projectlist.
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
     * @param type type
     * @param domainUuid domain uuid
     * @param status status
     * @return projects project list.
     * @throws Exception error occurs.
     */
    @RequestMapping(value = "invoice/listByDomain", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected String listInvoiceByDomainId(@RequestParam("type") String type,@RequestParam("domainUuid") String domainUuid,
            @RequestParam("status") String status) throws Exception {
        if (type.equals("invoice")) {
            return pingService.listInvoiceByDomainId(domainUuid, status,type);
        } else {
            return pingService.listPaymentByDomainId(domainUuid, status,type);
        }
    }

    /**
     * Find the list of invoices.
     *
     * @param type type
     * @return projects project list.
     * @throws Exception error occurs.
     */
    @RequestMapping(value = "invoice", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected String listInvoice(@RequestParam("type") String type) throws Exception {
        if (type.equals("invoice")) {
            return pingService.listInvoice(type);
        } else {
            return pingService.listPayment(type);
        }
    }

}
