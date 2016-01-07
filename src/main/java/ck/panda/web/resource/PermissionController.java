package ck.panda.web.resource;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.wordnik.swagger.annotations.Api;
import ck.panda.domain.entity.Permission;
import ck.panda.service.PermissionService;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/** Handle all request and response of permission. */
@RestController
@RequestMapping("/api/permissions")
@Api(value = "Permissions", description = "Operations with permissions", produces = "application/json")
public class PermissionController extends CRUDController<Permission> implements ApiController {

    /** Autowired PermissionService. */
    @Autowired
    private PermissionService permissionService;

    /**
     * List the permission.
     * 
     * @return list of permission.
     * @throws Exception error occurs.
     */
    @RequestMapping(value = "list", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<Permission> getPermissionList() throws Exception {
        return permissionService.getPermissionList();
    }
}
