package ck.panda.web.resource;

import java.util.HashMap;
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
import ck.panda.domain.entity.ResourceLimitProject;
import ck.panda.domain.entity.Volume;
import ck.panda.service.ResourceLimitProjectService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * Resource Limit Controller.
 */
@RestController
@RequestMapping("/api/resourceProjects")
@Api(value = "ResourceLimit", description = "Operations with resource limits", produces = "application/json")
public class ResourceLimitProjectController extends CRUDController<ResourceLimitProject> implements ApiController {

    /** Service reference to resource. */
    @Autowired
    private ResourceLimitProjectService resourceLimitService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new resource.", response = ResourceLimitDomain.class)
    @Override
    public ResourceLimitProject create(@RequestBody ResourceLimitProject resource) throws Exception {
        resource.setIsSyncFlag(true);
        return resourceLimitService.save(resource);
    }

    /**
     * Creating resource limit for project.
     *
     * @param resourceLimits resource limit
     * @return resource limit
     * @throws Exception error
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public List<ResourceLimitProject> createResourceLimits(@RequestBody List<ResourceLimitProject> resourceLimits)
            throws Exception {
        return resourceLimitService.createResourceLimits(resourceLimits);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing resource.", response = ResourceLimitDomain.class)
    @Override
    public ResourceLimitProject read(@PathVariable(PATH_ID) Long id) throws Exception {
        return resourceLimitService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing resource.", response = ResourceLimitDomain.class)
    @Override
    public ResourceLimitProject update(@RequestBody ResourceLimitProject resource, @PathVariable(PATH_ID) Long id)
            throws Exception {
        resource.setIsSyncFlag(true);
        return resourceLimitService.update(resource);
    }

    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing resource.")
    @Override
    public void delete(@PathVariable(PATH_ID) Long id) throws Exception {
        resourceLimitService.delete(id);
    }

    @Override
    public List<ResourceLimitProject> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Volume.class);
        Page<ResourceLimitProject> pageResponse = resourceLimitService.findAll(page);
        System.out.println(pageResponse);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * list all resources for instance.
     *
     * @return resource service
     * @throws Exception error
     */
    @RequestMapping(value = "list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<ResourceLimitProject> getSearch() throws Exception {
        return resourceLimitService.findAll();
    }

    /**
     * List all resource limits by project.
     *
     * @param projectId project id
     * @return resource service.
     * @throws Exception error occurs.
     */
    @RequestMapping(value = "project/{id}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<ResourceLimitProject> getResourceLimitByProject(@PathVariable(PATH_ID) Long projectId)
            throws Exception {
        return resourceLimitService.findAllByProjectAndIsActive(projectId, true);
    }

    /**
     * Get resource limits of project.
     *
     * @param domainId domain id
     * @return max values of resources
     * @throws Exception error occurs.
     */
    @RequestMapping(value = "/domainId/{id}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    public HashMap<String, String> findByDomainResource(@PathVariable(PATH_ID) Long domainId) throws Exception {
        return resourceLimitService.getResourceLimitsOfProject(domainId);
    }

    /**
     * Get resource limits of project.
     *
     * @param domainId domain id
     * @return max values of resources
     * @throws Exception error occurs.
     */
    @RequestMapping(value = "/departmentId/{id}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    public HashMap<String, String> findByDepartmentResource(@PathVariable(PATH_ID) Long departmentId) throws Exception {
        return resourceLimitService.getResourceLimitsOfDepartment(departmentId);
    }


}
