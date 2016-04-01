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
import ck.panda.domain.entity.VpnUser;
import ck.panda.service.VpnUserService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * VPN User Controller.
 *
 */
@RestController
@RequestMapping("/api/vpnUser")
@Api(value = "VpnUser", description = "Operations with VPN user", produces = "application/json")
public class VpnUserController extends CRUDController<VpnUser> implements ApiController {

    /** Service reference to VPN user. */
    @Autowired
    private VpnUserService vpnUserService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new VPN user.", response = VpnUser.class)
    @Override
    public VpnUser create(@RequestBody VpnUser vpnUser) throws Exception {
        vpnUser.setSyncFlag(true);
        return vpnUserService.save(vpnUser);
    }

    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing VPN user.")
    @Override
    public void delete(@PathVariable(PATH_ID) Long id) throws Exception {
        VpnUser vpnUser = vpnUserService.find(id);
        vpnUser.setSyncFlag(true);
        vpnUserService.softDelete(vpnUser);
    }

    @Override
    public List<VpnUser> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, VpnUser.class);
        Page<VpnUser> pageResponse = vpnUserService.findAllByActive(page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * List by VPN user by domain and department.
     *
     * @param domainId VPN user
     * @param departmentId of the VPN user
     * @return VPN user list
     * @throws Exception unhandled exception
     */
    @RequestMapping(value = "listbyvpnuser", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<VpnUser> listByVpnUser(@RequestParam("domainId") Long domainId, @RequestParam("departmentId") Long departmentId) throws Exception {
        return vpnUserService.findByDomainWithDepartment(domainId, departmentId);
    }

    /**
     * List by VPN user by domain and project.
     *
     * @param domainId VPN user
     * @param projectId of the VPN user
     * @return VPN user list
     * @throws Exception unhandled exception
     */
    @RequestMapping(value = "listbyvpnusers", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<VpnUser> listVpnUserByProject(@RequestParam("domainId") Long domainId, @RequestParam("projectId") Long projectId) throws Exception {
        return vpnUserService.findByDomainWithProject(domainId, projectId);
    }

}
