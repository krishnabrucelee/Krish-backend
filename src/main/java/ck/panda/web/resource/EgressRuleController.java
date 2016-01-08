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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.ComputeOffering;
import ck.panda.domain.entity.FirewallRules;
import ck.panda.service.EgressRuleService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 *
 * Egress Firewall Rule Controller.
 *
 */
@RestController
@RequestMapping("/api/egress")
@Api(value = "FirewallRuless", description = "Operations with FirewallRuless", produces = "application/json")
public class EgressRuleController extends CRUDController<FirewallRules> implements ApiController {

    /** Service reference to FirewallRules. */
    @Autowired
    private EgressRuleService egressService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new FirewallRules.", response = FirewallRules.class)
    @Override
    public FirewallRules create(@RequestBody FirewallRules egressFirewallRule) throws Exception {
        return egressService.save(egressFirewallRule);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing FirewallRules.", response = FirewallRules.class)
    @Override
    public FirewallRules read(@PathVariable(PATH_ID) Long id) throws Exception {
        return egressService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing FirewallRules.", response = FirewallRules.class)
    @Override
    public FirewallRules update(@RequestBody FirewallRules egressFirewallRule, @PathVariable(PATH_ID) Long id)
            throws Exception {
        return egressService.update(egressFirewallRule);
    }

    @Override
    public List<FirewallRules> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, FirewallRules.class);
        Page<FirewallRules> pageResponse = egressService.findAll(page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * Delete the egress FirewallRule
     *
     * @param egressFirewallRule reference of theegressFirewallRule.
     * @param id egressFirewallRule id.
     * @throws Exception error occurs.
     */
    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing compute Offering.")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDelete(@RequestBody FirewallRules egressFirewallRule, @PathVariable(PATH_ID) Long id) throws Exception {
        /** Doing Soft delete from the compute offering table. */
        egressFirewallRule = egressService.find(id);
        egressFirewallRule.setSyncFlag(true);
        egressService.softDelete(egressFirewallRule);
    }
 }
