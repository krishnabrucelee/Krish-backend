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
import ck.panda.domain.entity.VPC;
import ck.panda.service.NetworkService;
import ck.panda.service.VPCService;
import ck.panda.util.TokenDetails;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * Network controller.
 *
 */
@RestController
@RequestMapping("/api/vpc")
@Api(value = "VPC", description = "Operations with Vpcs", produces = "application/json")
public class VPCController extends CRUDController<VPC> implements ApiController {

    /** Service reference to vpc. */
    @Autowired
    private VPCService vpcService;

    /** Token Detail Utilities. */
    @Autowired
    private TokenDetails tokenDetails;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new VPC.", response = VPC.class)
    @Override
    public VPC create(@RequestBody VPC vpc) throws Exception {
    	vpc.setSyncFlag(true);
        return vpcService.save(vpc, Long.parseLong(tokenDetails.getTokenDetails("id")));
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing VPC.", response = VPC.class)
    @Override
    public VPC read(@PathVariable(PATH_ID) Long id) throws Exception {
        return vpcService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing VPC.", response = VPC.class)
    @Override
    public VPC update(@RequestBody VPC vpc, @PathVariable(PATH_ID) Long id) throws Exception {
        vpc.setSyncFlag(true);
        return vpcService.update(vpc);
    }

    /**
     * Delete the Vpc.
     *
     * @param network reference of the Network.
     * @param id vpc id.
     * @throws Exception error occurs.
     */
    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing VPC.")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDelete(@RequestBody VPC vpc, @PathVariable(PATH_ID) Long id) throws Exception {
        /** Doing Soft delete from the vpc table. */
        vpc = vpcService.find(id);
        vpc.setSyncFlag(true);
        vpcService.softDelete(vpc);
    }

    @Override
    public List<VPC> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, VPC.class);
        Page<VPC> pageResponse = vpcService.findAllByActive(page, Long.parseLong(tokenDetails.getTokenDetails("id")));
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * list all vpc for instance.
     *
     * @return projects
     * @param deptartment department
     * @throws Exception Exception
     */
    @RequestMapping(value = "list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<VPC> findByDepartment(@RequestParam("dept") Long deptartment) throws Exception {
        return vpcService.findByDepartmentAndVpcIsActive(deptartment, true);
    }

    /**
     * list all project related vpc for instance.
     *
     * @return networks
     * @param projectId project id
     * @throws Exception Exception
     */
    @RequestMapping(value = "listall", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<VPC> findByProject(@RequestParam("projectId") Long projectId) throws Exception {
        return vpcService.findByProjectAndVpcIsActive(projectId, true);
    }

    /**
     * List all vpc for instance.
     *
     * @return department
     * @param deptartment associated with vpc
     * @throws Exception Exception
     */
    @RequestMapping(value = "/list/{id}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<VPC> findByDepartmentAndVPC(@PathVariable(PATH_ID) Long deptartment) throws Exception {
        return vpcService.findByDepartmentAndVpcIsActive(deptartment, true);
    }

    /**
     * List all project related vpc for instance.
     *
     * @return vpcs
     * @param projectId project id
     * @throws Exception Exception
     */
    @RequestMapping(value = "/listall/{id}", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<VPC> findByProjectAndVPC(@PathVariable(PATH_ID) Long projectId) throws Exception {
        return vpcService.findByProjectAndVpcIsActive(projectId, true);
    }

    /**
     * Restart vpc for reapplying all ip addresses and rules.
     *
     * @param vpc to be restarted.
     * @param id of the vpc.
     * @return vpc.
     * @throws Exception if error occurs.
     */
    @RequestMapping(value = "restart/{id}", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected VPC restartNetwork(@RequestBody VPC vpc, @PathVariable(PATH_ID) Long id) throws Exception {
        vpc.setSyncFlag(true);
        return vpcService.restartVPC(vpc);
    }

    /**
     * Get all vpc list by domain.
     *
     * @param sortBy asc/desc.
     * @param domainId domain id of network.
     * @param range pagination range.
     * @param limit per page limit.
     * @param request page request.
     * @param response response content.
     * @return vpc list.
     * @throws Exception unhandled exception.
     */
    @RequestMapping(value = "/listByDomain", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<VPC> listNetworkByDomainId(@RequestParam String sortBy, @RequestParam Long domainId,
            @RequestHeader(value = RANGE) String range, @RequestParam(required = false) Integer limit,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, VPC.class);
        Page<VPC> pageResponse = vpcService.findAllByDomainId(domainId, page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }
}
