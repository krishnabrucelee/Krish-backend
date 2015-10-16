package ck.panda.util.infrastructure.security;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;

import java.util.UUID;

/**
 * Token Service.
 *
 */
public class TokenService {

    /** Logger constant. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenService.class);

    /** REST API auth token constant. */
    private static final Cache REST_API_AUTH_TOKEN = CacheManager.getInstance().getCache("restApiAuthTokenCache");

    /** Scheduler time constant. */
    public static final int HALF_AN_HOUR_IN_MILLISECONDS = 30 * 60 * 1000;

    /**
     * Evict expire tokens.
     */
    @Scheduled(fixedRate = HALF_AN_HOUR_IN_MILLISECONDS)
    public void evictExpiredTokens() {
        LOGGER.info("Evicting expired tokens");
        REST_API_AUTH_TOKEN.evictExpiredElements();
    }

    /**
     * Generate new token.
     * @return token
     */
    public String generateNewToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * Store token.
     * @param token to set
     * @param authentication to set
     */
    public void store(String token, Authentication authentication) {
        REST_API_AUTH_TOKEN.put(new Element(token, authentication));
    }

    /**
     * Check token already exists.
     * @param token to set
     * @return true/false.
     */
    public boolean contains(String token) {
        return REST_API_AUTH_TOKEN.get(token) != null;
    }

    /**
     * Get the auth token.
     * @param token to set
     * @return Authentication
     */
    public Authentication retrieve(String token) {
        return (Authentication) REST_API_AUTH_TOKEN.get(token).getObjectValue();
    }
}
