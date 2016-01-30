package ck.panda.util.infrastructure.security;

import ck.panda.domain.entity.Role;
import ck.panda.domain.entity.User;

/**
 * External Service Authenticator.
 *
 */
public interface ExternalServiceAuthenticator {
    /**
     * Authenticate.
     *
     * @param userName to set
     * @param roleName to set
     * @param role to set
     * @param user to set
     * @param buildNumber to set.
     * @return authenticated token
     */
    AuthenticationWithToken authenticate(String userName, String roleName, Role role, User user, String buildNumber);
}
