package ck.panda.util.infrastructure.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.Collection;

/**
 * Authentication with token.
 *
 */
@SuppressWarnings("serial")
public class AuthenticationWithToken extends PreAuthenticatedAuthenticationToken {

    /**
     * Parameterized constructor.
     * 
     * @param aPrincipal to set
     * @param aCredentials to set
     */
    public AuthenticationWithToken(Object aPrincipal, Object aCredentials) {
        super(aPrincipal, aCredentials);
    }

    /**
     * Parameterized constructor.
     * 
     * @param aPrincipal to set
     * @param aCredentials to set
     * @param anAuthorities to set
     */
    public AuthenticationWithToken(Object aPrincipal, Object aCredentials,
            Collection<? extends GrantedAuthority> anAuthorities) {
        super(aPrincipal, aCredentials, anAuthorities);
    }

    /**
     * Set token.
     * 
     * @param token to set
     */
    public void setToken(String token) {
        setDetails(token);
    }

    /**
     * Get token.
     * 
     * @return token
     */
    public String getToken() {
        return (String) getDetails();
    }
}
