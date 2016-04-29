package ck.panda.util.infrastructure.security;

import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.UUID;
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
import ck.panda.domain.entity.GeneralConfiguration;
import ck.panda.domain.entity.LoginHistory;
import ck.panda.domain.entity.LoginSecurityTrack;
import ck.panda.domain.entity.Role;
import ck.panda.domain.entity.User;
import ck.panda.service.DepartmentService;
import ck.panda.service.GeneralConfigurationService;
import ck.panda.service.LoginHistoryService;
import ck.panda.service.LoginSecurityTrackService;
import ck.panda.service.RoleService;
import ck.panda.service.UserService;
import ck.panda.util.CloudStackAuthenticationService;
import ck.panda.util.CloudStackUserService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.DateConvertUtil;

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

    /** Login security track service reference. */
    @Autowired
    private LoginSecurityTrackService loginSecurityTrackService;

    /** General configuration service reference. */
    @Autowired
    private GeneralConfigurationService generalConfigurationService;

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

    /** Secret key value is append. */
    @Value(value = "${aes.salt.secretKey}")
    private String secretKey;

    /** Login History Service attribute. */
    @Autowired
    private LoginHistoryService loginHistoryService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException, BadCredentialsException {
        Optional<String> userName = (Optional) authentication.getPrincipal();
        Optional<String> password = (Optional) authentication.getCredentials();

        AuthenticationWithToken resultOfAuthentication = null;
        String rememberMe = null;
        String forceLogin = null;
        String forwardedFor = null;
        HashMap<String, String> loginMap = (HashMap<String, String>) authentication.getDetails();
        if (userName != null && password != null) {
            String domain = null;
            if (loginMap.containsKey(CS_DOMAIN)) {
                domain = loginMap.get(CS_DOMAIN);
            } else {
                domain = loginMap.get(BACKEND_ADMIN);
            }
            if (!loginMap.get("rememberMe").equals(false)) {
                rememberMe = loginMap.get("rememberMe");
            }
            forceLogin = loginMap.get("forceLogin");
            forwardedFor = loginMap.get("forwardedFor");
            resultOfAuthentication = authValidation(userName, password, domain, rememberMe, resultOfAuthentication, forceLogin, forwardedFor);
        } else {
            LoginHistory loginHistory = loginHistoryService.findByLoginToken(loginMap.get("loginToken"));
            if (loginHistory == null && !loginMap.get("loginToken").equals("-1")) {
                throw new BadCredentialsException("Your credentials are used by another user. So you are logging out.");
            } else {
                resultOfAuthentication = (AuthenticationWithToken) tokenAuthenticationProvider.authenticate(authentication);
            }
        }
        return resultOfAuthentication;
    }

    /**
     * Authenticate user login details and return the token information.
     *
     * @param userName to set
     * @param password to set
     * @param resultOfAuthentication to set
     * @param domain to set
     * @param rememberMe to set
     * @param forceLogin to set
     * @param forwardedFor to set
     * @return authentication token value
     * @throws AuthenticationException if authentication exception occurs.
     */
    public AuthenticationWithToken authValidation(Optional<String> userName, Optional<String> password,
            String domain, String rememberMe, AuthenticationWithToken resultOfAuthentication, String forceLogin, String forwardedFor) throws AuthenticationException {
        User user = null;
        try {
            user = userService.findByUser(userName.get(), password.get(), ROOT_DOMAIN_SYMBOL);
            if (user == null && domain.equals(BACKEND_ADMIN)) {
                resultOfAuthentication = adminDefaultLoginAuthentication(userName, password, rememberMe, resultOfAuthentication,
                        user, forwardedFor);
            } else {
                Boolean authResponse = csLoginAuthentication(userName.get(), password.get(), domain);
                if (authResponse) {
                    if (!domain.equals(BACKEND_ADMIN)) {
                        user = userService.findByUser(userName.get(), password.get(), domain);
                    }
                    resultOfAuthentication = userLoginAuthentication(userName, domain, password, rememberMe, resultOfAuthentication, user, forceLogin, forwardedFor);
                } else {
                    loginAttemptvalidationCheck("error.login.credentials", CloudStackConstants.STATUS_INACTIVE, forwardedFor);
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
     * @param rememberMe to set
     * @param resultOfAuthentication to set
     * @param user to set
     * @param forwardedFor to set
     * @return admin default authentication token
     * @throws Exception unhandled exceptions.
     */
    public AuthenticationWithToken adminDefaultLoginAuthentication(Optional<String> userName, Optional<String> password,
            String rememberMe, AuthenticationWithToken resultOfAuthentication, User user, String forwardedFor) throws Exception {
        if (userName.get().equals(backendAdminUserName) && password.get().equals(backendAdminPassword)) {
            String loginToken = UUID.randomUUID().toString();
            resultOfAuthentication = externalServiceAuthenticator.authenticate(backendAdminUserName, backendAdminRole,
                    null, null, buildNumber, rememberMe, loginToken);
            String newToken = null;
            try {
                newToken = tokenService.generateNewToken(user, ROOT_DOMAIN, rememberMe);
            } catch (Exception e) {
                LOGGER.error("Error for generating token : " + e);
            }
            resultOfAuthentication.setToken(newToken);
            tokenService.store(newToken, resultOfAuthentication);
            loginHistoryService.saveLoginDetails(userName.get(), password.get(), BACKEND_ADMIN, rememberMe, loginToken);
        } else {
            loginAttemptvalidationCheck("error.login.credentials", CloudStackConstants.STATUS_INACTIVE, forwardedFor);
        }
        return resultOfAuthentication;
    }

    /**
     * Authenticate and generate token using user login details.
     *
     * @param userName to set
     * @param domain to set
     * @param password to set
     * @param rememberMe to set
     * @param resultOfAuthentication to set
     * @param user to set
     * @param forceLogin to set
     * @param forwardedFor to set
     * @return user authentication token
     * @throws Exception unhandled exceptions.
     */
    public AuthenticationWithToken userLoginAuthentication(Optional<String> userName, String domain, Optional<String> password, String rememberMe,
            AuthenticationWithToken resultOfAuthentication, User user, String forceLogin, String forwardedFor) throws Exception {
        if (user == null) {
            loginAttemptvalidationCheck("error.login.credentials", CloudStackConstants.STATUS_INACTIVE, forwardedFor);
        } else if (user != null && !user.getIsActive()) {
            loginAttemptvalidationCheck("error.inactive.login.credentials", CloudStackConstants.STATUS_INACTIVE, forwardedFor);
        } else if (user != null && user.getRole() == null) {
            loginAttemptvalidationCheck("error.access.permission.blocked", CloudStackConstants.STATUS_INACTIVE, forwardedFor);
        } else {
            Boolean loginAttemptCheck = loginAttemptvalidationCheck("success", CloudStackConstants.STATUS_ACTIVE, forwardedFor);
            Boolean authKeyResponse = apiSecretKeyGeneration(user);
            if (authKeyResponse && loginAttemptCheck) {
                Boolean forceLoginResponse = forceLoginAttemptCheck(user, forceLogin);
                if (forceLoginResponse) {
                String loginToken = UUID.randomUUID().toString();
                Department department = departmentService.find(user.getDepartment().getId());
                Role role = roleService.findWithPermissionsByNameDepartmentAndIsActive(user.getRole().getName(), department.getId(), true);
                resultOfAuthentication = externalServiceAuthenticator.authenticate(userName.get(),
                        user.getRole().getName(), role, user, buildNumber, rememberMe, loginToken);
                String newToken = null;
                try {
                    newToken = tokenService.generateNewToken(user, domain, rememberMe);
                } catch (Exception e) {
                    LOGGER.error("Error for generating token:" + e);
                }
                resultOfAuthentication.setToken(newToken);
                tokenService.store(newToken, resultOfAuthentication);
                loginHistoryService.saveLoginDetails(userName.get(), password.get(), domain, rememberMe, loginToken);
                }
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

    /**
     * Login attempt validation check.
     *
     * @param errorKey to set
     * @param status to set
     * @param forwardedFor to set
     * @return login attempt status true/false
     * @throws Exception raise if error
     */
    private Boolean loginAttemptvalidationCheck(String errorKey, String status, String forwardedFor) throws Exception {
        Integer unlockTime = -1;
        String ipAddress = forwardedFor;
        if (ipAddress != null) {
            LoginSecurityTrack persistLoginSecurityTrack = loginSecurityTrackService.findByIpAddress(ipAddress);
            GeneralConfiguration generalConfiguration = generalConfigurationService.findByIsActive(true);

            if (persistLoginSecurityTrack == null && status.equals(CloudStackConstants.STATUS_ACTIVE)) {
                return true;
            }
            if (generalConfiguration == null) {
                throw new BadCredentialsException("error.login.credentials");
            } else if (persistLoginSecurityTrack != null && generalConfiguration.getMaxLogin() >= persistLoginSecurityTrack.getLoginAttemptCount()
                && status.equals(CloudStackConstants.STATUS_ACTIVE)) {
                persistLoginSecurityTrack.setLoginAttemptCount(0);
                persistLoginSecurityTrack.setLoginTimeStamp(null);
                loginSecurityTrackService.save(persistLoginSecurityTrack);
                return true;
            } else {
                unlockTime = loginAttemptCountCheck(unlockTime, ipAddress, persistLoginSecurityTrack, generalConfiguration, status);
                if (unlockTime == -1 && status.equals(CloudStackConstants.STATUS_INACTIVE)) {
                    throw new BadCredentialsException(errorKey);
                } else if (unlockTime != -1) {
                    throw new BadCredentialsException("Your account is locked please login after " + unlockTime + " minutes");
                }
                return true;
            }
        }
        return true;
    }

    /**
     * Check the login attempt count for security purpose.
     *
     * @param unlockTime unlock time
     * @param ipAddress ip address
     * @param status status
     * @param persistLoginSecurityTrack login security object
     * @param generalConfiguration general configuration object
     * @return login attempt count
     * @throws Exception unhandled exceptions.
     */
    private Integer loginAttemptCountCheck(Integer unlockTime, String ipAddress, LoginSecurityTrack persistLoginSecurityTrack,
            GeneralConfiguration generalConfiguration, String status) throws Exception {
        if (persistLoginSecurityTrack == null) {
            LoginSecurityTrack loginSecurityTrack = new LoginSecurityTrack();
            loginSecurityTrack.setLoginAttemptCount(1);
            loginSecurityTrack.setLoginIpAddress(ipAddress);
            loginSecurityTrack.setLoginTimeStamp(null);
            loginSecurityTrack.setIsActive(true);
            loginSecurityTrackService.save(loginSecurityTrack);
        } else if (generalConfiguration != null) {
            if (generalConfiguration.getMaxLogin() > persistLoginSecurityTrack.getLoginAttemptCount()) {
                persistLoginSecurityTrack.setLoginAttemptCount(persistLoginSecurityTrack.getLoginAttemptCount() + 1);
                persistLoginSecurityTrack.setLoginTimeStamp(null);
                loginSecurityTrackService.save(persistLoginSecurityTrack);
            } else {
                unlockTime = loginAttemptFailureState(persistLoginSecurityTrack, generalConfiguration, unlockTime, status);
            }
        }
        return unlockTime;
    }

    /**
     * Check the login attempt count for security purpose.
     *
     * @param persistLoginSecurityTrack login security object
     * @param generalConfiguration general configuration object
     * @param unlockTime unlock time
     * @param status status
     * @return login attempt count
     * @throws Exception unhandled exceptions.
     */
    private Integer loginAttemptFailureState(LoginSecurityTrack persistLoginSecurityTrack, GeneralConfiguration generalConfiguration,
            Integer unlockTime, String status) throws Exception {
        if (persistLoginSecurityTrack.getLoginTimeStamp() != null && DateConvertUtil.getTimestamp() > persistLoginSecurityTrack.getLoginTimeStamp()) {
            if (status.equals(CloudStackConstants.STATUS_ACTIVE)) {
                persistLoginSecurityTrack.setLoginAttemptCount(0);
            } else {
                persistLoginSecurityTrack.setLoginAttemptCount(1);
            }
            persistLoginSecurityTrack.setLoginTimeStamp(null);
            loginSecurityTrackService.save(persistLoginSecurityTrack);
        } else {
            persistLoginSecurityTrack.setLoginAttemptCount(persistLoginSecurityTrack.getLoginAttemptCount() + 1);
            if (persistLoginSecurityTrack.getLoginTimeStamp() == null) {
                persistLoginSecurityTrack.setLoginTimeStamp(DateConvertUtil.getTimestamp() + (generalConfiguration.getUnlockTime() * 60));
                loginSecurityTrackService.save(persistLoginSecurityTrack);
                return generalConfiguration.getUnlockTime();
            } else {
                loginSecurityTrackService.save(persistLoginSecurityTrack);
                int remainingLockedTime = (int) ((persistLoginSecurityTrack.getLoginTimeStamp() - DateConvertUtil.getTimestamp()) / 60);
                return remainingLockedTime + 1;
            }
        }
        return unlockTime;
    }

    /**
     * Check the force login status.
     *
     * @param user to set
     * @param forceLogin to set
     * @return force login status
     * @throws Exception raise if error
     */
    private Boolean forceLoginAttemptCheck(User user, String forceLogin) throws Exception {
        LoginHistory loginHistory = loginHistoryService.findByUserId(user.getId());
        Long currentTimeStamp;
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        currentTimeStamp = cal.getTimeInMillis() / 1000;
        if (loginHistory == null || !loginHistory.getIsAlreadyLogin() || loginHistory.getSessionExpireTime() < currentTimeStamp) {
            return true;
        } else if (loginHistory.getIsAlreadyLogin() && forceLogin.equals("true")) {
            return true;
        } else {
            throw new BadCredentialsException("error.already.exists");
        }
    }

}
