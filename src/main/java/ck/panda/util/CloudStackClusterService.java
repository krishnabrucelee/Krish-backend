package ck.panda.util;

import java.util.HashMap;
import java.util.LinkedList;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * CloudStack cluster service for connectivity with CloudStack server.
 *
 */
@Service
public class CloudStackClusterService {

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
     * Lists clusters.
     *
     * @param optional values from cloud stack
     * @param response xml or json response
     * @return response from cloudstack server
     * @throws Exception unhandled errors.
     */
    public String listClusters(String response, HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("listClusters", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }
}
