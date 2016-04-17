package ck.panda.util.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import ck.panda.constants.CloudStackConstants;
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.RolePrincipal;
import ck.panda.util.TokenDetails;
import ck.panda.util.error.MessageByLocaleService;
import ck.panda.util.infrastructure.AuthenticatedExternalWebService;
import ck.panda.util.infrastructure.externalwebservice.ExternalWebServiceStub;
import ck.panda.util.web.ApiController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
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
import java.util.HashMap;

/**
 * Authentication filter.
 *
 */
public class AuthenticationFilter extends GenericFilterBean {

    /** Logger constant. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);

    /** Token session key constant. */
    public static final String TOKEN_SESSION_KEY = "token";

    /** User session key constant. */
    public static final String USER_SESSION_KEY = "user";

    /** Empty token value. */
    public static final String EMPTY_VALUE = "EMPTY";

    /** Password encoder algorithm. */
    public static final String PASSWORD_ENCODER = "SHA-1";

    /** Random salt text. */
    public static final String SALT_SPRINKLE = "not_so_random_salt";

    /** Authentication user name. */
    public static final String XAUTH_USERNAME = "x-auth-username";

    /** Authentication password. */
    public static final String XAUTH_PASSWORD = "x-auth-password";

    /** Authentication login token. */
    public static final String XAUTH_LOGIN_TOKEN = "x-auth-login-token";

    /** Authentication user id. */
    public static final String XAUTH_USER_ID = "x-auth-user-id";

    /** Authentication remember. */
    public static final String XAUTH_REMEMBER_ME = "x-auth-remember";

    /** Authentication force login flag. */
    public static final String XFORCE_LOGIN = "x-force-login";

    /** Authentication token. */
    public static final String XAUTH_TOKEN = "x-auth-token";

    /** Authentication request flag. */
    public static final String XAUTH_REQUEST = "x-requested-with";

    /** Domain name of the login user. */
    public static final String DOMAIN_NAME = "domainname";

    /** Root admin domain flag. */
    public static final String ROOT_DOMAIN = "ROOT";

    /** HTTP request post action. */
    public static final String HTTP_POST = "POST";

    /** HTTP request post action. */
    public static final String REMEMBER_ME = "rememberMe";

    /** HTTP request post action. */
    public static final String FORCE_LOGIN = "forceLogin";

    /** Authentication manager attribute. */
    private DatabaseAuthenticationManager databaseAuthenticationManager;

    /** Token details attribute. */
    private TokenDetails userTokenDetails;

    /** Message properties service attribute. */
    private MessageByLocaleService messageByLocaleService;

