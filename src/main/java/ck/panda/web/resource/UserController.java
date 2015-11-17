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
import ck.panda.domain.entity.User;
import ck.panda.service.UserService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/** User CRUD operations are handle here. */
@RestController
@RequestMapping("/api/users")
@Api(value = "Users", description = "Operations with user", produces = "application/json")
public class UserController extends CRUDController<User> implements ApiController {

    /** Inject userService business logic.*/
    @Autowired
    private UserService userService;

    @ApiOperation(value = SW_METHOD_CREATE, notes = "Create a new User.", response = User.class)
    @Override
    public User create(@RequestBody User user) throws Exception {
    	user.setSyncFlag(true);
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
    	user.setSyncFlag(true);
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

    /**
     * list all user for instance.
     * @return user
     * @throws Exception
     */
    @RequestMapping(value = "list",method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
   	@ResponseStatus(HttpStatus.OK)
   	@ResponseBody
   	protected List<User> getSearch() throws Exception {
   		return userService.findAll();
   	}

    @RequestMapping(value = "search",method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
   	@ResponseStatus(HttpStatus.OK)
   	@ResponseBody
   	protected List<User> getSearch(@RequestParam("q") String query) throws Exception {
   		return userService.findByName(query);
   	}
}
