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
import ck.panda.domain.entity.Organization;
import ck.panda.service.OrganizationService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * Organization controller.
 *
 */
@RestController
@RequestMapping("/api/organization")
@Api(value = "Organization", description = "Operations with Organization", produces = "application/json")
public class OrganizationController extends CRUDController<Organization> implements ApiController {

    /** Service reference to Organization. */
    @Autowired
    private OrganizationService organizationService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new Organization.", response = Organization.class)
    @Override
    public Organization create(@RequestBody Organization cost) throws Exception {
        return organizationService.save(cost);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing Organization.", response = Organization.class)
    @Override
    public Organization read(@PathVariable(PATH_ID) Long id) throws Exception {
        return organizationService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing Organization.", response = Organization.class)
    @Override
    public Organization update(@RequestBody Organization cost, @PathVariable(PATH_ID) Long id) throws Exception {
        return organizationService.update(cost);
    }

    @Override
    public List<Organization> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Organization.class);
        Page<Organization> pageResponse = organizationService.findAll(page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * List all organization.
     *
     * @return configuration values.
     * @throws Exception unhandled errors.
     */
    @RequestMapping(value = "orglist", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<Organization> getSearch() throws Exception {
        return organizationService.findAll();
    }
}
