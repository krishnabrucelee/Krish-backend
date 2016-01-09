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
import ck.panda.domain.entity.IpAddress;
import ck.panda.domain.entity.Network;
import ck.panda.service.IpaddressService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * IpAddress Controller.
 *
 */
@RestController
@RequestMapping("/api/ipAddresses")
@Api(value = "IpAddresss", description = "Operations with IpAddresss", produces = "application/json")
public class IpaddressController extends CRUDController<IpAddress> implements ApiController {

    /** Service reference to IpAddress. */
    @Autowired
    private IpaddressService ipAddressService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new IpAddress.", response = IpAddress.class)
    @Override
    public IpAddress create(@RequestBody IpAddress ipAddress) throws Exception {
        ipAddress.setSyncFlag(true);
        return ipAddressService.save(ipAddress);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing IpAddress.", response = IpAddress.class)
    @Override
    public IpAddress read(@PathVariable(PATH_ID) Long id) throws Exception {
        return ipAddressService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing IpAddress.", response = IpAddress.class)
    @Override
    public IpAddress update(@RequestBody IpAddress ipAddress, @PathVariable(PATH_ID) Long id) throws Exception {
        ipAddress.setSyncFlag(true);
        return ipAddressService.update(ipAddress);
    }

    @Override
    public List<IpAddress> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, IpAddress.class);
        Page<IpAddress> pageResponse = ipAddressService.findAllByActive(page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * Get list of ipaddress with in the network.
     *
     * @param sortBy Asc/Desc
     * @param range range per page.
     * @param limit limit per page.
     * @param request servlet request
     * @param response servlet response
     * @return list of ipaddress.
     * @throws Exception unhandled errors.
     */
    @RequestMapping(value = "/iplist", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<IpAddress> listByNetwork(@RequestParam("network") Long networkId, @RequestParam String sortBy,
            @RequestHeader(value = RANGE) String range, @RequestParam Integer limit, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, IpAddress.class);
        Page<IpAddress> pageResponse = ipAddressService.findByNetwork(networkId, page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    @RequestMapping(value = "dissociate/{id}", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected IpAddress detachIpAddress(@RequestBody IpAddress ipAddress, @PathVariable(PATH_ID) Long id)
            throws Exception {
        ipAddress.setSyncFlag(true);
        return ipAddressService.dissocitateIpAddress(ipAddress);
    }


    /**
     * Get list of ipaddresses with pagination object.
     *
     * @param networkId network's id.
     * @param sortBy asc/desc.
     * @param range range per page.
     * @param limit limit rows per page.
     * @param request servlet request.
     * @param response servlet response.
     * @return list of ipaddress.
     * @throws Exception unhandled exception.
     */
    @RequestMapping(value = "/iplist", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<IpAddress> listbyNetwork(@RequestParam("network") Long networkId, @RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, IpAddress.class);
        Page<IpAddress> pageResponse = ipAddressService.findByNetwork(networkId, page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * Get instance with latest state update.
     *
     * @param network network object.
     * @throws Exception if error occurs.
     * @return ipaddress list.
     */
    @RequestMapping(value = "/acquireip", method = RequestMethod.PUT, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<IpAddress> handleEventWithIPAddress(@RequestParam("network") Long networkId) throws Exception {
        return ipAddressService.acquireIP(networkId);
    }

}