    /**
     * Parameterized constructor.
     *
     * @param databaseAuthenticationManager to set
     * @param userTokenDetails to set
     */
    public AuthenticationFilter(DatabaseAuthenticationManager databaseAuthenticationManager,
            TokenDetails userTokenDetails, MessageByLocaleService messageByLocaleService) {
        this.databaseAuthenticationManager = databaseAuthenticationManager;
        this.userTokenDetails = userTokenDetails;
        this.messageByLocaleService = messageByLocaleService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException  {
        HttpServletRequest httpRequest = asHttp(request);
        HttpServletResponse httpResponse = asHttp(response);
        Optional<String> userName = Optional.fromNullable(httpRequest.getHeader(XAUTH_USERNAME));
        Optional<String> password = Optional.fromNullable(httpRequest.getHeader(XAUTH_PASSWORD));
        Optional<String> rememberMe = Optional.fromNullable(httpRequest.getHeader(XAUTH_REMEMBER_ME));
        Optional<String> loginToken = Optional.fromNullable(httpRequest.getHeader(XAUTH_LOGIN_TOKEN));
        Optional<String> userId = Optional.fromNullable(httpRequest.getHeader(XAUTH_USER_ID));
        Optional<String> token = Optional.fromNullable(httpRequest.getHeader(XAUTH_TOKEN));
        Optional<String> domain = Optional.fromNullable(httpRequest.getHeader(XAUTH_REQUEST));
        Optional<String> forceLogin = Optional.fromNullable(httpRequest.getHeader(XFORCE_LOGIN));
        String resourcePath = new UrlPathHelper().getPathWithinApplication(httpRequest);
        try {
            if (resourcePath.contains("socket")) {
                LOGGER.debug("Trying to authenticate user by x-auth-token method : ", token);
                ExternalWebServiceStub externalWebService = new ExternalWebServiceStub();
                AuthenticatedExternalWebService authenticatedExternalWebService = new AuthenticatedExternalWebService(
                        "pandasocket", null, AuthorityUtils.commaSeparatedStringToAuthorityList("BACKEND_ADMIN"));
                authenticatedExternalWebService.setExternalWebService(externalWebService);
                SecurityContextHolder.getContext().setAuthentication(authenticatedExternalWebService);
            }
            if (resourcePath.contains("panda") && !resourcePath.contains("usersessiondetails")) {
                ExternalWebServiceStub externalWebService = new ExternalWebServiceStub();
                AuthenticatedExternalWebService authenticatedExternalWebService = new AuthenticatedExternalWebService(
                        "pandapay", null, AuthorityUtils.commaSeparatedStringToAuthorityList("BACKEND_ADMIN"));
                authenticatedExternalWebService.setExternalWebService(externalWebService);
                SecurityContextHolder.getContext().setAuthentication(authenticatedExternalWebService);
            }
            if (postToAuthenticate(httpRequest, resourcePath)) {
                LOGGER.debug("Trying to authenticate user by x-auth-username method : ", userName);
                processUsernamePasswordAuthentication(httpRequest, httpResponse, userName, password, domain, rememberMe, loginToken, userId, forceLogin);
                return;
            }
            if (token.isPresent() || !token.get().isEmpty()) {
                LOGGER.debug("Trying to authenticate user by x-auth-token method : ", token);
                processTokenAuthentication(token, loginToken, userId, httpRequest);
            }
            LOGGER.debug("Authentication filter is passing request down the filter chain");
            addSessionContextToLogging();
            chain.doFilter(request, response);
        } catch (InternalAuthenticationServiceException internalAuthenticationServiceException) {
            SecurityContextHolder.clearContext();
            LOGGER.error("Internal authentication service exception : ", internalAuthenticationServiceException);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            httpResponse.setCharacterEncoding(GenericConstants.CHARACTER_ENCODING);
            httpResponse.getWriter().write("{\"message\":\"" + messageByLocaleService.getMessage(internalAuthenticationServiceException.getMessage()) + "\"}");
        } catch (AuthenticationException authenticationException) {
            SecurityContextHolder.clearContext();
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            httpResponse.setCharacterEncoding(GenericConstants.CHARACTER_ENCODING);
            httpResponse.getWriter().write("{\"message\":\"" + messageByLocaleService.getMessage(authenticationException.getMessage()) + "\"}");
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            MDC.remove(TOKEN_SESSION_KEY);
            MDC.remove(USER_SESSION_KEY);
        }
    }

    /** Add session context to logging. */
    private void addSessionContextToLogging() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String tokenValue = EMPTY_VALUE;
        if (authentication != null && authentication.getPrincipal().equals("pandapay")) {
            MessageDigestPasswordEncoder encoder = new MessageDigestPasswordEncoder(PASSWORD_ENCODER);
            tokenValue = encoder.encodePassword(authentication.getAuthorities().toString(), SALT_SPRINKLE);
        } else if (authentication != null && authentication.getPrincipal().equals("pandasocket")) {
            MessageDigestPasswordEncoder encoder = new MessageDigestPasswordEncoder(PASSWORD_ENCODER);
            tokenValue = encoder.encodePassword(authentication.getAuthorities().toString(), SALT_SPRINKLE);
        } else if (authentication != null && !Strings.isNullOrEmpty(authentication.getDetails().toString())) {
            MessageDigestPasswordEncoder encoder = new MessageDigestPasswordEncoder(PASSWORD_ENCODER);
            tokenValue = encoder.encodePassword(authentication.getDetails().toString(), SALT_SPRINKLE);
        }
        MDC.put(TOKEN_SESSION_KEY, tokenValue);
        String userValue = EMPTY_VALUE;
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
     * Post to authenticate process.
     *
     * @param httpRequest to set
     * @param resourcePath to set
     * @return true/false
     */
    private boolean postToAuthenticate(HttpServletRequest httpRequest, String resourcePath) {
        return ApiController.AUTHENTICATE_URL.equalsIgnoreCase(resourcePath)
               && httpRequest.getMethod().equals(HTTP_POST);
    }

    /**
     * Process the user login authentication.
     *
     * @param httpRequest to set
     * @param httpResponse to set
     * @param userName to set
     * @param password to set
     * @param domain name to set
     * @param rememberMe to set
     * @param loginToken to set
     * @param userId to set
     * @param forceLogin to set
     * @throws IOException if I/O exception occurs.
     */
    private void processUsernamePasswordAuthentication(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
            Optional<String> userName, Optional<String> password, Optional<String> domain, Optional<String> rememberMe, Optional<String> loginToken, Optional<String> userId, Optional<String> forceLogin) throws IOException {
        Authentication resultOfAuthentication = tryToAuthenticateWithUsernameAndPassword(httpRequest, userName,
                password, domain, rememberMe, loginToken, userId, forceLogin);
        SecurityContextHolder.getContext().setAuthentication(resultOfAuthentication);
        httpResponse.setStatus(HttpServletResponse.SC_OK);
        httpResponse.addHeader(GenericConstants.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        try {
            if (userTokenDetails.getTokenDetails(DOMAIN_NAME).equals(ROOT_DOMAIN)) {
                TokenResponse tokenResponse = new TokenResponse(resultOfAuthentication.getDetails().toString());
                Object rememberResponse = resultOfAuthentication.getPrincipal();
                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                hashMap.put("tokenResponse",tokenResponse);
                hashMap.put("rememberResponse", rememberResponse);
                String tokenJsonResponse = new ObjectMapper().writeValueAsString(hashMap);
                httpResponse.getWriter().print(tokenJsonResponse);
            } else {
                RolePrincipal rolePrincipal = (RolePrincipal) resultOfAuthentication.getPrincipal();
                httpResponse.getWriter().print(rolePrincipal);
            }
        } catch (Exception e) {
            LOGGER.error("User name and password authentication exception");
        }
    }

    /**
     * Try to authenticate with user name and password.
     *
     * @param httpRequest to set
     * @param userName to set
     * @param password to set
     * @param rememberMe to set
     * @param loginToken to set
     * @param userId to set
     * @param forceLogin to set
     * @return Authentication
     */
    private Authentication tryToAuthenticateWithUsernameAndPassword(HttpServletRequest httpRequest,
            Optional<String> userName, Optional<String> password, Optional<String> domain, Optional<String> rememberMe, Optional<String> loginToken, Optional<String> userId, Optional<String> forceLogin) {
        UsernamePasswordAuthenticationToken requestAuthentication = new UsernamePasswordAuthenticationToken(userName,
                password);
        if (rememberMe.isPresent()) {
            HashMap<String, String> loginMap = new HashMap<String, String>();
            loginMap.put(CloudStackConstants.CS_DOMAIN, domain.get());
            loginMap.put(REMEMBER_ME, rememberMe.get());
            loginMap.put(FORCE_LOGIN, forceLogin.get());
            requestAuthentication.setDetails(loginMap);
        }
        return tryToAuthenticate(requestAuthentication, loginToken, userId, httpRequest);
    }

    /**
     * Process token authentication.
     *
     * @param token to set
     * @param loginToken
     * @param httpRequest to set
     */
    private void processTokenAuthentication(Optional<String> token, Optional<String> loginToken, Optional<String> userId, HttpServletRequest httpRequest) {
        Authentication resultOfAuthentication = tryToAuthenticateWithToken(token, loginToken, userId, httpRequest);
        SecurityContextHolder.getContext().setAuthentication(resultOfAuthentication);
    }

    /**
     * Try to authenticate with token.
     *
     * @param token to set
     * @param httpRequest to set
     * @return Authentication
     */
    private Authentication tryToAuthenticateWithToken(Optional<String> token, Optional<String> loginToken, Optional<String> userId, HttpServletRequest httpRequest) {
        PreAuthenticatedAuthenticationToken requestAuthentication = new PreAuthenticatedAuthenticationToken(token,
                null);
        HashMap<String, String> setLoginToken = new HashMap<String, String>();
        if (userId.isPresent()) {
            setLoginToken.put("loginToken", loginToken.get());
        }
        if (userId.isPresent()) {
            setLoginToken.put("userId", userId.get());
        }
        requestAuthentication.setDetails(setLoginToken);
        return tryToAuthenticate(requestAuthentication, loginToken, userId, httpRequest);
    }

    /**
     * Try to authenticate.
     *
     * @param requestAuthentication to set
     * @param httpRequest to set
     * @return Authentication
     */
    private Authentication tryToAuthenticate(Authentication requestAuthentication, Optional<String> loginToken, Optional<String> userId, HttpServletRequest httpRequest) {
        Authentication responseAuthentication = databaseAuthenticationManager.authenticate(requestAuthentication);
        DateFormat dateFormat = new SimpleDateFormat(GenericConstants.AUTH_DATE_FORMAT);
        if (responseAuthentication == null || !responseAuthentication.isAuthenticated()) {
            LOGGER.debug("User authentication failed : " + httpRequest.getServletPath() + " : "
                    + dateFormat.format(new Date()));
            throw new BadCredentialsException(messageByLocaleService.getMessage("error.unable.to.authenticate.domain.user"));
        }
        LOGGER.debug("User authentication success : " + httpRequest.getServletPath() + " : "
                + dateFormat.format(new Date()));
        return responseAuthentication;
    }
}
