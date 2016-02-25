/**
 *
 */
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
import ck.panda.domain.entity.ResourceLimitDomain;
import ck.panda.domain.entity.Volume;
import ck.panda.service.ResourceLimitDomainService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * Resource Limit Controller.
 */
@RestController
@RequestMapping("/api/resourceDomains")
@Api(value = "ResourceLimitDomain", description = "Operations with resource limits", produces = "application/json")
public class ResourceLimitDomainController extends CRUDController<ResourceLimitDomain> implements ApiController {

    /** Service reference to resource. */
    @Autowired
    private ResourceLimitDomainService resourceLimitDomainService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new resource.", response = ResourceLimitDomain.class)
    @Override
    public ResourceLimitDomain create(@RequestBody ResourceLimitDomain resource) throws Exception {
        resource.setIsSyncFlag(true);
        return resourceLimitDomainService.save(resource);
    }

    /**
     * Creating resource limit for domain.
     *
     * @param resourceLimits resource limit
     * @return resource limit
     * @throws Exception error
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public List<ResourceLimitDomain> createResourceLimits(@RequestBody List<ResourceLimitDomain> resourceLimits)
            throws Exception {
        resourceLimits.get(0).setIsSyncFlag(true);
        return resourceLimitDomainService.createResourceLimits(resourceLimits);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing resource.", response = ResourceLimitDomain.class)
    @Override
    public ResourceLimitDomain read(@PathVariable(PATH_ID) Long id) throws Exception {
        return resourceLimitDomainService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing resource.", response = ResourceLimitDomain.class)
    @Override
    public ResourceLimitDomain update(@RequestBody ResourceLimitDomain resource, @PathVariable(PATH_ID) Long id)
            throws Exception {
        resource.setIsSyncFlag(true);
        return resourceLimitDomainService.update(resource);
    }

    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing resource.")
    @Override
    public void delete(@PathVariable(PATH_ID) Long id) throws Exception {
        resourceLimitDomainService.delete(id);
    }

    @Override
    public List<ResourceLimitDomain> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Volume.class);
        Page<ResourceLimitDomain> pageResponse = resourceLimitDomainService.findAll(page);
        System.out.println(pageResponse);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * List all resources for instance.
     *
     * @return resource service
     * @throws Exception error
     */
    @RequestMapping(value = "list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<ResourceLimitDomain> getSearch() throws Exception {
        return resourceLimitDomainService.findAll();
    }

    /**
     * List all resource limits by domain.
     *
     * @param id domain id
     * @return resource service
     * @throws Exception error
     */
    @RequestMapping(value = "domain/{id}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<ResourceLimitDomain> getResourceLimitByDomain(@PathVariable(PATH_ID) Long id) throws Exception {
        resourceLimitDomainService.asyncResourceDomain(id);
        return resourceLimitDomainService.findAllByDomainIdAndIsActive(id, true);
    }

    /**
     * List current domain for resource count.
     *
     * @return domain
     * @throws Exception error
     */
    @RequestMapping(value = "listresourcedomains", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<ResourceLimitDomain> getDomainSearch() throws Exception {
        return resourceLimitDomainService.findCurrentLoginDomain();
    }
}
