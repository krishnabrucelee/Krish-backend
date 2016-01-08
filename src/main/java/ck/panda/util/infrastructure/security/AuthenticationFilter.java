package ck.panda.util.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import ck.panda.domain.entity.RolePrincipal;
import ck.panda.util.TokenDetails;
import ck.panda.util.web.ApiController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UrlPathHelper;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Authentication filter.
 *
 */
public class AuthenticationFilter extends GenericFilterBean {

    /** Logger constant. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);

    /** Token session key constant. */
    public static final String TOKEN_SESSION_KEY = "token";

    /** User session constant. */
    public static final String USER_SESSION_KEY = "user";

    /** Authentication manager attribute. */
    private DatabaseAuthenticationManager databaseAuthenticationManager;

    /** TokenDetails attribute. */
    private TokenDetails userTokenDetails;

    /**
     * Parameterized constructor.
     *
     * @param databaseAuthenticationManager to set
     */
    public AuthenticationFilter(DatabaseAuthenticationManager databaseAuthenticationManager,
            TokenDetails userTokenDetails) {
        this.databaseAuthenticationManager = databaseAuthenticationManager;
        this.userTokenDetails = userTokenDetails;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = asHttp(request);
        HttpServletResponse httpResponse = asHttp(response);

        Optional<String> username = Optional.fromNullable(httpRequest.getHeader("x-auth-username"));
        Optional<String> password = Optional.fromNullable(httpRequest.getHeader("x-auth-password"));
        Optional<String> token = Optional.fromNullable(httpRequest.getHeader("x-auth-token"));
        Optional<String> domain = Optional.fromNullable(httpRequest.getHeader("x-requested-with"));

        String resourcePath = new UrlPathHelper().getPathWithinApplication(httpRequest);
        try {
            if (postToAuthenticate(httpRequest, resourcePath)) {
                LOGGER.debug("Trying to authenticate user {} by x-auth-username method", username);
                processUsernamePasswordAuthentication(httpRequest, httpResponse, username, password, domain);
                return;
            }

            if (token.isPresent()) {
                LOGGER.debug("Trying to authenticate user by x-auth-token method. Token: {}", token);
                processTokenAuthentication(token, httpRequest);
            }

            LOGGER.debug("AuthenticationFilter is passing request down the filter chain");
            addSessionContextToLogging();
            chain.doFilter(request, response);
        } catch (InternalAuthenticationServiceException internalAuthenticationServiceException) {
            SecurityContextHolder.clearContext();
            LOGGER.error("Internal authentication service exception", internalAuthenticationServiceException);
            httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (AuthenticationException authenticationException) {
            SecurityContextHolder.clearContext();
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, authenticationException.getMessage());
        } finally {
            MDC.remove(TOKEN_SESSION_KEY);
            MDC.remove(USER_SESSION_KEY);
        }
    }

    /** Add session context to logging. */
    private void addSessionContextToLogging() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String tokenValue = "EMPTY";
        if (authentication != null && !Strings.isNullOrEmpty(authentication.getDetails().toString())) {
            MessageDigestPasswordEncoder encoder = new MessageDigestPasswordEncoder("SHA-1");
            tokenValue = encoder.encodePassword(authentication.getDetails().toString(), "not_so_random_salt");
        }
        MDC.put(TOKEN_SESSION_KEY, tokenValue);

