package ck.panda.util.infrastructure.security;

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
     * @return token
     */
    AuthenticationWithToken authenticate(String username, String rolename);
}
