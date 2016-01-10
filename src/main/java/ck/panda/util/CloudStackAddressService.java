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
     * Disables static rule for given ip address.
     *
     * @param ipAddressId ipaddress id.
     * @return disable static nat.
     * @throws Exception unhandled errors.
     */
    public String  disableStaticNat(String ipAddressId)
            throws Exception {
        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("disableStaticNat", null);
        arguments.add(new NameValuePair("ipaddressid", ipAddressId));
        arguments.add(new NameValuePair("response", "json"));
        return server.request(arguments);
    }

    /**
     * Enables static nat for given ip address.
     *
     * @param ipAddressId the public IP address id for which static nat feature
     *                    is being enabled
     * @param virtualMachineId the ID of the virtual machine for enabling static
     *        nat feature
     * @param optional additional parameters
     * @return enable static nat.
     * @throws Exception unhandled errors.
     */
    public String enableStaticNat(String ipAddressId, String virtualMachineId, HashMap<String, String> optional)
            throws Exception {
        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("enableStaticNat", optional);
        arguments.add(new NameValuePair("ipaddressid", ipAddressId));
        arguments.add(new NameValuePair("virtualmachineid", virtualMachineId));
        arguments.add(new NameValuePair("response", "json"));
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

    /**
     * Retrieves the current status of asynchronous job for Ip address rules.
     *
     * @param asychronousJobid the ID of the asychronous job
     * @param response json or xml.
     * @return response.
     * @throws Exception if error occurs.
     */
    public String associatedJobResult(String asychronousJobid, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("queryAsyncJobResult", null);
        arguments.add(new NameValuePair("jobid", asychronousJobid));
         arguments.add(new NameValuePair("response",response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }
}
