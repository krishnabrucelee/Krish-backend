package ck.panda.util;

import java.util.HashMap;
import java.util.LinkedList;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *  CloudStack Host service for connectivity with CloudStack server.
 *
 */
@Service
public class CloudStackHostService {

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
     * Find hosts suitable for migrating a virtual machine.
     *
     * @param virtualMachineid find hosts to which this VM can be migrated and flag the hosts with enough CPU/RAM to
     * host the VM.
     * @param response json or xml
     * @return response
     * @throws Exception unhandled errors.
     */
    public String findHostsForMigration(String virtualMachineid, String response) throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("findHostsForMigration", null);
        arguments.add(new NameValuePair("virtualmachineid", virtualMachineid));
        arguments.add(new NameValuePair("response",response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Lists hosts.
     *
     * @param optional values from cloudstack.
     * @param response json or xml
     * @return response
     * @throws Exception unhandled errors
     */
    public String listHosts(String response, HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listHosts", optional);
        arguments.add(new NameValuePair("response",response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }
}