        String userValue = "EMPTY";
        if (authentication != null && !Strings.isNullOrEmpty(authentication.getPrincipal().toString())) {
            userValue = authentication.getPrincipal().toString();
        }
        MDC.put(USER_SESSION_KEY, userValue);
    }

    /**
     * Convert servlet request to http servlet request.
     *
     * @param request to set
     * @return HttpServletRequest
     */
    private HttpServletRequest asHttp(ServletRequest request) {
        return (HttpServletRequest) request;
    }

    /**
     * Convert servlet response to http servlet response.
     *
     * @param response to set
     * @return HttpServletResponse
     */
    private HttpServletResponse asHttp(ServletResponse response) {
        return (HttpServletResponse) response;
    }

    /**
     * Post to authentivate.
     *
     * @param httpRequest to set
     * @param resourcePath to set
     * @return true/false
     */
    private boolean postToAuthenticate(HttpServletRequest httpRequest, String resourcePath) {
        return ApiController.AUTHENTICATE_URL.equalsIgnoreCase(resourcePath) && httpRequest.getMethod().equals("POST");
    }

    /**
     * Process authentication.
     *
     * @param httpResponse to set
     * @param username to set
     * @param password to set
     * @param domain to set
     * @throws IOException if any error occurs
     */
    private void processUsernamePasswordAuthentication(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
            Optional<String> username, Optional<String> password, Optional<String> domain) throws IOException {
        Authentication resultOfAuthentication = tryToAuthenticateWithUsernameAndPassword(httpRequest, username,
                password, domain);
        SecurityContextHolder.getContext().setAuthentication(resultOfAuthentication);
        httpResponse.setStatus(HttpServletResponse.SC_OK);
        httpResponse.addHeader("Content-Type", "application/json");
        try {
            if (userTokenDetails.getTokenDetails("domainname").equals("ROOT")) {
                TokenResponse tokenResponse = new TokenResponse(resultOfAuthentication.getDetails().toString());
                String tokenJsonResponse = new ObjectMapper().writeValueAsString(tokenResponse);
                httpResponse.getWriter().print(tokenJsonResponse);
            } else {
                RolePrincipal rolePrincipal = (RolePrincipal) resultOfAuthentication.getPrincipal();
                httpResponse.getWriter().print(rolePrincipal);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Try to authenticate with username and password.
     *
     * @param username to set
     * @param password to set
     * @param domain to set
     * @return Authentication
     */
    private Authentication tryToAuthenticateWithUsernameAndPassword(HttpServletRequest httpRequest,
            Optional<String> username, Optional<String> password, Optional<String> domain) {
        UsernamePasswordAuthenticationToken requestAuthentication = new UsernamePasswordAuthenticationToken(username,
                password);
        requestAuthentication.setDetails(domain);
        return tryToAuthenticate(requestAuthentication, httpRequest);
    }

    /**
     * Process token authentication.
     *
     * @param token to set
     */
    private void processTokenAuthentication(Optional<String> token, HttpServletRequest httpRequest) {
        Authentication resultOfAuthentication = tryToAuthenticateWithToken(token, httpRequest);
        SecurityContextHolder.getContext().setAuthentication(resultOfAuthentication);
    }

    /**
     * Try to authenticate with token.
     *
     * @param token to set
     * @return Authentication
     */
    private Authentication tryToAuthenticateWithToken(Optional<String> token, HttpServletRequest httpRequest) {
        PreAuthenticatedAuthenticationToken requestAuthentication = new PreAuthenticatedAuthenticationToken(token,
                null);
        return tryToAuthenticate(requestAuthentication, httpRequest);
    }

    /**
     * Try to authenticate.
     *
     * @param requestAuthentication to set
     * @return Authentication
     */
    private Authentication tryToAuthenticate(Authentication requestAuthentication, HttpServletRequest httpRequest) {
        Authentication responseAuthentication = databaseAuthenticationManager.authenticate(requestAuthentication);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        if (responseAuthentication == null || !responseAuthentication.isAuthenticated()) {
            LOGGER.debug("User authentication failed : " + httpRequest.getServletPath() + " : "
                    + dateFormat.format(new Date()));
            throw new InternalAuthenticationServiceException(
                    "Unable to authenticate Domain User for provided credentials");
        }
        LOGGER.debug("User authentication success : " + httpRequest.getServletPath() + " : "
                + dateFormat.format(new Date()));
        return responseAuthentication;
    }
}
