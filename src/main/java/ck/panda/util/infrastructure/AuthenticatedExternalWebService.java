package ck.panda.util.infrastructure;

import org.springframework.security.core.GrantedAuthority;

import ck.panda.util.infrastructure.externalwebservice.ExternalWebServiceStub;
import ck.panda.util.infrastructure.security.AuthenticationWithToken;

import java.util.Collection;

/**
 * Authenticated external web service.
 *
 */
@SuppressWarnings("serial")
public class AuthenticatedExternalWebService extends AuthenticationWithToken {

    /** External web service stub attribute. */
    private ExternalWebServiceStub externalWebService;

    /**
     * Parameterized constructor.
     *
     * @param aPrincipal to set
     * @param aCredentials to set
     * @param anAuthorities to set
     */
    public AuthenticatedExternalWebService(Object aPrincipal, Object aCredentials, Collection<? extends GrantedAuthority> anAuthorities) {
        super(aPrincipal, aCredentials, anAuthorities);
    }

    /**
     * Set external service.
     * @param externalWebService to set
     */
    public void setExternalWebService(ExternalWebServiceStub externalWebService) {
        this.externalWebService = externalWebService;
    }

    /**
     * Get external web service.
     * @return external webservice
     */
    public ExternalWebServiceStub getExternalWebService() {
        return externalWebService;
    }
}
