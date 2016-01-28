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
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Role;
import ck.panda.domain.entity.User;
import ck.panda.domain.repository.jpa.DepartmentRepository;
import ck.panda.domain.repository.jpa.RoleReposiory;
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

    /** User service reference. */
    @Autowired
    private UserService userService;

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

    /** Role repository reference. */
    @Autowired
    private RoleReposiory roleReposiory;

    /** Department repository reference. */
    @Autowired
    private DepartmentRepository departmentRepository;

    /** Admin username. */
    @Value("${backend.admin.username}")
    private String backendAdminUsername;

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
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Optional<String> username = (Optional) authentication.getPrincipal();
        Optional<String> password = (Optional) authentication.getCredentials();

        AuthenticationWithToken resultOfAuthentication = null;
        if (username != null && password != null) {
            Optional<String> domain = (Optional) authentication.getDetails();
            resultOfAuthentication = authValidation(username, password, domain, resultOfAuthentication);
        } else {
            resultOfAuthentication = (AuthenticationWithToken) tokenAuthenticationProvider.authenticate(authentication);
        }
        return resultOfAuthentication;
    }

    /**
     * @param username login user name
     * @param password login user password
     * @param resultOfAuthentication authentication token object
     * @param domain login user domain
     * @return authentication token value
     * @throws AuthenticationException raise if error
     */
    public AuthenticationWithToken authValidation(Optional<String> username, Optional<String> password,
            Optional<String> domain, AuthenticationWithToken resultOfAuthentication) throws AuthenticationException {
        User user = null;
        try {
            user = userService.findByUser(username.get(), password.get(), "/");
            if (user == null && domain.get().equals("BACKEND_ADMIN")) {
                if (username.get().equals(backendAdminUsername) && password.get().equals(backendAdminPassword)) {
                    resultOfAuthentication = externalServiceAuthenticator.authenticate(backendAdminUsername,
                            backendAdminRole, null, null, buildNumber);
                    String newToken = null;
                    try {
                        newToken = tokenService.generateNewToken(user, "ROOT");
                    } catch (Exception e) {
                        LOGGER.error("Error to generating token :" + e);
                    }
                    resultOfAuthentication.setToken(newToken);
                    tokenService.store(newToken, resultOfAuthentication);
                } else {
                    throw new BadCredentialsException("Invalid login credentials");
                }
            } else {
                Boolean authResponse = csLoginAuthentication(username.get(), password.get(), domain.get());
                if (authResponse) {
                    if (!domain.get().equals("BACKEND_ADMIN")) {
                        user = userService.findByUser(username.get(), password.get(), domain.get());
                    }
                    if (user == null) {
                        throw new BadCredentialsException("Invalid login credentials");
                    } else if (user != null && !user.getIsActive()) {
                        throw new BadCredentialsException("Account is inactive. Please contact admin");
                    } else if (user != null && user.getRole() == null) {
                        throw new BadCredentialsException("Contact administrator to get the access permission granted");
                    } else {
                        Boolean authKeyResponse = apiSecretKeyGeneration(user);
                        if (authKeyResponse) {
                            Department department = departmentRepository.findOne(user.getDepartment().getId());
                            Role role = roleReposiory.findUniqueness(user.getRole().getName(), department.getId());
                            resultOfAuthentication = externalServiceAuthenticator.authenticate(username.get(),
                                    user.getRole().getName(), role, user, buildNumber);
                            String newToken = null;
                            try {
                                newToken = tokenService.generateNewToken(user, domain.get());
                            } catch (Exception e) {
                                LOGGER.error("Error to generating token :" + e);
                            }
                            resultOfAuthentication.setToken(newToken);
                            tokenService.store(newToken, resultOfAuthentication);
                        } else {
                            throw new BadCredentialsException("Problem for getting API and Secret key");
                        }
                    }
                } else {
                    throw new BadCredentialsException("Invalid login credentials");
                }
            }
        } catch (BadCredentialsException e) {
            LOGGER.error("Invalid login credentials : " + e);
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new BadCredentialsException(e.getMessage());
        }
        return resultOfAuthentication;
    }

    /**
     * Cloud stack connection to verify user authentication.
     *
     * @param username to set
     * @param password to set
     * @param domain to set
     * @return domain UUID
     * @throws Exception raise if error
     */
    private Boolean csLoginAuthentication(String username, String password, String domain) throws Exception {
        configUtil.setServer(1L);
        HashMap<String, String> optional = new HashMap<String, String>();
        if (domain.equals("BACKEND_ADMIN")) {
            optional.put("domain", "/");
        } else {
            optional.put("domain", domain);
        }
        String resp = cloudStackAuthenticationService.login(username, password, "json", optional);
        JSONObject userJSON = new JSONObject(resp).getJSONObject("loginresponse");
        if (userJSON.has("errorcode")) {
            return false;
        } else {
            return true;
        }
    }

    private Boolean apiSecretKeyGeneration(User user) throws Exception {
        configUtil.setServer(1L);
        HashMap<String, String> optional = new HashMap<String, String>();
        optional.put("id", user.getUuid());
        String listUserByIdResponse = cloudStackUserService.listUsers(optional, "json");
        JSONObject listUsersResponse = new JSONObject(listUserByIdResponse).getJSONObject("listusersresponse");
        if (listUsersResponse.has("errorcode")) {
            return false;
        } else {
            JSONArray userJsonobject = (JSONArray) listUsersResponse.get("user");
            if (userJsonobject.getJSONObject(0).has("apikey")) {
                user.setApiKey(userJsonobject.getJSONObject(0).get("apikey").toString());
                user.setSecretKey(userJsonobject.getJSONObject(0).get("secretkey").toString());
                return true;
            } else {
                String keyValueResponse = cloudStackUserService.registerUserKeys(user.getUuid(), "json");
                JSONObject keyValue = new JSONObject(keyValueResponse).getJSONObject("registeruserkeysresponse");
                if (keyValue.has("errorcode")) {
                    return false;
                } else {
                    user.setApiKey(keyValue.getJSONObject("userkeys").getString("apikey"));
                    user.setSecretKey(keyValue.getJSONObject("userkeys").getString("secretkey"));
                    return true;
                }
            }
        }
    }
}
