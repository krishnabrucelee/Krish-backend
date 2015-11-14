package ck.panda.util;

import java.util.HashMap;
import java.util.LinkedList;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Cloud stack ISO service.
 */
@Service
public class CSIsoService {

    @Autowired
    private CloudStackServer server;

    public CSIsoService(CloudStackServer server) {
        this.server = server;
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

        return server.request(arguments);
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

        return server.request(arguments);
    }

    /**
     * Lists all available ISO files.
     *
     * @param optional
     * @return
     * @throws Exception
     */
    public String listIsos(String response, HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("listIsos", optional);
        arguments.add(new NameValuePair("response", response));

        return server.request(arguments);
    }

}
