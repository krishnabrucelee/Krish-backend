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
     * @param username to set
     * @param rolename to set
     * @param role to set
     * @param user to set
     * @param buildNumber build number.
     * @return token
     */
    AuthenticationWithToken authenticate(String username, String rolename, Role role, User user, String buildNumber);
}
