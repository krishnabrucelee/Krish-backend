package ck.panda.util;

import java.util.HashMap;
import java.util.LinkedList;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
*
* CloudStack ipaddress service for connectivity with CloudStack server.
*/
@Service
public class CloudStackAddressService {


    /** Cloudstack server for connectivity. */
    @Autowired
    private CloudStackServer server;

    /**
     * Sets api key , secret key and url.
     *
     * @param server sets these values.
     */
    public void setServer(CloudStackServer server) {
        this.server = server;
    }

    /**
     * Acquires and associates a public IP to an account.
     *
     * @param optional parameters.
     * @param response json/xml.
     * @return response string.
     * @throws Exception unhandled excpetions.
     */
    public String associateIpAddress(String response, HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("associateIpAddress", optional);
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }

    /**
     * Disassociates an ip address from the account.
     *
     * @param publicIpAddressId the id of the public ip address to disassociate.
     * @param response json/xml.
     * @return response.
     * @throws Exception unhandled excpetions.
     */
    public String disassociateIpAddress(String publicIpAddressId, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("disassociateIpAddress", null);
        arguments.add(new NameValuePair("id", publicIpAddressId));
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }

    /**
     * Lists all public ip addresses.
     *
     * @param optional parameters.
     * @param response json/xml.
     * @return response.
     * @throws Exception unhandled excpetions.
     */
    public String listPublicIpAddresses(String response, HashMap<String, String> optional)
            throws Exception {
        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listPublicIpAddresses", optional);
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }
}
