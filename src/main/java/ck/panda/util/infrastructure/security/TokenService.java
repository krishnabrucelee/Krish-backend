package ck.panda.util.infrastructure.security;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.User;
import ck.panda.util.DateConvertUtil;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;

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
    public static final int FOUR_HOUR_IN_MILLISECONDS = 4 * 60 * 60 * 1000;

    /** Secret key value is append. */
    @Value(value = "${aes.salt.secretKey}")
    private String secretKey;

    /** Admin user id. */
    @Value("${backend.admin.userid}")
    private String backendAdminUserId;

    /** Admin user name. */
    @Value("${backend.admin.username}")
    private String backendAdminUserName;

    /** Admin domain id. */
    @Value("${backend.admin.dominid}")
    private String backendAdminDomainId;

    /** Admin department id. */
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

    /**
     * Generate new encrypted token using user login details.
     *
     * @param user token.
     * @param domainName name of the domain
     * @return token
     * @throws Exception unhandled exceptions.
     */
    public String generateNewToken(User user, String domainName) throws Exception {
        String encryptedToken = null;
        try {
            String strEncoded = Base64.getEncoder()
                    .encodeToString(secretKey.getBytes(GenericConstants.CHARACTER_ENCODING));
            byte[] decodedKey = Base64.getDecoder().decode(strEncoded);
            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length,
                    GenericConstants.ENCRYPT_ALGORITHM);
            encryptedToken = new String(
                    ck.panda.util.EncryptionUtil.encrypt(createTokenDetails(user, domainName).toString(), originalKey));
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Error at token generation : ", e);
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
     * Get the authentication token.
     *
     * @param token to set
     * @return Authentication status object
     * @throws Exception unhandled exceptions.
     */
    public Authentication retrieve(String token) throws Exception {
        CacheConfiguration config = REST_API_AUTH_TOKEN.getCacheConfiguration();

        // Sets the time to idle for an element before it expires. This property can be modified dynamically while the
        // cache is operating.
        config.setTimeToIdleSeconds(FOUR_HOUR_IN_MILLISECONDS);
        return (Authentication) REST_API_AUTH_TOKEN.get(token).getObjectValue();
    }

    /**
     * Generate the new token with user login details.
     *
     * @param user details for token generation
     * @param domainName name of the domain
     * @return user details
     */
    public StringBuilder createTokenDetails(User user, String domainName) {
        StringBuilder stringBuilder = null;
        try {
            stringBuilder = new StringBuilder();
            if (user == null) {
                stringBuilder.append(backendAdminUserId).append(GenericConstants.TOKEN_SEPARATOR);
                stringBuilder.append(backendAdminUserName).append(GenericConstants.TOKEN_SEPARATOR);
                stringBuilder.append(backendAdminDomainId).append(GenericConstants.TOKEN_SEPARATOR);
                stringBuilder.append(backendAdminDepartmentId).append(GenericConstants.TOKEN_SEPARATOR);
                stringBuilder.append(backendAdminRole).append(GenericConstants.TOKEN_SEPARATOR);
                stringBuilder.append(domainName).append(GenericConstants.TOKEN_SEPARATOR);
                stringBuilder.append(backendAdminType).append(GenericConstants.TOKEN_SEPARATOR);
                stringBuilder.append(userApiKey).append(GenericConstants.TOKEN_SEPARATOR);
                stringBuilder.append(userSecretKey).append(GenericConstants.TOKEN_SEPARATOR);
            } else {
                stringBuilder.append(user.getId()).append(GenericConstants.TOKEN_SEPARATOR);
                stringBuilder.append(user.getUserName()).append(GenericConstants.TOKEN_SEPARATOR);
                stringBuilder.append(user.getDomain().getId()).append(GenericConstants.TOKEN_SEPARATOR);
                stringBuilder.append(user.getDepartment().getId()).append(GenericConstants.TOKEN_SEPARATOR);
                stringBuilder.append(user.getRole().getName()).append(GenericConstants.TOKEN_SEPARATOR);
                stringBuilder.append(domainName).append(GenericConstants.TOKEN_SEPARATOR);
                stringBuilder.append(user.getType()).append(GenericConstants.TOKEN_SEPARATOR);
                stringBuilder.append(user.getApiKey()).append(GenericConstants.TOKEN_SEPARATOR);
                stringBuilder.append(user.getSecretKey()).append(GenericConstants.TOKEN_SEPARATOR);
            }
            stringBuilder.append(DateConvertUtil.getTimestamp());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder;
    }
}
