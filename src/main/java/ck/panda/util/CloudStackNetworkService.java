package ck.panda.util;

import java.util.HashMap;
import java.util.LinkedList;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * CloudStack Network Service provider for cloud Stack connectivity with network providers.
 *
 */

@Service
public class CloudStackNetworkService {

    /** Cloudstack server for connectivity. */
    @Autowired
    private CloudStackServer server;

    /**
     * setServer passes apikey, url, secretkey from UI and aids to establish cloudstack connectivity.
     *
     * @param server sets apikey and url.
     */
    public void setServer(CloudStackServer server) {
        this.server = server;
    }

    /**
     * Creates a network.
     *
     * @param zoneId the Zone ID for the network
     * @param optional optional
     * @param response response
     * @return zoneId zone
     * @return
     * @throws Exception exception
     */
    public String createNetwork(String zoneId, String response, HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("createNetwork", optional);
        arguments.add(new NameValuePair("zoneid", zoneId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Lists network service providers for cloud stack.
     *
     * @param optional from values cloud stack
     * @param response json or xml.
     * @return response Document
     * @throws Exception unhandled errors.
     */
    public String listNetworkServiceProviders(String response, HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("listNetworkServiceProviders", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Lists all available networks.
     *
     * @param response response
     * @param optional optional
     * @return response Document
     * @throws Exception exception
     */
    public String listNetworks(String response, HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("listNetworks", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Deletes a network.
     *
     * @param networkId the ID of the network
     * @param response response
     * @throws Exception exception
     * @return response
     */
    public String deleteNetwork(String networkId, String response) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("deleteNetwork", null);
        arguments.add(new NameValuePair("id", networkId));
        arguments.add(new NameValuePair("response", response));

        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Updates a network.
     *
     * @param networkId the ID of the network
     * @param optional optional
     * @param response response
     * @return response
     * @throws Exception exception
     */
    public String updateNetwork(String networkId, HashMap<String, String> optional, String response) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("updateNetwork", optional);
        arguments.add(new NameValuePair("id", networkId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Retrieves the current status of asynchronous job for network.
     *
     * @param asychronousJobid the ID of the asychronous job
     * @param response json
     * @return job response
     * @throws Exception error
     */
    public String networkJobResult(String asychronousJobid, String response) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("queryAsyncJobResult", null);
        arguments.add(new NameValuePair("jobid", asychronousJobid));
        arguments.add(new NameValuePair("response", response));
        String jobResponse = server.request(arguments);
        return jobResponse;
    }

    /**
     * Restarts the network; includes 1) restarting network elements - virtual routers, dhcp servers 2) reapplying all.
     * public ips 3) reapplying loadBalancing/portForwarding rules
     *
     * @param networkId the network id to restart
     * @param optional values mapping to ACS.
     * @param response response
     * @return network.
     * @throws Exception if error occurs.
     */
    public String restartNetwork(String networkId, HashMap<String, String> optional, String response) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("restartNetwork", optional);
        arguments.add(new NameValuePair("id", networkId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }
}
