package ck.panda.web.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import ck.panda.domain.entity.VpcAcl;
import ck.panda.domain.entity.VpcNetworkAcl;
import ck.panda.service.VpcAclService;
import ck.panda.service.VpcNetworkAclService;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * VPC NETWORK ACL controller.
 *
 */
@RestController
@RequestMapping("/api/vpcnetworkacl")
@Api(value = "VpNetworkcAcl", description = "Operations with VPC NETWORK ACL", produces = "application/json")
public class VpcNetworkAclController extends CRUDController<VpcNetworkAcl> implements ApiController {

    /** VPC ACL service reference. */
    @Autowired
    private VpcNetworkAclService vpcNetworkAclService;

    /**
     * Add network acl for vpc.
     *
     * @param vpcAcl vpc acl
     * @param aclId vpc id
     * @return network acl
     * @throws Exception exception
     */
    @RequestMapping(value = "addNetworkAcl/{id}", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected VpcNetworkAcl addVpcAcl(@RequestBody VpcNetworkAcl vpcNetworkAcl, @PathVariable(PATH_ID) Long aclId) throws Exception {
        return vpcNetworkAclService.addVpcAcl(vpcNetworkAcl, aclId);
    }

    /**
     * Get the list of VPC Network ACL.
     *
     * @return VPC ACL list.
     * @throws Exception unhandled errors.
     */
    @RequestMapping(value = "/networkAclList/{id}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<VpcNetworkAcl> networkAclList(@PathVariable(PATH_ID) Long aclId) throws Exception {
        return vpcNetworkAclService.findByAclIdAndIsActive(aclId);
    }

    /**
     * Soft delete for Network acl.
     *
     * @param Network acl Network acl
     * @param id Network acl id
     * @throws Exception error
     */
    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing Network acl.")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public VpcNetworkAcl softDelete(@RequestBody VpcNetworkAcl vpcNetworkAcl, @PathVariable(PATH_ID) Long id) throws Exception {
        return vpcNetworkAclService.softDelete(vpcNetworkAcl);
    }
}
