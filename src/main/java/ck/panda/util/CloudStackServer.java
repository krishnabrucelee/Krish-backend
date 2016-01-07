package ck.panda.util;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

/**
 * Cloudstack server component to connect to cloudstack server.
 *
 * It requires cloudstack credentials to be set.
 *
 */
@Component
public class CloudStackServer {

    /** secret key for authentication. */
    private String secret;

    /** api key unique for an account.This should have been generated by the administrator of the cloud instance. */
    private String apikey;

    /** URL that connects with cloudstack. */
    private String apiURL;

    /** Logger attribute. */
    private static final Log LOGER = LogFactory.getLog(CloudStackServer.class);

    /** Default Constructor. */
    public CloudStackServer() {

    }

    /**
     * Parameterized constructor to set cloudstack credentials.
     *
     * @param apiURL URL of the CloudStack server
     * @param secret secret key combination with HMAC SHA1 to generate command String signature.
     * @param apikey which uniquely identifies the account.
     */
    public void setServer(String apiURL, String secret, String apikey) {
        this.apiURL = apiURL;
        this.secret = secret;
        this.apikey = apikey;
    }

    /**
     * URL Mapping.All CloudStack API requests are submitted in the form of a HTTP GET/POST with an associated command
     * and any parameters.
     *
     * @param queryValues - The web services command we wish to execute, such as create a virtual machine or create a
     *            disk volume
     * @return final URL
     * @throws Exception if any invalid parameters.
     */
    public String request(LinkedList<NameValuePair> queryValues) throws Exception {
        HttpMethod method = null;

        try {
            String querySignature = signRequest(queryValues);
            queryValues.add(new NameValuePair("signature", querySignature));
            method = new GetMethod(apiURL);
            method.setFollowRedirects(true);
            method.setQueryString(queryValues.toArray(new NameValuePair[0]));
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw new CloudStackException(e);
        }
        return getResponse(method);
    }

    /**
     * URL Mapping.All CloudStack API requests are submitted in the form of a HTTP GET/POST with an associated command
     * and any parameters.
     *
     * @param queryValues - The web services command we wish to execute, such as create a virtual machine or create a
     *            disk volume
     * @return final URL
     * @throws Exception if any invalid parameters.
     */
    public String requestLogin(LinkedList<NameValuePair> queryValues) throws Exception {
        HttpMethod method = null;

        try {
            String querySignature = signRequestLogin(queryValues);
            queryValues.add(new NameValuePair("signature", querySignature));
            method = new PostMethod(apiURL);
            method.setQueryString(queryValues.toArray(new NameValuePair[0]));
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw new CloudStackException(e);
        }
        return getResponse(method);
    }

    /**
     * Get response from the CloudStack in our specified format also throws exception in case of error.
     *
     * @param method Passes final URL and connects it with cloudstack.
     * @return response in xml or json
     * @throws HttpException exception in URL formation.
     * @throws IOException error in signature formation
     * @throws Exception general exceptions
     */
    public String getResponse(HttpMethod method) throws HttpException, IOException, Exception {
        HttpClient client = new HttpClient();

        LOGER.debug(method.getQueryString());
        LOGER.debug(method.getPath());

        String response = null;
        client.executeMethod(method);

        LOGER.debug(method.getResponseBodyAsString());

        response = method.getResponseBodyAsString();

        method.releaseConnection();
        return response;
    }

