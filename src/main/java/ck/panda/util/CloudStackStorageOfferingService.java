/**
 *
 */
package ck.panda.util;

import java.util.HashMap;
import java.util.LinkedList;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * CloudStack Storage Offering service for cloudStack connectivity with the Cloud Stack server.
 *
 */
@Service
public class CloudStackStorageOfferingService {

    /** CloudStack server for connectivity. */
    @Autowired
    private CloudStackServer server;

    /**
     * Set values in CloudStack server.
     *
     * @param server setting apikey, secretkey, URL in cloudStack server.
     */
    public void setServer(CloudStackServer server) {
        this.server = server;
    }

    /**
     * Creating Storage offering in cloud stack.
     *
     * @param displayText description of the storage offering.
     * @param name storage offering name.
     * @param response JSON response.
     * @param optional optional values for storage offering.
     * @return created response.
     * @throws Exception if error.
     */
    public String createStorageOffering(String response, HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("createDiskOffering", optional);
        arguments.add(new NameValuePair("response", response));

        String createResponse = server.request(arguments);

        return createResponse;
    }

    /**
     * Updating Storage offering in cloud stack.
     *
     * @param storageOfferingId storage offering id.
     * @param response JSON response.
     * @param optional optional values for storage offering.
     * @return updated response.
     * @throws Exception if error.
     */
    public String updateStorageOffering(String storageOfferingId, String response, HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("updateDiskOffering", optional);
        arguments.add(new NameValuePair("id", storageOfferingId));
        arguments.add(new NameValuePair("response", response));

        String updateResponse = server.request(arguments);

        return updateResponse;
    }

    /**
     * Deleting Storage offering in cloud stack.
     *
     * @param srotageOfferingId storage offering id.
     * @param response JSON response.
     * @return deleted response.
     * @throws Exception if error.
     */
    public String deleteStorageOffering(String srotageOfferingId, String response) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("deleteDiskOffering", null);
        arguments.add(new NameValuePair("id", srotageOfferingId));
        arguments.add(new NameValuePair("response", response));

        String deleteResponse = server.request(arguments);

        return deleteResponse;
    }

    /**
     * Listing Storage offering from cloud stack.
     *
     * @param response JSON response.
     * @param optional optional values for storage offering.
     * @return list response.
     * @throws Exception if error.
     */
    public String listStorageOfferings(String response, HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("listDiskOfferings", optional);
        arguments.add(new NameValuePair("response", response));

        String listResponse = server.request(arguments);

        return listResponse;
    }

}
