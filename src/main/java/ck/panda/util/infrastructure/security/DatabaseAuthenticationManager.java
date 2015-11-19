package ck.panda.util.infrastructure.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import com.google.common.base.Optional;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.User.Type;
import ck.panda.service.UserService;

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

    /** Admin username. */
    @Value("${backend.admin.username}")
    private String backendAdminUsername;

    /** Admin password. */
    @Value("${backend.admin.password}")
    private String backendAdminPassword;

    /** Admin role. */
    @Value("${backend.admin.role}")
    private String backendAdminRole;

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
    public AuthenticationWithToken authValidation(Optional<String> username, Optional<String> password, Optional<String> domain, AuthenticationWithToken resultOfAuthentication) throws AuthenticationException {

        User user = null;
        if (domain.get().equals("BACKEND_ADMIN")) {
            if (username.get().equals(backendAdminUsername) && password.get().equals(backendAdminPassword)) {
                resultOfAuthentication = externalServiceAuthenticator.authenticate(backendAdminUsername, backendAdminRole);
                String newToken = null;
                try {
                    newToken = tokenService.generateNewToken(user);
                } catch (Exception e) {
                    LOGGER.error("Error to generating token :" + e);
                }
                resultOfAuthentication.setToken(newToken);
                tokenService.store(newToken, resultOfAuthentication);
            } else {
                throw new BadCredentialsException("Invalid Login Credentials");
            }
        } else {
            try {
                user = userService.findByUser(username, password);
            } catch (Exception e) {
                LOGGER.error("Invalid Login Credentials : " + e);
            }

            if (user == null) {
                throw new BadCredentialsException("Invalid Login Credentials");
            } else if (!domain.get().equals("BACKEND_ADMIN") && !user.getDomain().getName().equals(domain.get().trim())) {
                throw new LockedException("Invalid Domain Address");
            } else if (domain.get().equals("BACKEND_ADMIN") && user.getType() != Type.ROOT_ADMIN) {
                throw new LockedException("Unauthorized Admin Details");
            } else if (user != null && !user.getIsActive()) {
                throw new DisabledException("Account is Inactive. Please Contact Admin");
            } else {
                if (user.getRole() != null) {
                    backendAdminRole = user.getRole().getName();
                }
                resultOfAuthentication = externalServiceAuthenticator.authenticate(username.get(), backendAdminRole);
                String newToken = null;
                try {
                    newToken = tokenService.generateNewToken(user);
                } catch (Exception e) {
                    LOGGER.error("Error to generating token :" + e);
                }
                resultOfAuthentication.setToken(newToken);
                tokenService.store(newToken, resultOfAuthentication);
            }
        }
        return resultOfAuthentication;
    }
}
