package ck.panda.util.infrastructure.security;

import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import com.google.common.base.Optional;
import ck.panda.constants.CloudStackConstants;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Role;
import ck.panda.domain.entity.User;
import ck.panda.service.DepartmentService;
import ck.panda.service.RoleService;
import ck.panda.service.UserService;
import ck.panda.util.CloudStackAuthenticationService;
import ck.panda.util.CloudStackUserService;
import ck.panda.util.ConfigUtil;

/**
 * Database authentication manager to handle all the validation and authentication for login users.
 *
 */
@Component
public class DatabaseAuthenticationManager implements AuthenticationManager {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseAuthenticationManager.class);

    /** Root admin flag. */
    public static final String BACKEND_ADMIN = "BACKEND_ADMIN";

    /** Root admin domain flag. */
    public static final String ROOT_DOMAIN = "ROOT";

    /** Root admin domain symbol. */
    public static final String ROOT_DOMAIN_SYMBOL = "/";

    /** Cloud stack user key response. */
    public static final String USER_KEYS = "userkeys";

    /** Cloud stack user response. */
    public static final String CS_USER = "user";

    /** Cloud stack optional value for domain name. */
    public static final String CS_DOMAIN = "domain";

    /** External service authenticator reference. */
    @Autowired
    private ExternalServiceAuthenticator externalServiceAuthenticator;

    /** Authentication provider reference. */
    @Autowired
    private AuthenticationProvider tokenAuthenticationProvider;

    /** Token service reference. */
    @Autowired
    private TokenService tokenService;

    /** Cloud stack configuration reference. */
    @Autowired
    private ConfigUtil configUtil;

    /** Cloud stack authentication service. */
    @Autowired
    private CloudStackAuthenticationService cloudStackAuthenticationService;

    /** Cloud stack user service. */
    @Autowired
    private CloudStackUserService cloudStackUserService;

    /** User service reference. */
    @Autowired
    private UserService userService;

    /** Role service reference. */
    @Autowired
    private RoleService roleService;

    /** Department service reference. */
    @Autowired
    private DepartmentService departmentService;

    /** Admin user name. */
    @Value("${backend.admin.username}")
    private String backendAdminUserName;

    /** Admin password. */
    @Value("${backend.admin.password}")
    private String backendAdminPassword;

    /** Admin role. */
    @Value("${backend.admin.role}")
    private String backendAdminRole;

    /** Build Version. */
    @Value("${app.buildversion}")
    private String buildNumber;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException, BadCredentialsException {
        Optional<String> userName = (Optional) authentication.getPrincipal();
        Optional<String> password = (Optional) authentication.getCredentials();

        AuthenticationWithToken resultOfAuthentication = null;
        if (userName != null && password != null) {
            Optional<String> domain = (Optional) authentication.getDetails();
            resultOfAuthentication = authValidation(userName, password, domain, resultOfAuthentication);
        } else {
            resultOfAuthentication = (AuthenticationWithToken) tokenAuthenticationProvider.authenticate(authentication);
        }
        return resultOfAuthentication;
    }

    /**
     * Authenticate user login details and return the token information.
     *
     * @param userName login user name
     * @param password login user password
     * @param resultOfAuthentication authentication token object
     * @param domain login user domain
     * @return authentication token value
     * @throws AuthenticationException if authentication exception occurs.
     */
    public AuthenticationWithToken authValidation(Optional<String> userName, Optional<String> password,
            Optional<String> domain, AuthenticationWithToken resultOfAuthentication) throws AuthenticationException {
        User user = null;
        try {
            user = userService.findByUser(userName.get(), password.get(), ROOT_DOMAIN_SYMBOL);
            if (user == null && domain.get().equals(BACKEND_ADMIN)) {
                resultOfAuthentication = adminDefaultLoginAuthentication(userName, password, resultOfAuthentication,
                        user);
            } else {
                Boolean authResponse = csLoginAuthentication(userName.get(), password.get(), domain.get());
                if (authResponse) {
                    if (!domain.get().equals(BACKEND_ADMIN)) {
                        user = userService.findByUser(userName.get(), password.get(), domain.get());
                    }
                    resultOfAuthentication = userLoginAuthentication(userName, domain, resultOfAuthentication, user);
                } else {
                    throw new BadCredentialsException("error.login.credentials");
                }
            }
        } catch (BadCredentialsException e) {
            LOGGER.error("Invalid login credentials exception : " + e);
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new BadCredentialsException(e.getMessage());
        }
        return resultOfAuthentication;
    }

    /**
     * Authenticate and generate token using default admin user details.
     *
     * @param userName to set
     * @param password to set
     * @param resultOfAuthentication to set
     * @param user to set
     * @return admin default authentication token
     */
    public AuthenticationWithToken adminDefaultLoginAuthentication(Optional<String> userName, Optional<String> password,
            AuthenticationWithToken resultOfAuthentication, User user) {
        if (userName.get().equals(backendAdminUserName) && password.get().equals(backendAdminPassword)) {
            resultOfAuthentication = externalServiceAuthenticator.authenticate(backendAdminUserName, backendAdminRole,
                    null, null, buildNumber);
            String newToken = null;
            try {
                newToken = tokenService.generateNewToken(user, ROOT_DOMAIN);
            } catch (Exception e) {
                LOGGER.error("Error for generating token : " + e);
            }
            resultOfAuthentication.setToken(newToken);
            tokenService.store(newToken, resultOfAuthentication);
        } else {
            throw new BadCredentialsException("error.login.credentials");
        }
        return resultOfAuthentication;
    }

    /**
     * Authenticate and generate token using user login details.
     *
     * @param userName to set
     * @param domain to set
     * @param resultOfAuthentication to set
     * @param user to set
     * @return user authentication token
     * @throws Exception unhandled exceptions.
     */
    public AuthenticationWithToken userLoginAuthentication(Optional<String> userName, Optional<String> domain,
            AuthenticationWithToken resultOfAuthentication, User user) throws Exception {
        if (user == null) {
            throw new BadCredentialsException("error.login.credentials");
        } else if (user != null && !user.getIsActive()) {
            throw new BadCredentialsException("error.inactive.login.credentials");
        } else if (user != null && user.getRole() == null) {
            throw new BadCredentialsException("error.access.permission.blocked");
        } else {
            Boolean authKeyResponse = apiSecretKeyGeneration(user);
            if (authKeyResponse) {
                Department department = departmentService.find(user.getDepartment().getId());
                Role role = roleService.findWithPermissionsByNameDepartmentAndIsActive(user.getRole().getName(), department.getId(), true);
                resultOfAuthentication = externalServiceAuthenticator.authenticate(userName.get(),
                        user.getRole().getName(), role, user, buildNumber);
                String newToken = null;
                try {
                    newToken = tokenService.generateNewToken(user, domain.get());
                } catch (Exception e) {
                    LOGGER.error("Error for generating token:" + e);
                }
                resultOfAuthentication.setToken(newToken);
                tokenService.store(newToken, resultOfAuthentication);
            } else {
                throw new BadCredentialsException("error.apikey.generate.problem");
            }
        }
        return resultOfAuthentication;
    }

    /**
     * Cloud stack connection to verify user authentication.
     *
     * @param userName to set
     * @param password to set
     * @param domain to set
     * @return authentication status true/false
     * @throws Exception unhandled exceptions.
     */
    private Boolean csLoginAuthentication(String userName, String password, String domain) throws Exception {
        configUtil.setServer(1L);
        HashMap<String, String> optional = new HashMap<String, String>();
        if (domain.equals(BACKEND_ADMIN)) {
            optional.put(CS_DOMAIN, ROOT_DOMAIN_SYMBOL);
        } else {
            optional.put(CS_DOMAIN, domain);
        }
        String loginResponse = cloudStackAuthenticationService.login(userName, password, CloudStackConstants.JSON,
                optional);
        JSONObject userJSON = new JSONObject(loginResponse).getJSONObject(CloudStackConstants.CS_LOGIN_RESPONSE);
        if (userJSON.has(CloudStackConstants.CS_ERROR_CODE)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Create the API and Secret key for the login user.
     *
     * @param user to set
     * @return API and Secret key status true/false
     * @throws Exception unhandled exceptions.
     */
    private Boolean apiSecretKeyGeneration(User user) throws Exception {
        configUtil.setServer(1L);
        HashMap<String, String> optional = new HashMap<String, String>();
        optional.put(CloudStackConstants.CS_ID, user.getUuid());
        String listUserByIdResponse = cloudStackUserService.listUsers(optional, CloudStackConstants.JSON);
        JSONObject listUsersResponse = new JSONObject(listUserByIdResponse)
                .getJSONObject(CloudStackConstants.CS_LIST_USER_RESPONSE);
        if (listUsersResponse.has(CloudStackConstants.CS_ERROR_CODE)) {
            return false;
        } else {
            JSONArray userJsonobject = (JSONArray) listUsersResponse.get(CS_USER);
            if (userJsonobject.getJSONObject(0).has(CloudStackConstants.CS_API_KEY)) {
                user.setApiKey(userJsonobject.getJSONObject(0).get(CloudStackConstants.CS_API_KEY).toString());
                user.setSecretKey(userJsonobject.getJSONObject(0).get(CloudStackConstants.CS_SECRET_KEY).toString());
                return true;
            } else {
                String keyValueResponse = cloudStackUserService.registerUserKeys(user.getUuid(),
                        CloudStackConstants.JSON);
                JSONObject keyValue = new JSONObject(keyValueResponse)
                        .getJSONObject(CloudStackConstants.CS_REGISTER_KEY_RESPONSE);
                if (keyValue.has(CloudStackConstants.CS_ERROR_CODE)) {
                    return false;
                } else {
                    user.setApiKey(keyValue.getJSONObject(USER_KEYS).getString(CloudStackConstants.CS_API_KEY));
                    user.setSecretKey(keyValue.getJSONObject(USER_KEYS).getString(CloudStackConstants.CS_SECRET_KEY));
                    return true;
                }
            }
        }
    }
}
