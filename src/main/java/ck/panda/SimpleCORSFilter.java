package ck.panda;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import ck.panda.service.ThemeSettingService;

/**
 * SimpleCORSFilter to allow cross domain call.
 *
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SimpleCORSFilter implements Filter {

    @Autowired
    private ThemeSettingService themeSettingService;
    /**
     * Overriden method.
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        System.out.println("IN");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers",
                "Origin, Range, x-requested-with, x-auth-token, x-auth-username, x-auth-password, x-auth-remember, x-auth-login-token, x-auth-user-id, x-auth-login-time, Content-Type, Accept, x-force-login");
        response.setHeader("Access-Control-Expose-Headers", "Rage, Content-Range");
        if (request.getRequestURI().contains("socket")) {

        }
        System.out.println(request.getHeader("x-requested-with"));
        if (request.getRequestURI().contains("panda")) {
        }
        if (request.getRequestURI().contains("resources")) {
        }
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(req, res);
        }

    }

    /**
     * Initialization life cycle method.
     */
    @Override
    public void init(FilterConfig filterConfig) {

    }

    /**
     * Destroy life cycle method.
     */
    @Override
    public void destroy() {

    }


}
