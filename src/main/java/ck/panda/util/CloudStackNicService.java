package ck.panda.util;

import java.util.HashMap;
import java.util.LinkedList;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * CloudStack Nic service acts as Interface Virtual Machine and network in CloudStack server.
 *
 */
@Service
public class CloudStackNicService {

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
     * Assigns secondary IP to NIC
     *
     * @param nicid of the nic.
     * @param optional values to CS.
     * @param response json or xml response.
     * @return nic.
     * @throws Exception if error occurs.
     */
    public String addIpToNic(String nicid, String response, HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("addIpToNic", optional);
        arguments.add(new NameValuePair("nicid", nicid));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Remove secondary IP to NIC.
     *
     * @param secondaryIpId of the nic.
     * @param response json or xml response.
     * @return nic.
     * @throws Exception if error occurs.
     */
    public String removeIpFromNic(String secondaryIpId, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("removeIpFromNic", null);
        arguments.add(new NameValuePair("id", secondaryIpId));
        arguments.add(new NameValuePair("response", response));

        String responseDocument = server.request(arguments);
        return  responseDocument;
    }

    /**
     * list the VM NICS IP to NIC
     *
     * @param virtualMachineId
     * @param optional
     * @return
     * @throws Exception
     */
    public String listNics(HashMap<String, String> optional, String response) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("listNics", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);

        return responseDocument;
    }

    /**
     * Retrieves the current status of asynchronous job for Acquire Ip address rules.
     *
     * @param asychronousJobid the ID of the asychronous job
     * @param response json or xml.
     * @return response.
     * @throws Exception if error occurs.
     */
    public String AcquireIpJobResult(String asychronousJobid, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("queryAsyncJobResult", null);
        arguments.add(new NameValuePair("jobid", asychronousJobid));
         arguments.add(new NameValuePair("response",response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

}
