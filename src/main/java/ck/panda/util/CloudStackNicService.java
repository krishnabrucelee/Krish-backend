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

    /** sets api key , secret key and url.
     * @param server sets these values.
     */
    public void setServer(CloudStackServer server) {
        this.server = server;
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

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listNics", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);

        return responseDocument;
    }

   

   }
