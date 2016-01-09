package ck.panda.web.resource;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.FirewallRules;
import ck.panda.domain.entity.PortForwarding;
import ck.panda.service.PortForwardingService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * Port Forwarding Controller.
 *
 */
@RestController
@RequestMapping("/api/portforwarding")
@Api(value = "PortForwarding", description = "Operations with PortForwarding", produces = "application/json")
public class PortForwardingController extends CRUDController<PortForwarding> implements ApiController {

    /** Service reference to Port Forwarding. */
    @Autowired
    private PortForwardingService portForwardingService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new Port Forwarding.", response = PortForwarding.class)
    @Override
    public PortForwarding create(@RequestBody PortForwarding portForwarding) throws Exception {
        portForwarding.setSyncFlag(true);
        return portForwardingService.save(portForwarding);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing Port Forwarding.", response = PortForwarding.class)
    @Override
    public PortForwarding read(@PathVariable(PATH_ID) Long id) throws Exception {
        return portForwardingService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing port Forwarding.", response = PortForwarding.class)
    @Override
    public PortForwarding update(@RequestBody PortForwarding portForwarding, @PathVariable(PATH_ID) Long id)
            throws Exception {
        portForwarding.setSyncFlag(true);
        return portForwardingService.update(portForwarding);
    }

    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing port Forwarding.")
    @Override
    public void delete(@PathVariable(PATH_ID) Long id) throws Exception {
        PortForwarding portForwarding = portForwardingService.find(id);
        portForwarding.setSyncFlag(true);
        portForwardingService.softDelete(portForwarding);
    }

    @Override
    public List<PortForwarding> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, FirewallRules.class);
        Page<PortForwarding> pageResponse = portForwardingService.findAll(page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }
}
