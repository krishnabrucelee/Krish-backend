package ck.panda.web.resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ck.panda.service.LoginHistoryService;

/** Template controller. */
@RestController
@RequestMapping("/api/logout")
public class LogoutController {

    /** Login History Service attribute. */
    @Autowired
    private LoginHistoryService loginHistoryService;

    /** Logout type. */
    public static final String LOG_OUT = "LOG_OUT";

    /**
     * logout from panda.
     *
     * @param request servlet request
     * @param response servlet response.
     * @return redirect url for logout.
     * @throws Exception unhandled errors.
     */
    @RequestMapping(method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String read(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout";
    }

    /**
     * logout the application.
     *
     * @param id application id
     * @param request servlet request
     * @param response servlet response.
     * @throws Exception error occurs
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    public void deleteSession(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) throws Exception {
        loginHistoryService.updateLogoutStatus(id, LOG_OUT);
    }
}
