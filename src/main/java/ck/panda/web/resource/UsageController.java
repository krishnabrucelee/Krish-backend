package ck.panda.web.resource;

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

    @Autowired
    private PingService pingService;

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
    protected String listInvoiceByDomainId(@RequestParam("sortBy") String sortBy, @RequestParam("domainUuid") String domainUuid,
            @RequestParam("status") String status,
            @RequestHeader(value = RANGE) String range, @RequestParam(required = false) Integer limit) throws Exception {
        return pingService.listInvoiceByDomainId(sortBy, domainUuid, status, range, limit.toString());
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
    protected String listInvoice(@RequestParam("sortBy") String sortBy, @RequestHeader(value = RANGE) String range, @RequestParam(required = false) Integer limit) throws Exception {
        return pingService.listInvoice(sortBy, range, limit.toString());
    }

}
