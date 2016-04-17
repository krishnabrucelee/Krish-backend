package ck.panda.web.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import ck.panda.domain.entity.LoginHistory;
import ck.panda.service.LoginHistoryService;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

/** Login History controller. */
@RestController
@RequestMapping("/api/loginHistory")
@Api(value = "LoginHistory", description = "Operations with login History", produces = "application/json")
public class LoginHistoryController extends CRUDController<LoginHistory> implements ApiController {

    /** Service reference to login History. */
    @Autowired
    private LoginHistoryService loginHistoryService;

    @ApiOperation(value = SW_METHOD_READ, notes = "Read an existing login History.", response = LoginHistory.class)
    @Override
    public LoginHistory read(@PathVariable(PATH_ID) Long id) throws Exception {
        return loginHistoryService.updateLogoutStatus(id);
    }
}
