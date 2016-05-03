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
import ck.panda.domain.entity.Project;
import ck.panda.service.ProjectService;
import ck.panda.util.TokenDetails;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * Project controller.
 */
@RestController
@RequestMapping("/api/projects")
@Api(value = "Projects", description = "Operations with projects", produces = "application/json")
public class ProjectController extends CRUDController<Project> implements ApiController {
    /** Service reference to project. */
    @Autowired
    private ProjectService projectService;

    /** Autowired TokenDetails. */
    @Autowired
    private TokenDetails tokenDetails;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new project.", response = Project.class)
    @Override
    public Project create(@RequestBody Project project) throws Exception {
        project.setSyncFlag(true);
        return projectService.save(project);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing project.", response = Project.class)
    @Override
    public Project update(@RequestBody Project project, @PathVariable(PATH_ID) Long id) throws Exception {
        project.setSyncFlag(true);
        return projectService.update(project);
    }

    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing project.")
    @Override
    public void delete(@PathVariable(PATH_ID) Long id) throws Exception {
        projectService.delete(id);
    }

    /**
     * Delete the project.
     *
     * @param project project object.
     * @param id project id.
     * @throws Exception if error occurs.
     */
    @ApiOperation(value = SW_METHOD_DELETE, notes = "Disable an existing project.")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDelete(@RequestBody Project project, @PathVariable(PATH_ID) Long id) throws Exception {
        project.setSyncFlag(true);
        projectService.softDelete(project);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing project.", response = Project.class)
    @Override
    public Project read(@PathVariable(PATH_ID) Long id) throws Exception {
        return projectService.find(id);
    }

    @Override
    public List<Project> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Project.class);
        Page<Project> pageResponse = projectService.findAllByActive(true, page,
                Long.valueOf(tokenDetails.getTokenDetails("id")));
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * Get the list of projects.
     *
     * @return list of projects.
     * @throws Exception if error occurs.
     */
    @RequestMapping(value = "listall", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<Project> getAllProjects() throws Exception {
        return projectService.getAllProjects(Long.valueOf(tokenDetails.getTokenDetails("id")));
    }

    /**
     * Get the list of projects by department.
     *
     * @param id department id.
     * @return list of projects.
     * @throws Exception error occurs.
     */
    @RequestMapping(value = "/department/{id}", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    public List<Project> findAllByDepartmentAndIsActive(@PathVariable(PATH_ID) Long id) throws Exception {
        return projectService.findAllByDepartmentAndIsActive(id, true);
    }

    /**
     * Remove the user from project.
     *
     * @param project project object.
     * @throws Exception if error occurs.
     * @return project.
     */
    @RequestMapping(value = "/remove/user", method = RequestMethod.PUT, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Project removeUser(@RequestBody Project project) throws Exception {
        return projectService.removeUser(project);
    }

    /**
     * Get the project list by user.
     *
     * @param id user id.
     * @return list of projects.
     * @throws Exception error occurs.
     */
    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    public List<Project> findAllByUserAndIsActive(@PathVariable(PATH_ID) Long id) throws Exception {
        return projectService.findAllByUserAndIsActive(id, true);
    }

    /**
     * Get all project list by domain.
     *
     * @param sortBy asc/desc
     * @param domainId domain id of project.
     * @param range pagination range.
     * @param limit per page limit.
     * @param request page request.
     * @param response response content.
     * @return project list.
     * @throws Exception unhandled exception.
     */
    @RequestMapping(value = "/listByDomain", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Project> listProjectByDomainId(@RequestParam String sortBy,@RequestParam Long domainId,
            @RequestHeader(value = RANGE) String range, @RequestParam(required = false) Integer limit,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Project.class);
        Page<Project> pageResponse = projectService.findAllByDomainId(domainId, page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    @RequestMapping(value = "/listByDomainAndOwner", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Project> listProjectByDomainIdAndOwner(@RequestParam String sortBy,@RequestParam String searchText,@RequestParam Long domainId,
            @RequestHeader(value = RANGE) String range, @RequestParam(required = false) Integer limit,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Project.class);
        Page<Project> pageResponse = projectService.findAllByDomainIdAndSearchText(domainId, page,searchText);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }
}
