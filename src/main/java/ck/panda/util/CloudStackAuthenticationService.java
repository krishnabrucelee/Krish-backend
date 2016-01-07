package ck.panda.util;

import java.util.HashMap;
import java.util.LinkedList;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Krishna <krishnakumar@assistanz.com>
 */
@Service
public class CloudStackAuthenticationService {

    /** Cloudstack server for connectivity. */
    @Autowired
    private CloudStackServer server;

    /**
     * sets api key , secret key and url.
     * 
     * @param server sets these values.
     */
    public void setServer(CloudStackServer server) {
        this.server = server;
    }

    /**
     * Logs a user into the CloudStack.
     *
     * @param userName - authentication user name
     * @param password - authentication password
     * @param response - Response format as json
     * @param optional - List of optional values
     * @return - Json string response
     * @throws Exception - Raise if any error
     */
    public String login(String userName, String password, String response, HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQueryLogin("login", optional);
        arguments.add(new NameValuePair("username", userName));
        arguments.add(new NameValuePair("password", password));
        arguments.add(new NameValuePair("response", response));
        String responseJson = server.requestLogin(arguments);
        return responseJson;
    }

    /**
     * Logs out the user.
     *
     * @param response - Response format as json
     * @param optional - List of optional values
     * @return - Json string response
     * @throws Exception - Raise if any error
     */
    public String logout(String response, HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("logout", optional);
        arguments.add(new NameValuePair("response", response));
        String responseJson = server.request(arguments);
        return responseJson;
    }

    /**
     * SP initiated SAML Single Sign On.
     *
     * @param idpUrl - SSO HTTP-Redirect binding URL
     * @param response - Response format as json
     * @param optional - List of optional values
     * @return - Json string response
     * @throws Exception - Raise if any error
     */
    public String samlSso(String idpUrl, String response, HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("samlSso", optional);
        arguments.add(new NameValuePair("idpUrl", idpUrl));
        arguments.add(new NameValuePair("response", response));
        String responseJson = server.request(arguments);
        return responseJson;
    }

    /**
     * SAML Global Log Out API.
     *
     * @param response - Response format as json
     * @param optional - List of optional values
     * @return - Json string response
     * @throws Exception - Raise if any error
     */
    public String samlSlo(String response, HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("samlSlo", optional);
        arguments.add(new NameValuePair("response", response));
        String responseJson = server.request(arguments);
        return responseJson;
    }

    /**
     * SAML Global Log Out API.
     *
     * @param response - Response format as json
     * @param optional - List of optional values
     * @return - Json string response
     * @throws Exception - Raise if any error
     */
    public String getSpMetadata(String response, HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("getSpMetadata", optional);
        arguments.add(new NameValuePair("response", response));
        String responseJson = server.request(arguments);
        return responseJson;
    }
}
