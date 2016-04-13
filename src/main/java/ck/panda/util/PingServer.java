package ck.panda.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * MR.ping server component to connect to usage server.
 *
 *
 */
@Component
public class PingServer {

    /** URL that connects with MR.ping. */
    private String apiURL;

    /** Default Constructor. */
    public PingServer() {

    }

    /**
     * Parameterized constructor to set MR.ping credentials.
     *
     * @param apiURL URL of the MR.ping server
     */
    public void setServer(String apiURL) {
        this.apiURL = apiURL;
    }

    /**
     * URL Mapping.All MR.ping requests are submitted in the form of a HTTP GET/POST with an associated command
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
            method = new GetMethod(apiURL);
            method.setFollowRedirects(true);
            method.setQueryString(queryValues.toArray(new NameValuePair[0]));
        } catch (Exception e) {
            throw new CloudStackException(e);
        }
        return getResponse(method);
    }

    /**
     * URL Mapping.All MR.ping requests are submitted in the form of a HTTP GET/POST with an associated command
     * and any parameters. with headers
     *
     * @param queryValues - The web services command we wish to execute, such as create a virtual machine or create a
     *            disk volume
     * @param method - The request method
     * @return final URL
     * @throws Exception if any invalid parameters.
     */
    public String requestWithMethod(LinkedList<NameValuePair> queryValues, HttpMethod method) throws Exception {
        try {
            method.setFollowRedirects(true);
            method.setQueryString(queryValues.toArray(new NameValuePair[0]));
        } catch (Exception e) {
            throw new CloudStackException(e);
        }
        return getResponse(method);
    }

    /**
     * URL Mapping.All MR.ping requests are submitted in the form of a HTTP POST with an associated command
     * and any parameters.
     *
     * @param queryValues - The web services command we wish to execute, such as create a virtual machine or create a
     *            disk volume
     * @return final URL
     * @throws Exception if any invalid parameters.
     */
    public String postRequest(String queryValues) throws Exception {
        PostMethod method = null;
        try {
            method = new PostMethod(apiURL);
            method.setRequestHeader("Accept", "application/json");
            method.setRequestHeader("Content-Type", "application/json; charset=UTF-8");
            StringRequestEntity requestEntity = new StringRequestEntity(
                    queryValues,
                    "application/json",
                    "UTF-8");
            method.setRequestEntity(requestEntity);
        } catch (Exception e) {
            throw new CloudStackException(e);
        }
        return getResponse(method);
    }

    /**
     * URL Mapping.All MR.ping requests are submitted in the form of a HTTP PUT with an associated command
     * and any parameters.
     *
     * @param queryValues - The web services command we wish to execute, such as create a virtual machine or create a
     *            disk volume
     * @return final URL
     * @throws Exception if any invalid parameters.
     */
    public String putRequest(String queryValues) throws Exception {
        PutMethod method = null;
        try {
            method = new PutMethod(apiURL);
            method.setRequestHeader("Accept", "application/json");
            method.setRequestHeader("Content-Type", "application/json; charset=UTF-8");
            StringRequestEntity requestEntity = new StringRequestEntity(
                    queryValues,
                    "application/json",
                    "UTF-8");
            method.setRequestEntity(requestEntity);
        } catch (Exception e) {
            throw new CloudStackException(e);
        }
        return getResponse(method);
    }

    /**
     * Get response from the MR.ping in our specified format also throws exception in case of error.
     *
     * @param method Passes final URL and connects it with MR.ping.
     * @return response in xml or json
     * @throws HttpException exception in URL formation.
     * @throws IOException error in signature formation
     * @throws Exception general exceptions
     */
    public String getResponse(HttpMethod method) throws HttpException, IOException, Exception {
        HttpClient client = new HttpClient();
        String response = null;
        client.executeMethod(method);
        response = method.getResponseBodyAsString();
        method.releaseConnection();
        return response;
    }

    /**
     * A web service command executed and parameters in command is compared with the MR.ping parameters.
     *
     * @param requestParam excluding mandatory fields
     * @return query values.
     */
    public LinkedList<NameValuePair> getDefaultQuery(HashMap<String, String> requestParam) {
        LinkedList<NameValuePair> queryValues = new LinkedList<NameValuePair>();
        if (requestParam != null) {
            Iterator optionalIt = requestParam.entrySet().iterator();
            while (optionalIt.hasNext()) {
                Map.Entry pairs = (Map.Entry) optionalIt.next();
                queryValues.add(new NameValuePair((String) pairs.getKey(), (String) pairs.getValue()));
            }
        }
        return queryValues;
    }

    /**
     * A web service command executed and parameters in command is compared with the MR.ping parameters.
     *
     * @param requestJson excluding mandatory fields
     * @return String values.
     */
    public String getJsonToString(JSONObject requestJson) {
        if (requestJson != null) {
           return requestJson.toString();
        }
        return "";
    }

}
