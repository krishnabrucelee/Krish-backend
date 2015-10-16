package ck.panda.web.resource;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
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
import ck.panda.service.DepartmentService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

@RestController
@RequestMapping("/api/departments")
@Api(value = "Departments", description = "Operations with departments", produces = "application/json")
@PreAuthorize("hasAuthority('ROLE_DOMAIN_USER')")
public class DepartmentController extends CRUDController<Department> implements ApiController {

    /** Service reference to Department. */
    @Autowired
    private DepartmentService departmentService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new Department.", response = Department.class)
    @Override
    public Department create(@RequestBody Department department) throws Exception {
        return departmentService.save(department);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing Department.", response = Department.class)
    @Override
    public Department read(@PathVariable(PATH_ID) Long id) throws Exception {
        return departmentService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing Department.", response = Department.class)
    @Override
    public Department update(@RequestBody Department department, @PathVariable(PATH_ID) Long id) throws Exception {
        return departmentService.update(department);
    }

    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing Department.")
    @Override
    public void delete(@PathVariable(PATH_ID) Long id) throws Exception {
        departmentService.delete(id);
    }
    
    @Override
    public List<Department> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
    		@RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Department.class);
        Page<Department> pageResponse = departmentService.findAll(page);
        System.out.println(pageResponse);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

}