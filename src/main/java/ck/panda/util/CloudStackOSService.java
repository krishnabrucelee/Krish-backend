package ck.panda.util;

import java.util.HashMap;
import java.util.LinkedList;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * CloudStack OS service for providing Operating System from cloud stack.
 *
 */

@Service
public class CloudStackOSService {

    /** Cloudstack server for connectivity. */
    @Autowired
    private CloudStackServer server;

    /** sets api key , secret key and url.
     * @param server sets these values.
     */
    public void setServer(CloudStackServer server) {
        this.server = server;
    }

    /**
     * Lists all supported OS types for this cloud.
     *
     * @param optional values from cloudstack.
     * @return response document.
     * @throws Exception unhandled errors.
     * @param response json or xml response.
     */
    public String listOsTypes(String response, HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listOsTypes", optional);
        arguments.add(new NameValuePair("response", response));

       String responseDocument = server.request(arguments);

        return  responseDocument;
    }

    /**
     * Lists all supported OS categories for this cloud.
     *
     * @param optional vales from cloud stack.
     * @param response json or xml response.
     * @return response Document.
     * @throws Exception unhandled errors.
     */
    public String listOsCategories(String response, HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listOsCategories", optional);
        arguments.add(new NameValuePair("response", response));

        String responseDocument = server.request(arguments);

        return responseDocument;
    }
}

