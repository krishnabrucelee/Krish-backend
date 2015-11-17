package ck.panda.util.infrastructure.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
            resultOfAuthentication = externalServiceAuthenticator.authenticate(username.get(), user.getRole().getName());
            String newToken = null;
            try {
                newToken = tokenService.generateNewToken(user);
            } catch (Exception e) {
                LOGGER.error("Error to generating token :" + e);
            }
            resultOfAuthentication.setToken(newToken);
            tokenService.store(newToken, resultOfAuthentication);
        }
        return resultOfAuthentication;
    }
}
