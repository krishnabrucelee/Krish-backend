package ck.panda.util;

import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import ck.panda.service.EncryptionUtil;

/**
 * Read the token details from the authentication.
 *
 */
@Component
public class TokenDetails {

    /** Secret key value is append. */
    @Value(value = "${aes.salt.secretKey}")
    private String secretKey;

    /**
     * @param key to set
     * @return token details
     * @throws Exception raise if error
     */
    public String getTokenDetails(String key) throws Exception {
        Authentication token = SecurityContextHolder.getContext().getAuthentication();
        String strEncoded = Base64.getEncoder().encodeToString(secretKey.getBytes("utf-8"));
        byte[] decodedKey = Base64.getDecoder().decode(strEncoded);
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        String decriptText = new String(EncryptionUtil.decrypt(token.getDetails().toString(), originalKey));
        String[] splitToken = decriptText.split("@@");
        String responseString;
        switch (key) {
        case "id":
            responseString = splitToken[0];
            break;
        case "username":
            responseString = splitToken[1];
            break;
        case "domainid":
            responseString = splitToken[2];
            break;
        case "departmentid":
            responseString = splitToken[3];
            break;
        case "rolename":
            responseString = splitToken[4];
            break;
        default:
            responseString = "";
        }
        return responseString;
    }
}
