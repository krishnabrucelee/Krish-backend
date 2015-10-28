package ck.panda.util;

import java.util.HashMap;
import java.util.LinkedList;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 *
 */
@Service
public class CloudStackNetworkService {

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
     * Creates a network
     *
     * @param networkOfferingDisplayText the display text of the network
     * @param networkOfferingName the name of the network
     * @param networkOfferingId the network offering id
     * @param networkOfferingZoneId the Zone ID for the network
     * @param optional
     * @return
     * @throws Exception
     */
    public String createNetwork(String networkOfferingDisplayText,
            String networkOfferingName, String response,
            HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("createNetwork", optional);
        arguments.add(new NameValuePair("displaytext", networkOfferingDisplayText));
        arguments.add(new NameValuePair("name", networkOfferingName));
        arguments.add(new NameValuePair("response",response));
        String responseDocument = server.request(arguments);

        return responseDocument;
    }


    /**
     * Lists all available networks.
     *
     * @param optional
     * @return
     * @throws Exception
     */
    public String listNetworks( String response,HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listNetworks", optional);
        arguments.add(new NameValuePair("response",response));
        String responseDocument = server.request(arguments);

        return responseDocument;
    }

}
