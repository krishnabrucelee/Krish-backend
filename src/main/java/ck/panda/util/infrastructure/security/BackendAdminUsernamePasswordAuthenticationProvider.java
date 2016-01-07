package ck.panda.util.infrastructure.security;

import com.google.common.base.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;

/**
 * Backend admin username password authentication provider.
 *
 */
public class BackendAdminUsernamePasswordAuthenticationProvider implements AuthenticationProvider {

    /** Invalid credentials constant. */
    public static final String INVALID_BACKEND_ADMIN_CREDENTIALS = "Invalid Backend Admin Credentials";

    /** Admin username. */
    @Value("${backend.admin.username}")
    private String backendAdminUsername;

    /** Admin password. */
    @Value("${backend.admin.password}")
    private String backendAdminPassword;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Optional<String> username = (Optional) authentication.getPrincipal();
        Optional<String> password = (Optional) authentication.getCredentials();

        if (credentialsMissing(username, password) || credentialsInvalid(username, password)) {
            throw new BadCredentialsException(INVALID_BACKEND_ADMIN_CREDENTIALS);
        }

        return new UsernamePasswordAuthenticationToken(username.get(), null,
                AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_BACKEND_ADMIN"));
    }

    /**
     * Credentials missing.
     * 
     * @param username to set
     * @param password to set.
     * @return true/false
     */
    private boolean credentialsMissing(Optional<String> username, Optional<String> password) {
        return !username.isPresent() || !password.isPresent();
    }

    /**
     * Invalid credentials.
     * 
     * @param username to set
     * @param password to set
     * @return true/false
     */
    private boolean credentialsInvalid(Optional<String> username, Optional<String> password) {
        return !isBackendAdmin(username.get()) || !password.get().equals(backendAdminPassword);
    }

    /**
     * Is backend admin.
     * 
     * @param username to set
     * @return true/false
     */
    private boolean isBackendAdmin(String username) {
        return backendAdminUsername.equals(username);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(BackendAdminUsernamePasswordAuthenticationToken.class);
    }
}
