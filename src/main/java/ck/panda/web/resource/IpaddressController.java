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
     * Get the detached IP address by uuid.
     *
     * @param ipUuid of the ip address
     * @return ip address
     * @throws Exception unhandled exception.
     */
    @RequestMapping(value = "/dissociate", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected IpAddress detachIpAddress(@RequestParam("ipuuid") String ipUuid)
            throws Exception {
        return ipAddressService.dissocitateIpAddress(ipUuid);
    }

    /**
     * Get the detached IP address by uuid.
     *
     * @param ipUuid of the ip address
     * @return ip address
     * @throws Exception unhandled exception.
     */
    @RequestMapping(value = "/vpc/dissociate", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected IpAddress detachIpAddressFoVPC(@RequestParam("ipuuid") String ipUuid)
            throws Exception {
        return ipAddressService.dissocitateIpAddressForVPC(ipUuid);
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
    public List<IpAddress> listbyNetwork(@RequestParam("network") Long networkId, @RequestParam String sortBy,
            @RequestHeader(value = RANGE) String range, @RequestParam Integer limit, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, IpAddress.class);
        Page<IpAddress> pageResponse = ipAddressService.findByNetwork(networkId, page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * Get list of ipaddresses with pagination object.
     *
     * @param vpcId vpc id.
     * @param sortBy asc/desc.
     * @param range range per page.
     * @param limit limit rows per page.
     * @param request servlet request.
     * @param response servlet response.
     * @return list of ipaddress.
     * @throws Exception unhandled exception.
     */
    @RequestMapping(value = "/vpc/iplist", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<IpAddress> listbyVpck(@RequestParam("vpc") Long vpcId, @RequestParam String sortBy,
            @RequestHeader(value = RANGE) String range, @RequestParam Integer limit, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, IpAddress.class);
        Page<IpAddress> pageResponse = ipAddressService.findAllByVpc(vpcId, page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * Get new ip from zone for current network.
     *
     * @param networkId network id.
     * @throws Exception if error occurs.
     * @return ip address list.
     */
    @RequestMapping(value = "/acquireip", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<IpAddress> handleEventWithIPAddress(@RequestParam("network") Long networkId) throws Exception {
        return ipAddressService.acquireIP(networkId);
    }

    /**
     * Get new ip from zone for current vpc.
     *
     * @param vpcId vpc id.
     * @throws Exception if error occurs.
     * @return ip address list.
     */
    @RequestMapping(value = "/vpc/acquireip", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<IpAddress> handleEventWithIPAddressForVpc(@RequestParam("vpc") Long vpcId) throws Exception {
        return ipAddressService.acquireVPCIP(vpcId);
    }

    /**
     * Get new ip from zone for current vpc.
     *
     * @param vpcId vpc id.
     * @throws Exception if error occurs.
     * @return ip address list.
     */
    @RequestMapping(value = "/vpc/nat/list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<IpAddress> IpaddressListForVpcWithNAT(@RequestParam("networkId") Long networkId) throws Exception {
        return ipAddressService.vpcNatList(networkId);
    }

    /**
     * Get new ip from zone for current vpc.
     *
     * @param vpcId vpc id.
     * @throws Exception if error occurs.
     * @return ip address list.
     */
    @RequestMapping(value = "/vpc/lb/list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<IpAddress> IpaddressListForVpcWithLB(@RequestParam("networkId") Long networkId) throws Exception {
        return ipAddressService.vpcLBList(networkId);
    }

    /**
     * Set static NAT for ipaddress that doesn't have source nat.
     *
     * @param ipaddressId ipaddress's id.
     * @param vmId virtual machine's id.
     * @param guestip guest ipaddress.
     * @param type of the network.
     * @throws Exception unhandled exception.
     * @return ip address.
     */
    @RequestMapping(value = "/nat", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public IpAddress enableStatic(@RequestParam("ipaddress") Long ipaddressId, @RequestParam("vm") Long vmId,
            @RequestParam("guestip") String guestip, @RequestParam("type") String type) throws Exception {
        if (type.equalsIgnoreCase("enable")) {
            return ipAddressService.enableStaticNat(ipaddressId, vmId, guestip);
        } else {
            return ipAddressService.disableStaticNat(ipaddressId);
        }
    }

    /**
     * Set static NAT for ipaddress that doesn't have source nat.
     *
     * @param ipaddressId ipaddress's id.
     * @param vmId virtual machine's id.
     * @param guestip guest ipaddress.
     * @param type of the network.
     * @throws Exception unhandled exception.
     * @return ip address.
     */
    @RequestMapping(value = "/vpc/nat", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public IpAddress enableStaticForVpc(@RequestParam("ipaddress") Long ipaddressId, @RequestParam("vm") Long vmId,
            @RequestParam("guestip") String guestip, @RequestParam("type") String type, @RequestParam("networkId") String networkId ) throws Exception {
        if (type.equalsIgnoreCase("enable")) {
            return ipAddressService.enableStaticNatForVpc(ipaddressId, vmId, guestip, networkId);
        } else {
            return ipAddressService.disableStaticNat(ipaddressId);
        }
    }

    /**
     * Enable the remote VPN access.
     *
     * @param uuid of the ip address
     * @return ip address
     * @throws Exception unhandled exception.
     */
    @RequestMapping(value = "/enablevpn", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected IpAddress enableVpnAccess(@RequestParam("uuid") String uuid) throws Exception {
        return ipAddressService.enableRemoteAccessVpn(uuid);
    }

    /**
     * Disable the remote VPN access.
     *
     * @param uuid of the ip address
     * @return ip address
     * @throws Exception unhandled exception.
     */
    @RequestMapping(value = "/disablevpn", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected IpAddress disableVpnAccess(@RequestParam("uuid") String uuid) throws Exception {
        return ipAddressService.disableRemoteAccessVpn(uuid);
    }

    /**
     * Get the VPN pre-shared key.
     *
     * @param id of the ip address.
     * @throws Exception if error occurs.
     * @return project.
     */
    @RequestMapping(value = "getvpnkey", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public IpAddress getVpnKey(@RequestParam("id") Long id) throws Exception {
        return ipAddressService.findByVpnKey(id);
    }

}
