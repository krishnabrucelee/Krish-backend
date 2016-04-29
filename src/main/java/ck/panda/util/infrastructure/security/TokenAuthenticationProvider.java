package ck.panda.util.infrastructure.security;

import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import com.google.common.base.Optional;
import ck.panda.constants.CloudStackConstants;
import ck.panda.domain.entity.GeneralConfiguration;
import ck.panda.domain.entity.LoginHistory;
import ck.panda.service.GeneralConfigurationService;
import ck.panda.service.LoginHistoryService;
import ck.panda.service.RoleService;
import ck.panda.service.UserService;

/**
 * Token authentication provider.
 *
 */
public class TokenAuthenticationProvider implements AuthenticationProvider {

    /** Logger constant. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenAuthenticationProvider.class);

    /** Token service attribute. */
    private TokenService tokenService;

    /** Login history service attribute. */
    private LoginHistoryService loginHistoryService;

    /** General Configuration service attribute. */
    private GeneralConfigurationService generalConfigurationService;

    /** External service authenticator reference. */
    private ExternalServiceAuthenticator externalServiceAuthenticator;

    /** User service attribute. */
    private UserService userService;

    /** Role service reference. */
    private RoleService roleService;

    /** Error session expired. */
    public static final String ERROR_SESSION_EXPIRED = "error.session.expired";

    /** User id. */
    public static final String USER_ID = "userId";

    /** Login token. */
    public static final String LOGIN_TOKEN = "loginToken";

    /** Undefined value. */
    public static final String UNDEFINED = "undefined";

    /** Time zone. */
    public static final String UTC = "UTC";

    /** Negative value. */
    public static final String NEGATIVE_VALUE = "-1";

    /** Null value check. */
    public static final String NULL = "null";

    /**
     * Parameterized constructor.
     *
     * @param tokenService to set
     * @param loginHistoryService to set
     * @param generalConfigurationService to set
     * @param externalServiceAuthenticator to set
     * @param userService to set
     * @param roleService to set
     */
    public TokenAuthenticationProvider(TokenService tokenService, LoginHistoryService loginHistoryService, GeneralConfigurationService generalConfigurationService,
            ExternalServiceAuthenticator externalServiceAuthenticator, UserService userService, RoleService roleService) {
        this.tokenService = tokenService;
        this.loginHistoryService = loginHistoryService;
        this.generalConfigurationService = generalConfigurationService;
        this.externalServiceAuthenticator = externalServiceAuthenticator;
        this.userService = userService;
        this.roleService = roleService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Optional<String> token = (Optional) authentication.getPrincipal();
        HashMap<String, String> getLoginToken = (HashMap<String, String>) authentication.getDetails();
        if (!token.isPresent() || token.get().isEmpty()) {
            throw new BadCredentialsException(ERROR_SESSION_EXPIRED);
        }
        if (!tokenService.contains(token.get())) {
            throw new BadCredentialsException(ERROR_SESSION_EXPIRED);
        }
        if (getLoginToken.get(USER_ID) != null && !getLoginToken.get(USER_ID).equals(UNDEFINED)
                && !getLoginToken.get(USER_ID).equals(NULL)) {
            String loginToken = getLoginToken.get(LOGIN_TOKEN);
            String userId = getLoginToken.get(USER_ID);
            LoginHistory loginHistory = loginHistoryService.findByUserIdAndAlreadyLogin(Long.valueOf(userId), true);
            Long currentTimeStamp;
            if (loginHistory != null) {
            try {
                Calendar cal = Calendar.getInstance();
                cal.setTimeZone(TimeZone.getTimeZone(UTC));
                currentTimeStamp = cal.getTimeInMillis() / 1000;
                if (currentTimeStamp > loginHistory.getRememberMeExpireDate()) {
                    loginHistory.setRememberMe(CloudStackConstants.STATUS_INACTIVE);
                    loginHistoryService.save(loginHistory);
                }
                if (!loginToken.equals(NEGATIVE_VALUE)) {
                    GeneralConfiguration generalConfiguration = generalConfigurationService.findByIsActive(true);
                    Long sessionExpirtyTime = currentTimeStamp + (generalConfiguration.getSessionTime() * 60);
                    if (loginHistory.getSessionExpireTime() > currentTimeStamp || loginHistory.getRememberMe().equals(CloudStackConstants.STATUS_ACTIVE)) {
                        loginHistory.setSessionExpireTime(sessionExpirtyTime);
                        loginHistoryService.save(loginHistory);
                    } else if (loginHistory.getRememberMe().equals(CloudStackConstants.STATUS_INACTIVE)) {
                        loginHistory.setIsAlreadyLogin(false);
                        loginHistory = loginHistoryService.save(loginHistory);
                        tokenService.evictExpiredTokens();
                        throw new BadCredentialsException(ERROR_SESSION_EXPIRED);
                    }
                }
            } catch (Exception e1) {
                throw new BadCredentialsException(ERROR_SESSION_EXPIRED);
            }
            }
        }

        try {
            authentication = tokenService.retrieve(token.get());
        } catch (Exception e) {
            LOGGER.error("Error at token authentication");
        }
        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(PreAuthenticatedAuthenticationToken.class);
    }
}
