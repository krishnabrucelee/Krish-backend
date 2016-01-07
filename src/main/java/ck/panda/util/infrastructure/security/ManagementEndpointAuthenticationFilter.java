package ck.panda.util.infrastructure.security;

import com.google.common.base.Optional;

import ck.panda.util.web.ApiController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Management endpoint authentivation filter.
 *
 */
public class ManagementEndpointAuthenticationFilter extends GenericFilterBean {

    /** Logger constant. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ManagementEndpointAuthenticationFilter.class);

    /** Authentication manager. */
    private AuthenticationManager authenticationManager;

    /** Management endpoints. */
    private Set<String> managementEndpoints;

    /**
     * Parameterized constructor.
     * 
     * @param authenticationManager to set
     */
    public ManagementEndpointAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        prepareManagementEndpointsSet();
    }

    /**
     * Prepare management endpoints.
     */
    private void prepareManagementEndpointsSet() {
        managementEndpoints = new HashSet<String>();
        managementEndpoints.add(ApiController.AUTOCONFIG_ENDPOINT);
        managementEndpoints.add(ApiController.BEANS_ENDPOINT);
        managementEndpoints.add(ApiController.CONFIGPROPS_ENDPOINT);
        managementEndpoints.add(ApiController.ENV_ENDPOINT);
        managementEndpoints.add(ApiController.MAPPINGS_ENDPOINT);
        managementEndpoints.add(ApiController.METRICS_ENDPOINT);
        managementEndpoints.add(ApiController.SHUTDOWN_ENDPOINT);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = asHttp(request);
        HttpServletResponse httpResponse = asHttp(response);

        Optional<String> username = Optional.fromNullable(httpRequest.getHeader("X-Auth-Username"));
        Optional<String> password = Optional.fromNullable(httpRequest.getHeader("X-Auth-Password"));

        String resourcePath = new UrlPathHelper().getPathWithinApplication(httpRequest);

        try {
            if (postToManagementEndpoints(resourcePath)) {
                LOGGER.debug("Trying to authenticate user {} for management endpoint by X-Auth-Username method",
                        username);
                processManagementEndpointUsernamePasswordAuthentication(username, password);
            }

            LOGGER.debug("ManagementEndpointAuthenticationFilter is passing request down the filter chain");
            chain.doFilter(request, response);
        } catch (AuthenticationException authenticationException) {
            SecurityContextHolder.clearContext();
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, authenticationException.getMessage());
        }
    }

    /**
     * Convert ServletRequest to HttpServletRequest.
     * 
     * @param request to set
     * @return HttpServletRequest
     */
    private HttpServletRequest asHttp(ServletRequest request) {
        return (HttpServletRequest) request;
    }

    /**
     * Convert ServletResponse to HttpServletResponse.
     * 
     * @param response to set
     * @return HttpServletResponse
     */
    private HttpServletResponse asHttp(ServletResponse response) {
        return (HttpServletResponse) response;
    }

    /**
     * Post to management endpoints.
     * 
     * @param resourcePath to set
     * @return true/false
     */
    private boolean postToManagementEndpoints(String resourcePath) {
        return managementEndpoints.contains(resourcePath);
    }

    /**
     * Process management endpoint authentication.
     * 
     * @param username to set
     * @param password to set
     * @throws IOException to set
     */
    private void processManagementEndpointUsernamePasswordAuthentication(Optional<String> username,
            Optional<String> password) throws IOException {
        Authentication resultOfAuthentication = tryToAuthenticateWithUsernameAndPassword(username, password);
        SecurityContextHolder.getContext().setAuthentication(resultOfAuthentication);
    }

    /**
     * Authenticate with username and password.
     * 
     * @param username to set
     * @param password to set
     * @return Authentication
     */
    private Authentication tryToAuthenticateWithUsernameAndPassword(Optional<String> username,
            Optional<String> password) {
        BackendAdminUsernamePasswordAuthenticationToken requestAuthentication = new BackendAdminUsernamePasswordAuthenticationToken(
                username, password);
        return tryToAuthenticate(requestAuthentication);
    }

    /**
     * Authenticate with request.
     * 
     * @param requestAuthentication to set
     * @return Authentication
     */
    private Authentication tryToAuthenticate(Authentication requestAuthentication) {
        Authentication responseAuthentication = authenticationManager.authenticate(requestAuthentication);
        if (responseAuthentication == null || !responseAuthentication.isAuthenticated()) {
            throw new InternalAuthenticationServiceException(
                    "Unable to authenticate Backend Admin for provided credentials");
        }
        LOGGER.debug("Backend Admin successfully authenticated");
        return responseAuthentication;
    }
}
