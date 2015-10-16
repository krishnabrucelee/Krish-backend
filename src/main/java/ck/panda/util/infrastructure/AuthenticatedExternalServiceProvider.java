package ck.panda.util.infrastructure;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * External authentication provider.
 *
 */
@Component
public class AuthenticatedExternalServiceProvider {

    /**
     * Provide auth web service.
     * @return AuthenticatedExternalWebService
     */
    public AuthenticatedExternalWebService provide() {
        return (AuthenticatedExternalWebService) SecurityContextHolder.getContext().getAuthentication();
    }
}
