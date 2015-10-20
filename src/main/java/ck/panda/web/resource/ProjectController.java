package ck.panda.web.resource;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Project;
import ck.panda.service.ProjectService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * Project controller.
 *
 */
@RestController
@RequestMapping("/api/departments")
@Api(value = "Projects", description = "Operations with projects", produces = "application/json")
public class ProjectController extends CRUDController<Project> implements ApiController {

    /** Service reference to project. */
    @Autowired
    private ProjectService projectService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new project.", response = Project.class)
    @Override
    public Project create(@RequestBody Project project) throws Exception {
        return projectService.save(project);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing project.", response = Project.class)
    @Override
    public Project update(@RequestBody Project project, @PathVariable(PATH_ID) Long id) throws Exception {
        return projectService.update(project);
    }

    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing project.")
    @Override
    public void delete(@PathVariable(PATH_ID) Long id) throws Exception {
        projectService.delete(id);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing project.", response = Project.class)
    @Override
    public Project read(@PathVariable(PATH_ID) Long id) throws Exception {
        return projectService.find(id);
    }

   @Override
    public List<Project> list(@RequestParam String sortBy,@RequestHeader(value = RANGE) String range, @RequestParam(required = false)  Integer limit, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
       PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Department.class);
       Page<Project> pageResponse = projectService.findAll(page);
       response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
       return pageResponse.getContent();
    }

    /**
     * No Need implement this method.
     */
    @Override
    public void testMethod() throws Exception {


    }
}
