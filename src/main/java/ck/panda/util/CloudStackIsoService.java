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

    /**
     * sets api key , secret key and url.
     * 
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
    public String listIsos(String response, HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("listIsos", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Attaches an ISO to a virtual machine.
     *
     * @param isoId the ID of the ISO file
     * @param virtualMachineId the ID of the virtual machine
     * @return
     * @throws Exception
     */
    public String attachIso(String isoId, String virtualMachineId, String response) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("attachIso", null);
        arguments.add(new NameValuePair("id", isoId));
        arguments.add(new NameValuePair("virtualmachineid", virtualMachineId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Detaches any ISO file (if any) currently attached to a virtual machine.
     *
     * @param virtualMachineId The ID of the virtual machine
     * @return
     * @throws Exception
     */
    public String detachIso(String virtualMachineId, String response) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("detachIso", null);
        arguments.add(new NameValuePair("virtualmachineid", virtualMachineId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

}
