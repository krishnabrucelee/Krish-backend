package ck.panda.web.resource;

import java.util.ArrayList;
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
import ck.panda.domain.entity.Application;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Department.AccountType;
import ck.panda.service.ConvertEntityService;
import ck.panda.service.DepartmentService;
import ck.panda.util.TokenDetails;
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

    /** Service reference to Convert Entity. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** Autowired TokenDetails. */
    @Autowired
    private TokenDetails tokenDetails;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new Department.", response = Department.class)
    @Override
    public Department create(@RequestBody Department department) throws Exception {
        department.setSyncFlag(true);
        return departmentService.save(department, Long.parseLong(tokenDetails.getTokenDetails("id")));
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing Department.", response = Department.class)
    @Override
    public Department read(@PathVariable(PATH_ID) Long id) throws Exception {
        return departmentService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing Department.", response = Department.class)
    @Override
    public Department update(@RequestBody Department department, @PathVariable(PATH_ID) Long id) throws Exception {
        department.setSyncFlag(true);
        return departmentService.update(department,Long.parseLong(tokenDetails.getTokenDetails("id")));
    }

    /**
     * Delete the department.
     *
     * @param department reference of the department.
     * @param id department id.
     * @throws Exception error occurs.
     */
    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing Department.")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDelete(@RequestBody Department department, @PathVariable(PATH_ID) Long id) throws Exception {
        /** Doing Soft delete from the department table. */
        department.setSyncFlag(true);
        departmentService.softDelete(department);
    }

    @Override
    public List<Department> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Department.class);
        Page<Department> pageResponse = departmentService.findAllByActive(page, Long.parseLong(tokenDetails.getTokenDetails("id")));
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * Find the list of active departments.
     *
     * @return projects project list.
     * @throws Exception error occurs.
     */
    @RequestMapping(value = "list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<Department> getDepartmentList() throws Exception {
        return departmentService.findByDomainAndIsActive(Long.parseLong(tokenDetails.getTokenDetails("id")), true);
    }

    /**
     * Find the list of active departments based on the domain.
     *
     * @param domainId domain id of the department.
     * @return projects project list.
     * @throws Exception error occurs.
     */
    @RequestMapping(value = "search", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<Department> getDepartmentListByDomain(@RequestParam("dept") Long domainId) throws Exception {
        List<AccountType> types = new ArrayList<AccountType>();
        types.add(Department.AccountType.USER);
        types.add(Department.AccountType.DOMAIN_ADMIN);
        types.add(Department.AccountType.ROOT_ADMIN);
        return departmentService.findByDomainAndAccountTypesAndActive(domainId, types, true);
    }

    /**
     * Get the department by Domain.
     *
     * @param id department id.
     * @return domain
     * @throws Exception error occurs.
     */
    @RequestMapping(value = "/domain/{id}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    public List<Department> findByDomain(@PathVariable(PATH_ID) Long id) throws Exception {
        return departmentService.findAllByDomainAndIsActive(id, true);
    }

    /**
     * Get all department list by domain.
     *
     * @param sortBy asc/desc
     * @param domainId domain id of department.
     * @param searchText search text.
     * @param range pagination range.
     * @param limit per page limit.
     * @param request page request.
     * @param response response content.
     * @return department list.
     * @throws Exception unhandled exception.
     */
    @RequestMapping(value = "/listByDomain", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Department> listDepartmentByDomainId(@RequestParam String sortBy, @RequestParam Long domainId, @RequestParam String searchText,
            @RequestHeader(value = RANGE) String range, @RequestParam(required = false) Integer limit,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Application.class);
        Page<Department> pageResponse = departmentService.findAllByDomainIdAndSearchText(domainId, page, searchText, Long.valueOf(tokenDetails.getTokenDetails("id")));
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }
}
