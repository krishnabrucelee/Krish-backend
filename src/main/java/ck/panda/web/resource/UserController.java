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
import ck.panda.domain.entity.User;
import ck.panda.service.UserService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;
/**
 * User CRUD operations are handle here.
 */
@RestController
@RequestMapping("/api/users")
@Api(value = "Users", description = "Operations with user", produces = "application/json")
@PreAuthorize("hasAuthority('ROLE_DOMAIN_USER')")
public class UserController extends CRUDController<User> implements ApiController {

    /**
     * Inject userService business logic.
     */
    @Autowired
    private UserService userService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new User.", response = User.class)
    @Override
    public User create(@RequestBody User user) throws Exception {
        return userService.save(user);
    }

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing User.", response = User.class)
    @Override
    public User read(@PathVariable(PATH_ID) Long id) throws Exception {
        return userService.find(id);
    }

    @ApiOperation(value = SW_METHOD_UPDATE, notes = "Update an existing User.", response = User.class)
    @Override
    public User update(@RequestBody User user, @PathVariable(PATH_ID) Long id) throws Exception {
        return userService.update(user);
    }

    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing User.")
    @Override
    public void delete(@PathVariable(PATH_ID) Long id) throws Exception {
        userService.delete(id);
    }
    @Override
    public List<User> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, User.class);
        Page<User> pageResponse = userService.findAll(page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

   @Override
  public void testMethod() throws Exception {
   // TODO Auto-generated method stub
  }
}
