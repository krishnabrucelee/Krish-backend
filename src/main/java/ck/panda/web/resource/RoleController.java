package ck.panda.web.resource;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import ck.panda.domain.entity.Role;
import ck.panda.service.DepartmentService;
import ck.panda.service.RoleService;
import ck.panda.util.TokenDetails;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/**
 *
 * Role Controller.
 */
@RestController
@RequestMapping("/api/roles")
@Api(value = "Roles", description = "Operations with roles", produces = "application/json")
public class RoleController extends CRUDController<Role> implements ApiController {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleController.class);

    /** Service reference to Role. */
    @Autowired
    private RoleService roleService;

    /** Autowired departmentService. */
    @Autowired
    private DepartmentService departmentService;

    /** Autowired TokenDetails. */
    @Autowired
    private TokenDetails tokenDetails;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new Role.", response = Role.class)
    @Override
    public Role create(@RequestBody Role role) throws Exception {
        role.setSyncFlag(true);
        return roleService.save(role, Long.parseLong(tokenDetails.getTokenDetails("id")));
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing Role.", response = Role.class)
    @Override
    public Role read(@PathVariable(PATH_ID) Long id) throws Exception {
        return roleService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing Role.", response = Role.class)
    @Override
    public Role update(@RequestBody Role role, @PathVariable(PATH_ID) Long id) throws Exception {
        role.setSyncFlag(true);
        return roleService.update(role);
    }

    /**
     * Soft delete for role.
     *
     * @param role role
     * @param id role id
     * @throws Exception error
     */
    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing role.")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDelete(@RequestBody Role role, @PathVariable(PATH_ID) Long id) throws Exception {
        /** Doing Soft delete from the department table. */
        role.setSyncFlag(true);
        roleService.softDelete(role);
    }

    @Override
    public List<Role> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Role.class);
        Page<Role> pageResponse = roleService.findAllByUserId(page, Long.valueOf(tokenDetails.getTokenDetails("id")));
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * Method to find list of roles by department.
     *
     * @param id - department id
     * @return list of roles
     * @throws Exception - if error occurs
     */
    @RequestMapping(value = "/department/{id}", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Role> getRolesByDepartment(@PathVariable(PATH_ID) Long id) throws Exception {
        return roleService.findAllByDepartmentAndIsActiveExceptName(departmentService.find(id), true, GenericConstants.FULL_PERMISSION);
    }

    /**
     * Get all role list by domain.
     *
     * @param sortBy asc/desc
     * @param domainId domain id of role.
     * @param range pagination range.
     * @param limit per page limit.
     * @param searchText search text.
     * @param request page request.
     * @param response response content.
     * @return role list.
     * @throws Exception unhandled exception.
     */
    @RequestMapping(value = "/listByDomain", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Role> listRoleByDomainId(@RequestParam String sortBy, @RequestParam Long domainId,
            @RequestParam String searchText, @RequestHeader(value = RANGE) String range, @RequestParam(required = false) Integer limit,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Role.class);
        Page<Role> pageResponse = roleService.findAllByDomainId(domainId, searchText, page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

}
