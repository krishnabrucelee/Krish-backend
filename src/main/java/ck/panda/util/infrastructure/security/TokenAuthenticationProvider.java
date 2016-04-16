package ck.panda.util.infrastructure.security;

import com.google.common.base.Optional;

import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.GeneralConfiguration;
import ck.panda.domain.entity.LoginHistory;
import ck.panda.domain.entity.Role;
import ck.panda.domain.entity.User;
import ck.panda.service.DepartmentService;
import ck.panda.service.GeneralConfigurationService;
import ck.panda.service.LoginHistoryService;
import ck.panda.service.RoleService;
import ck.panda.service.UserService;
import ck.panda.util.DateConvertUtil;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.Calendar;
import java.util.Date;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

/**
 * Token authentication provider.
 *
 */
public class TokenAuthenticationProvider implements AuthenticationProvider {

    /** Logger constant. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenAuthenticationProvider.class);

    /** Token service attribute. */
    private TokenService tokenService;

    /** Token service attribute. */
    private LoginHistoryService loginHistoryService;

    /** General Configuration service attribute. */
    private GeneralConfigurationService generalConfigurationService;

    /** External service authenticator reference. */
    private ExternalServiceAuthenticator externalServiceAuthenticator;

    /** User service attribute. */
    private UserService userService;

    /** Role service reference. */
    private RoleService roleService;

    /** Build Version. */
    @Value("${app.buildversion}")
    private String buildNumber;

    /**
     * Parameterized constructor.
     *
     * @param tokenService to set
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
        if (!token.isPresent() || token.get().isEmpty()) {
            throw new BadCredentialsException("error.session.expired");
        }

        HashMap<String, String> getLoginToken = (HashMap<String, String>) authentication.getDetails();
        String loginToken = getLoginToken.get("loginToken");
        String userId = getLoginToken.get("userId");
        LoginHistory alreadyLoginDetail = loginHistoryService.findByUserIdAndAlreadyLogin(Long.valueOf(userId),
                true);
        GeneralConfiguration generalConfigurations;
        try {
            generalConfigurations = generalConfigurationService.findByIsActive(true);
            Calendar cal = Calendar.getInstance();
            cal.setTimeZone(TimeZone.getTimeZone("UTC"));
            cal.add(Calendar.DATE, generalConfigurations.getRememberMeExpiredDays());
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if (!tokenService.contains(token.get()) || !alreadyLoginDetail.getLoginToken().equals(loginToken)) {
            try {
                GeneralConfiguration generalConfiguration = generalConfigurationService.findByIsActive(true);

                Calendar cal = Calendar.getInstance();
                cal.setTimeZone(TimeZone.getTimeZone("UTC"));
                cal.add(Calendar.DATE, generalConfiguration.getRememberMeExpiredDays());
                Long expireTimeStamp = cal.getTimeInMillis() / 1000;
                if (!alreadyLoginDetail.getLoginToken().equals(loginToken)) {
                    throw new BadCredentialsException("error.session.expired");
                } else if (alreadyLoginDetail.getRememberMe().equals("true") && alreadyLoginDetail
                        .getRememberMeExpireDate() > DateConvertUtil.getTimestamp()) {
                    AuthenticationWithToken resultOfAuthentication = null;
                    User user = userService.find(alreadyLoginDetail.getUserId());
                    Role role = roleService.findWithPermissionsByNameDepartmentAndIsActive(user.getRole().getName(),
                            user.getDepartment().getId(), true);
                    resultOfAuthentication = externalServiceAuthenticator.authenticate(user.getUserName(),
                            user.getRole().getName(), role, user, buildNumber, alreadyLoginDetail.getRememberMe(),
                            loginToken);
                    resultOfAuthentication.setToken(authentication.getPrincipal().toString());
                    tokenService.store(authentication.getPrincipal().toString(), resultOfAuthentication);
                    alreadyLoginDetail.setRememberMeExpireDate(expireTimeStamp);
                    loginHistoryService.save(alreadyLoginDetail);
                } else {
                    throw new BadCredentialsException("error.session.expired");
                }
            } catch (Exception e) {
                throw new BadCredentialsException("error.session.expired");
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
