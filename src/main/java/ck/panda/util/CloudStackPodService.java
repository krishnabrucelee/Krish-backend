package ck.panda.util;

import java.util.HashMap;
import java.util.LinkedList;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * CloudStack Pod service for connectivity with CloudStack server.
 *
 */
@Service
public class CloudStackPodService {

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
     * Lists all Pods.
     *
     * @param optional values from cloudstack.
     * @param response josn or xml.
     * @return response from cloud stack server.
     * @throws Exception handles errors
     */
    public String listPods(String response, HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listPods", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return  responseDocument;
    }

}
