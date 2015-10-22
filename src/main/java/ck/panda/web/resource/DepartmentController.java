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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Domain;
import ck.panda.service.DepartmentService;
import ck.panda.service.DomainService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 * Department controller.
 *
 */
@RestController
@RequestMapping("/api/departments")
@Api(value = "Departments", description = "Operations with departments", produces = "application/json")
public class DepartmentController extends CRUDController<Department> implements ApiController {

    /** Service reference to Department. */
    @Autowired
    private DepartmentService departmentService;

    /** Service reference to Domain. */
    @Autowired
    private DomainService domainService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new Department.", response = Department.class)
    @Override
    public Department create(@RequestBody Department department) throws Exception {
        Domain domain = new Domain();
        domain.setName("assistanz.com");
        domain.setCompanyName("Assistanz Networks");
        domain.setDomainOwner("Assistanz");
        domain = domainService.save(domain);
        department.setDomain(domain);
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

    /**
     * Soft deleting the department from the table.
     *
     * @param department
     * @param id
     * @throws Exception
     */
    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing Department.")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDelete(@RequestBody Department department, @PathVariable(PATH_ID) Long id) throws Exception {
        /** Doing Soft delete from the department table. */
        departmentService.softDelete(department);
    }

    @Override
    public List<Department> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Department.class);
        Page<Department> pageResponse = departmentService.findAllByActive(page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    @Override
    public void testMethod() throws Exception {
        departmentService.findAll();
    }
}
