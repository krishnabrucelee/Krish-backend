package ck.panda.util;

import java.util.HashMap;
import java.util.LinkedList;

import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *  CloudStack Network Service provider for cloud Stack connectivity with network providers.
 *
 */

@Service
public class CloudStackNetworkService {

    /** Cloudstack server for connectivity. */
    @Autowired
    private CloudStackServer server;

    /**
     * setServer passes apikey, url, secretkey from UI and aids to establish
     * cloudstack connectivity.
     *
     * @param server sets apikey and url.
     */
    public void setServer(CloudStackServer server) {
        this.server = server;
    }


    /**
     * Lists supported network services from cloud stack.
     *
     * @param optional from values cloud stack
     * @param response json or xml.
     * @return response Document
     * @throws Exception unhandled errors.
     */
    public String listSupportedNetworkServices(String response,
            HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listSupportedNetworkServices", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);

        return  responseDocument;
    }


    /**
     * Lists network service providers for cloud stack.
     *
     * @param optional from values cloud stack
     * @param response json or xml.
     * @return response Document
     * @throws Exception unhandled errors.
     */
    public String listNetworkServiceProviders(String response,
            HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listNetworkServiceProviders", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);

        return responseDocument;
    }

}
