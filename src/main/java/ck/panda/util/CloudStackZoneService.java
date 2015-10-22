package ck.panda.util;

import java.util.HashMap;
import java.util.LinkedList;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * CloudStack Zone service for cloudStack connectivity with the Cloud Stack
 * server.
 *
 */
@Service
public class CloudStackZoneService {

    /** CloudStack server for connectivity. */
    @Autowired
    private CloudStackServer server;

    /**
     * Set values in CloudStack server.
     *
     * @param server
     *            setting apikey, secretkey, URL in cloudStack server.
     */
    public void setServer(CloudStackServer server) {
        this.server = server;
    }

    /**
     * Lists zones.
     *
     * @param optional
     *            values from cloudstack.
     * @param response
     *            json or xml
     * @return response document.
     * @throws Exception
     *             unhandled errors.
     */
    public String listZones(HashMap<String, String> optional, String response) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("listZones", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

}
