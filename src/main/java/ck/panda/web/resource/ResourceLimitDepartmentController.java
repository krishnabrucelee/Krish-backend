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
import ck.panda.domain.entity.ResourceLimitDepartment;
import ck.panda.domain.entity.Volume;
import ck.panda.service.ConvertEntityService;
import ck.panda.service.ResourceLimitDepartmentService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * Resource Limit Controller.
 */
@RestController
@RequestMapping("/api/resourceDepartments")
@Api(value = "ResourceLimitDepartment", description = "Operations with resource limits", produces = "application/json")
public class ResourceLimitDepartmentController extends CRUDController<ResourceLimitDepartment>
        implements ApiController {

    /** Service reference to resource. */
    @Autowired
    private ResourceLimitDepartmentService resourceLimitService;

    /** Convert Entity Service reference to resource. */
    @Autowired
    private ConvertEntityService convertEntityService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new resource.", response = ResourceLimitDepartment.class)
    @Override
    public ResourceLimitDepartment create(@RequestBody ResourceLimitDepartment resource) throws Exception {
        resource.setIsSyncFlag(true);
        return resourceLimitService.save(resource);
    }

    /**
     * Creating resource limit for department.
     *
     * @param resourceLimits resource limit
     * @return resource limit
     * @throws Exception error
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public List<ResourceLimitDepartment> createResourceLimits(@RequestBody List<ResourceLimitDepartment> resourceLimits)
            throws Exception {
        return resourceLimitService.createResourceLimits(resourceLimits);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing resource.", response = ResourceLimitDepartment.class)
    @Override
    public ResourceLimitDepartment read(@PathVariable(PATH_ID) Long id) throws Exception {
        return resourceLimitService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing resource.", response = ResourceLimitDepartment.class)
    @Override
    public ResourceLimitDepartment update(@RequestBody ResourceLimitDepartment resource, @PathVariable(PATH_ID) Long id)
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
    public List<ResourceLimitDepartment> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Volume.class);
        Page<ResourceLimitDepartment> pageResponse = resourceLimitService.findAll(page);
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
    protected List<ResourceLimitDepartment> getSearch() throws Exception {
        return resourceLimitService.findAll();
    }

    /**
     * List all resource limits by department.
     *
     * @param departmentId department id
     * @return resource service.
     * @throws Exception error occurs.
     */
    @RequestMapping(value = "department/{id}", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<ResourceLimitDepartment> getResourceLimitByDepartment(@PathVariable(PATH_ID) Long departmentId)
            throws Exception {
        return resourceLimitService.findAllByDepartmentIdAndIsActive(departmentId, true);
    }

    /**
     * Get resource limits of department.
     *
     * @param domainId domain id
     * @return max values of resources
     * @throws Exception error occurs.
     */
    @RequestMapping(value = "/domainId/{id}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    public HashMap<String, String> findByDomainResource(@PathVariable(PATH_ID) Long domainId) throws Exception {
        return resourceLimitService.getResourceLimitsOfDepartment(domainId);
    }

    /**
     * Get resource limits of department.
     *
     * @param projectId project id
     * @return max values of resources
     * @throws Exception error occurs.
     */
    @RequestMapping(value = "/projectId/{id}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    public HashMap<String, String> findByDepartmentResource(@PathVariable(PATH_ID) Long projectId) throws Exception {
        return resourceLimitService.getResourceLimitsOfProject(projectId);
    }

    /**
     * Get resource limits of Project.
     *
     * @param projectId department id
     * @return max values of resources
     * @throws Exception error occurs.
     */
    @RequestMapping(value = "/quotaprojectId/{id}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    public HashMap<String, String> findByProjectQuotaDepartmentResource(@PathVariable(PATH_ID) Long projectId) throws Exception {
        return resourceLimitService.getResourceLimitsOfProject(convertEntityService.getProjectById(projectId).getDepartmentId());
    }

    /**
     * Get resource limits of Department.
     *
     * @param departmentId department id
     * @return max values of resources
     * @throws Exception error occurs.
     */
    @RequestMapping(value = "/quotadepartmentId/{id}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    public HashMap<String, String> findQuotaDepartmentResource(@PathVariable(PATH_ID) Long departmentId) throws Exception {
        return resourceLimitService.getResourceLimitsOfDepartment(convertEntityService.getDepartmentById(departmentId).getDomainId());
    }

    /**
     * List all department min resource limits by department id.
     *
     * @param id department id
     * @return resource service
     * @throws Exception error
     */
    @RequestMapping(value = "departmentmin/{id}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected HashMap<String, Long> getSumOfDepartmentMin(@PathVariable(PATH_ID) Long id) throws Exception {
        return resourceLimitService.getSumOfDepartmentMin(id);
    }

    /**
     * List all department max resource limits by department id.
     *
     * @param id department id
     * @return resource service
     * @throws Exception error
     */
    @RequestMapping(value = "departmentmax/{id}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected HashMap<String, Long> getSumOfDepartmentMax(@PathVariable(PATH_ID) Long id) throws Exception {
        return resourceLimitService.getSumOfDepartmentMax(id);
    }
}
