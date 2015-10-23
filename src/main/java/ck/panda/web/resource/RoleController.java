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
import ck.panda.domain.entity.Role;
import ck.panda.service.RoleService;
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

    /** Service reference to Role. */
    @Autowired
    private RoleService roleService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new Role.", response = Role.class)
    @Override
    public Role create(@RequestBody Role role) throws Exception {
        return roleService.save(role);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing Role.", response = Role.class)
    @Override
    public Role read(@PathVariable(PATH_ID) Long id) throws Exception {
        return roleService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing Role.", response = Role.class)
    @Override
    public Role update(@RequestBody Role role, @PathVariable(PATH_ID) Long id) throws Exception {
        return roleService.update(role);
    }

    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing Role.")
    @Override
    public void delete(@PathVariable(PATH_ID) Long id) throws Exception {
        roleService.delete(id);
    }

    @Override
    public List<Role> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, Role.class);
        Page<Role> pageResponse = roleService.findAll(page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * Empty method No implementation.
     */
    @Override
    public void testMethod() throws Exception {
        roleService.findAll();
    }

}
