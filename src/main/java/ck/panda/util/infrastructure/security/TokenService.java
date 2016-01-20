package ck.panda.util.infrastructure.security;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import ck.panda.domain.entity.User;
import ck.panda.util.DateConvertUtil;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

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
    public static final int HALF_AN_HOUR_IN_MILLISECONDS = 4 * 60 * 60 * 1000;

    /** Secret key value is append. */
    @Value(value = "${aes.salt.secretKey}")
    private String secretKey;

    /** Admin username. */
    @Value("${backend.admin.userid}")
    private String backendAdminUserid;

    /** Admin username. */
    @Value("${backend.admin.username}")
    private String backendAdminUsername;

    /** Admin role. */
    @Value("${backend.admin.dominid}")
    private String backendAdminDomainId;

    /** Admin role. */
    @Value("${backend.admin.departmentid}")
    private String backendAdminDepartmentId;

    /** Admin role. */
    @Value("${backend.admin.role}")
    private String backendAdminRole;

    /** Admin type. */
    @Value("${backend.admin.type}")
    private String backendAdminType;

    /** API key. */
    @Value("${backend.admin.userapikey}")
    private String userApiKey;

    /** Secret key. */
    @Value("${backend.admin.usersecretkey}")
    private String userSecretKey;

    /** Build Version. */
    @Value("${app.buildversion}")
    private String buildNumber;

    /**
     * Evict expire tokens.
     */
    @Scheduled(fixedRate = HALF_AN_HOUR_IN_MILLISECONDS)
    public void evictExpiredTokens() {
        LOGGER.info("Evicting expired tokens");
        REST_API_AUTH_TOKEN.evictExpiredElements();
    }

    /**
     * @param user token.
     * @param domainName name of the domain
     * @return token
     * @throws Exception raise if error
     */
    public String generateNewToken(User user, String domainName) throws Exception {
        String encryptedToken = null;
        try {
            String strEncoded = Base64.getEncoder().encodeToString(secretKey.getBytes("utf-8"));
            byte[] decodedKey = Base64.getDecoder().decode(strEncoded);
            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
            encryptedToken = new String(
                    ck.panda.util.EncryptionUtil.encrypt(createTokenDetails(user, domainName).toString(), originalKey));
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("ERROR AT TOKEN GENERATION", e);
        }
        return encryptedToken;
    }

    /**
     * Store token.
     *
     * @param token to set
     * @param authentication to set
     */
    public void store(String token, Authentication authentication) {
        REST_API_AUTH_TOKEN.put(new Element(token, authentication));
    }

    /**
     * Check token already exists.
     *
     * @param token to set
     * @return true/false.
     */
    public boolean contains(String token) {
        return REST_API_AUTH_TOKEN.get(token) != null;
    }

    /**
     * Get the auth token.
     *
     * @param token to set
     * @return Authentication
     * @throws Exception raise if error
     */
    public Authentication retrieve(String token) throws Exception {
        return (Authentication) REST_API_AUTH_TOKEN.get(token).getObjectValue();
    }

    /**
     * @param user details for token generation
     * @param domainName name of the domain
     * @return user details
     */
    public StringBuilder createTokenDetails(User user, String domainName) {
        StringBuilder stringBuilder = null;
        try {
            stringBuilder = new StringBuilder();
            stringBuilder.append(user == null ? backendAdminUserid : user.getId()).append("@@");
            stringBuilder.append(user == null ? backendAdminUsername : user.getUserName()).append("@@");
            stringBuilder.append(user == null ? backendAdminDomainId : user.getDomain().getId()).append("@@");
            stringBuilder.append(user == null ? backendAdminDepartmentId : user.getDepartment().getId()).append("@@");
            stringBuilder.append(user == null ? backendAdminRole : user.getRole().getName()).append("@@");
            stringBuilder.append(domainName).append("@@");
            stringBuilder.append(user == null ? backendAdminType : user.getType()).append("@@");
            stringBuilder.append(user == null ? userApiKey : user.getApiKey()).append("@@");
            stringBuilder.append(user == null ? userSecretKey : user.getSecretKey()).append("@@");
            stringBuilder.append(DateConvertUtil.getTimestamp());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder;
    }

}
