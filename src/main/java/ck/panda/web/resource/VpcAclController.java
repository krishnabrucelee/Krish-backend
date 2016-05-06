package ck.panda.web.resource;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.VpcAcl;
import ck.panda.service.VpcAclService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * VPC ACL controller.
 *
 */
@RestController
@RequestMapping("/api/vpcacl")
@Api(value = "VpcAcl", description = "Operations with VPC ACL", produces = "application/json")
public class VpcAclController extends CRUDController<VpcAcl> implements ApiController {

    /** VPC ACL service reference. */
    @Autowired
    private VpcAclService vpcAclService;

    @Override
    public List<VpcAcl> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, VpcAcl.class);
        Page<VpcAcl> pageResponse = vpcAclService.findAll(page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * Get the list of VPC ACL.
     *
     * @return VPC ACL list.
     * @throws Exception unhandled errors.
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<VpcAcl> vpcAclList() throws Exception {
        return vpcAclService.findAll();
    }

}
