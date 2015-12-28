package ck.panda.util.infrastructure.externalwebservice;

import org.springframework.security.core.authority.AuthorityUtils;
import ck.panda.domain.entity.Role;
import ck.panda.domain.entity.RolePrincipal;
import ck.panda.domain.entity.User;
import ck.panda.util.infrastructure.AuthenticatedExternalWebService;
import ck.panda.util.infrastructure.security.AuthenticationWithToken;
import ck.panda.util.infrastructure.security.ExternalServiceAuthenticator;

/**
 * External service authenticator.
 *
 */
public class SomeExternalServiceAuthenticator implements ExternalServiceAuthenticator {

    @Override
    public AuthenticationWithToken authenticate(String username, String rolename, Role role, User user, String buildVersion) {
        ExternalWebServiceStub externalWebService = new ExternalWebServiceStub();

        // Do all authentication mechanisms required by external web service protocol and validated response.
        // Throw descendant of Spring AuthenticationException in case of unsucessful authentication. For example BadCredentialsException

        // ...
        // ...

        // If authentication to external service succeeded then create authenticated wrapper with proper Principal and GrantedAuthorities.
        // GrantedAuthorities may come from external service authentication or be hardcoded at our layer as they are here with ROLE_DOMAIN_USER
        AuthenticatedExternalWebService authenticatedExternalWebService = null;
        if (role == null) {
            authenticatedExternalWebService = new AuthenticatedExternalWebService(username, null,
                AuthorityUtils.commaSeparatedStringToAuthorityList(rolename));
        } else {
            authenticatedExternalWebService = new AuthenticatedExternalWebService(new RolePrincipal(username, role, user.getType(), user.getDomain().getName(), user.getDomain().getId(), user.getDepartment().getId(), buildVersion), null,
                AuthorityUtils.commaSeparatedStringToAuthorityList(rolename));
        }
        authenticatedExternalWebService.setExternalWebService(externalWebService);
        return authenticatedExternalWebService;
    }
}