    /**
     * Whether you access the CloudStack API with HTTP or HTTPS, it must still be signed so that CloudStack can verify
     * the caller has been authenticated and authorized to execute the command.
     * 
     * @param queryValues web service commands like create service offering
     * @return value for request.
     * @throws java.security.NoSuchAlgorithmException if tried to execute different algorithm except HMAC SHA1
     * @throws java.security.InvalidKeyException if secret or api key is invalid.
     */
    private String signRequest(LinkedList<NameValuePair> queryValues)
            throws java.security.NoSuchAlgorithmException, java.security.InvalidKeyException {
        Collections.sort(queryValues, new Comparator<NameValuePair>() {
            public int compare(NameValuePair o1, NameValuePair o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        String queryString = EncodingUtil
                .formUrlEncode(queryValues.toArray(new NameValuePair[queryValues.size()]), "UTF-8").replace("+", "%20")
                .replace("%5B", "[").replace("%5D", "]");
        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA1");
        mac.init(secretKey);
        byte[] digest = mac.doFinal(queryString.toLowerCase().getBytes());

        return DatatypeConverter.printBase64Binary(digest);
    }

    /**
     * Whether you access the CloudStack API with HTTP or HTTPS, it must still be signed so that CloudStack can verify
     * the caller has been authenticated and authorized to execute the command.
     * 
     * @param queryValues web service commands like create service offering
     * @return value for request.
     * @throws java.security.NoSuchAlgorithmException if tried to execute different algorithm except HMAC SHA1
     * @throws java.security.InvalidKeyException if secret or api key is invalid.
     */
    private String signRequestLogin(LinkedList<NameValuePair> queryValues)
            throws java.security.NoSuchAlgorithmException, java.security.InvalidKeyException {
        Collections.sort(queryValues, new Comparator<NameValuePair>() {
            public int compare(NameValuePair o1, NameValuePair o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        String queryString = EncodingUtil
                .formUrlEncode(queryValues.toArray(new NameValuePair[queryValues.size()]), "UTF-8").replace("+", "%20")
                .replace("%5B", "[").replace("%5D", "]");
        byte[] digest = queryString.toLowerCase().getBytes();

        return DatatypeConverter.printBase64Binary(digest);
    }

    /**
     * A web service command executed and parameters in command is compared with the cloudstack parameters.
     * 
     * @param command web service eg. Create an instance
     * @param optional excluding mandatory fields
     * @return query values.
     */
    public LinkedList<NameValuePair> getDefaultQuery(String command, HashMap<String, String> optional) {
        LinkedList<NameValuePair> queryValues = new LinkedList<NameValuePair>();
        queryValues.add(new NameValuePair("command", command));
        queryValues.add(new NameValuePair("apiKey", apikey));
        if (optional != null) {
            Iterator optionalIt = optional.entrySet().iterator();
            while (optionalIt.hasNext()) {
                Map.Entry pairs = (Map.Entry) optionalIt.next();
                queryValues.add(new NameValuePair((String) pairs.getKey(), (String) pairs.getValue()));
            }
        }
        return queryValues;
    }

    /**
     * A web service command executed and parameters in command is compared with the cloudstack parameters for login.
     * 
     * @param command web service eg. Create an instance
     * @param optional excluding mandatory fields
     * @return query values.
     */
    public LinkedList<NameValuePair> getDefaultQueryLogin(String command, HashMap<String, String> optional) {
        LinkedList<NameValuePair> queryValues = new LinkedList<NameValuePair>();
        queryValues.add(new NameValuePair("command", command));
        if (optional != null) {
            Iterator optionalIt = optional.entrySet().iterator();
            while (optionalIt.hasNext()) {
                Map.Entry pairs = (Map.Entry) optionalIt.next();
                queryValues.add(new NameValuePair((String) pairs.getKey(), (String) pairs.getValue()));
            }
        }
        return queryValues;
    }

    /**
     * CloudStackServer exception is called when unhandled exception is thrown.
     */
    public class CloudStackServerException extends Exception {

        /**
         * serialVersionUID is a universal version identifier for a Serializable class.
         */
        private static final long serialVersionUID = 1L;

        /**
         * CloudStackServer exception throws error message with error code.
         *
         * @param errorcode such as 401 User invalid credentials.
         * @param errortext such as Unauthorized user
         */
        CloudStackServerException(String errorcode, String errortext) {
            super(errorcode + ": " + errortext);
        }
    }
}
