package ck.panda.util;

import java.util.HashMap;
import java.util.LinkedList;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * CloudStack iso service for connectivity with CloudStack server.
 */
@Service
public class CloudStackIsoService {

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
     * Lists all available ISO files.
     *
     * @param optional values from cloudstack server.
     * @return response document.
     * @param response json or xml
     * @throws Exception unhandled errors.
     */
    public String listIsos(String response, HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listIsos", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return  responseDocument;
    }
}
