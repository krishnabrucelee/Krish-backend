package ck.panda.util;

import java.util.HashMap;
import java.util.LinkedList;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * CloudStack Hypervisor service for connectivity with CloudStack server.
 *
 */

@Service
public class CloudStackHypervisorsService {

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
     * List Hypervisor.
     *
     * @param optional values from cloudstack
     * @param response json or xml
     * @return response Document
     * @throws Exception unhandled errors.
     */
    public String listHypervisors(String response,
            HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listHypervisors", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return  responseDocument;
    }
}
