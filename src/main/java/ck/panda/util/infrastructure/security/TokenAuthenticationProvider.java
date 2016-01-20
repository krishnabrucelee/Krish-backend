package ck.panda.util.infrastructure.security;

import com.google.common.base.Optional;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

/**
 * Token authentication provider.
 *
 */
public class TokenAuthenticationProvider implements AuthenticationProvider {

    /** Token service attribute. */
    private TokenService tokenService;

    /**
     * Parameterized constructor.
     *
     * @param tokenService to set
     */
    public TokenAuthenticationProvider(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Optional<String> token = (Optional) authentication.getPrincipal();
        if (!token.isPresent() || token.get().isEmpty()) {
            throw new BadCredentialsException("INVALID_TOKEN");
        }
        if (!tokenService.contains(token.get())) {
            throw new BadCredentialsException("INVALID_TOKEN");
        }
        try {
            authentication = tokenService.retrieve(token.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(PreAuthenticatedAuthenticationToken.class);
    }
}
