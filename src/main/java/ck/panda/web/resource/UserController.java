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

import ck.panda.constants.CloudStackConstants;
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.User.Status;
import ck.panda.service.UserService;
import ck.panda.util.TokenDetails;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/** User CRUD operations are handle here. */
@RestController
@RequestMapping("/api/users")
@Api(value = "Users", description = "Operations with user", produces = "application/json")
public class UserController extends CRUDController<User> implements ApiController {

    /** Inject userService business logic. */
    @Autowired
    private UserService userService;

    /** Autowired TokenDetails. */
    @Autowired
    private TokenDetails tokenDetails;

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

    /**
     * Soft delete for user.
     *
     * @param user the user object.
     * @throws Exception unhandled errors.
     */
    @ApiOperation(value = SW_METHOD_DELETE, notes = "Delete an existing user.")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDelete(@RequestBody User user) throws Exception {
        user.setSyncFlag(true);
        userService.softDelete(user);
    }

    @Override
    public List<User> list(@RequestParam String sortBy, @RequestHeader(value = RANGE) String range,
            @RequestParam(required = false) Integer limit, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, User.class);
        Page<User> pageResponse = userService.findAllUserByDomain(page, Long.valueOf(tokenDetails.getTokenDetails(CloudStackConstants.CS_ID)),Status.DELETED);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();
    }

    /**
     * To list all the users.
     *
     * @param sortBy user
     * @param range range
     * @param limit pagelimit
     * @param request request
     * @param response response
     * @return users
     * @throws Exception exceptions
     */
    @RequestMapping(value = "/listall", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<User> listAllUser(@RequestParam String sortBy,
            @RequestHeader(value = RANGE) String range, @RequestParam(required = false) Integer limit,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        PagingAndSorting page = new PagingAndSorting(range, sortBy, limit, VmInstance.class);
        Page<User> pageResponse = userService.findAll(page);
        response.setHeader(GenericConstants.CONTENT_RANGE_HEADER, page.getPageHeaderValue(pageResponse));
        return pageResponse.getContent();

    }

    /**
     * list all user for instance.
     *
     * @return user
     * @throws Exception unhandled errors.
     */
    @RequestMapping(value = "list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<User> getSearch() throws Exception {
        return userService.findAll();
    }

    /**
     * find all users by department.
     *
     * @param deptId the department id.
     * @return users.
     * @throws Exception unhandled errors.
     */
    @RequestMapping(value = "search", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<User> findAllByDepartment(@RequestParam("dept") Long deptId) throws Exception {
        return userService.findByDepartment(deptId);
    }

    /**
     * list all user for instance along with department.
     *
     * @param deptId department id.
     * @return user
     * @throws Exception unhandled errors.
     */
    @RequestMapping(value = "departmentusers", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<User> findAllByDepartmentwithLoggedUser(@RequestParam("dept") Long deptId) throws Exception {
        return userService.findByDepartmentWithLoggedUser(deptId, Long.valueOf(tokenDetails.getTokenDetails("id")));
    }

    /**
     * list all user for instance.
     *
     * @return user
     * @throws Exception unhandled errors.
     */
    @RequestMapping(value = "listbydomain", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<User> findAllUserByDomain() throws Exception {
        return userService.findAllUserByDomain(Long.valueOf(tokenDetails.getTokenDetails("id")));
    }

    /**
     * list all root admin.
     *
     * @return user
     * @throws Exception unhandled errors.
     */
    @RequestMapping(value = "listbyrootadmin", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<User> findAllRootAdminUser() throws Exception {
        return userService.findAllRootAdminUser();
    }

    /**
     * Method to find list of users by department.
     *
     * @param id - department id
     * @return list of users
     * @throws Exception - if error occurs
     */
    @RequestMapping(value = "/department/{id}", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<User> getUsersByDepartment(@PathVariable(PATH_ID) Long id) throws Exception {
        return userService.findByDepartment(id);
    }

    /**
     * Assign role for users.
     *
     * @param users user list.
     * @return users.
     * @throws Exception error
     */
    @RequestMapping(value = "/assignRole", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public List<User> assignUserRoles(@RequestBody List<User> users) throws Exception {
        return userService.assignUserRoles(users);
    }

    /**
     * Method to find list of users by project.
     *
     * @param projectId - project id
     * @return list of users
     * @throws Exception - if error occurs
     */
    @RequestMapping(value = "/project/{id}", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<User> getUsersByProject(@PathVariable(PATH_ID) Long projectId) throws Exception {
        return userService.findAllByProject(projectId);
    }

    /**
     * Method to Enable the User.
     *
     * @param userId id of the user
     * @return list of users
     * @throws Exception - if error occurs
     */
    @RequestMapping(value = "/enable/{id}", method = RequestMethod.PUT, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public User enableUser(@PathVariable(PATH_ID) Long userId) throws Exception {
        return userService.enableUser(userId);
    }

    /**
     * Method to Disable the User.
     *
     * @param userId id of the user
     * @return disabled users
     * @throws Exception exception
     */
    @RequestMapping(value = "/disable/{id}", method = RequestMethod.PUT, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public User disableUser(@PathVariable(PATH_ID) Long userId) throws Exception {
        return userService.disableUser(userId);
    }

    /**
     * Update password of user.
     *
     * @param users user.
     * @return users.
     * @throws Exception error
     */
    @RequestMapping(value = "updatePassword", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public User updatePassword(@RequestBody User user) throws Exception {
        user.setSyncFlag(true);
        return userService.updatePassword(user);
    }

    /**
     * Method to get list of required parameter of user.
     *
     * @param id user id
     * @return users
     * @throws Exception if error occurs
     */
    @RequestMapping(value = "/uservalidlist/{id}", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    public User getUserValidList(@PathVariable(PATH_ID) Long id) throws Exception {
        return userService.findByUserValidList(id);
    }

  }
