package ck.panda.util;

import java.util.HashMap;
import java.util.LinkedList;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * CloudStack Region Service for cloud Stack connectivity with Region.
 */
@Service
public class CloudStackAffinityGroupService {

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
     * Creates an affinity/anti-affinity group.
     *
     * @param affinityGroupName name of the affinity group
     * @param affinityGroupType Type of the affinity group from the available affinity/anti-affinity group types
     * @param response json.
     * @param optional value
     * @return response String json
     * @throws Exception raise if error
     */
    public String createAffinityGroup(String affinityGroupName,
            String affinityGroupType, String response, HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("createAffinityGroup", optional);
        arguments.add(new NameValuePair("name", affinityGroupName));
        arguments.add(new NameValuePair("type", affinityGroupType));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }


    /**
     * Deletes affinity group.
     *
     * @param optional value
     * @param response json.
     * @return response String json
     * @throws Exception raise if error
     */
    public String deleteAffinityGroup(String response, HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("deleteAffinityGroup", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Lists affinity groups.
     *
     * @param response json.
     * @param optional value
     * @return response String json
     * @throws Exception raise if error
     */
    public String listAffinityGroups(String response,
            HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listAffinityGroups", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }


    /**
     * Updates the affinity/anti-affinity group associations of a virtual machine.
     *
     * @param virtualMachineId The ID of the virtual machine
     * @param response json.
     * @param optional value
     * @return response String json
     * @throws Exception raise if error
     */
    public String updateVMAffinityGroup(String virtualMachineId, String response,
            HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("updateVMAffinityGroup", optional);
        arguments.add(new NameValuePair("id", virtualMachineId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Lists affinity group types available.
     *
     * @param response json.
     * @param optional value
     * @return response String json
     * @throws Exception raise if error
     */
    public String listAffinityGroupTypes(String response,
            HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listAffinityGroupTypes", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Event status of affinity group.
     *
     * @param asychronousJobid job id
     * @param response response type.
     * @return json response.
     * @throws Exception unhandled exceptions.
     */
    public String queryAsyncJobResult(String asychronousJobid, String response) throws Exception {
        LinkedList<NameValuePair> arguments = server.getDefaultQuery("queryAsyncJobResult", null);
        arguments.add(new NameValuePair("jobid", asychronousJobid));
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }

}
