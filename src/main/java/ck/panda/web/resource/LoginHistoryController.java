package ck.panda.web.resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.wordnik.swagger.annotations.Api;
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

    /**
     * logout the application.
     *
     * @param id user id.
     * @param type account type.
     * @param request page request.
     * @param response page response content.
     * @return login history.
     * @throws Exception unhandled errors.
     */
    @RequestMapping(value = "/logoutSession", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public LoginHistory logoutSession(@RequestParam("id") Long id, @RequestParam("type") String type, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LoginHistory loginHistory = loginHistoryService.updateLogoutStatus(id, type);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return loginHistory;
    }

}
