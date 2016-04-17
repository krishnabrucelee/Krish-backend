package ck.panda.util.infrastructure.externalwebservice;

import java.util.HashMap;
import org.springframework.security.core.authority.AuthorityUtils;
import ck.panda.domain.entity.Role;
import ck.panda.domain.entity.RolePrincipal;
import ck.panda.domain.entity.User;
import ck.panda.util.DateConvertUtil;
import ck.panda.util.infrastructure.AuthenticatedExternalWebService;
import ck.panda.util.infrastructure.security.AuthenticationWithToken;
import ck.panda.util.infrastructure.security.ExternalServiceAuthenticator;

/**
 * External service authenticator.
 *
 */
public class SomeExternalServiceAuthenticator implements ExternalServiceAuthenticator {

    @Override
    public AuthenticationWithToken authenticate(String userName, String roleName, Role role, User user,
            String buildVersion, String rememberMe, String loginToken) {
        ExternalWebServiceStub externalWebService = new ExternalWebServiceStub();

        // Do all authentication mechanisms required by external web service
        // protocol and validated response.
        // Throw descendant of Spring AuthenticationException in case of
        // unsucessful authentication. For example
        // BadCredentialsException

        // If authentication to external service succeeded then create
        // authenticated wrapper with proper Principal and
        // GrantedAuthorities.
        // GrantedAuthorities may come from external service authentication or
        // be hardcoded at our layer as they are
        // here with ROLE_DOMAIN_USER
        AuthenticatedExternalWebService authenticatedExternalWebService = null;
        if (role == null) {
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            try {
                hashMap.put(RolePrincipal.REMEMBER_ME, rememberMe);
                hashMap.put(RolePrincipal.LOGIN_HISTORY_TOKEN, loginToken);
                hashMap.put(RolePrincipal.LOGIN_TIME, DateConvertUtil.getTimestamp());
            } catch (Exception e) {
                e.getMessage();
            }
            authenticatedExternalWebService = new AuthenticatedExternalWebService(
                    hashMap, null,
                    AuthorityUtils.commaSeparatedStringToAuthorityList(roleName));
        } else {
            // Reduced the role principal parameter count
            authenticatedExternalWebService = new AuthenticatedExternalWebService(
                    new RolePrincipal(user, userName, role, buildVersion, rememberMe, loginToken), null,
                    AuthorityUtils.commaSeparatedStringToAuthorityList(roleName));
        }
        authenticatedExternalWebService.setExternalWebService(externalWebService);
        return authenticatedExternalWebService;
    }
}
