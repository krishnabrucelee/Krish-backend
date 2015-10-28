/**
 *
 */
package ck.panda.util;

import java.util.HashMap;
import java.util.LinkedList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ck.panda.domain.entity.StorageOffering;

/**
 * CloudStack Storage Offering service for cloudStack connectivity with the
 * Cloud Stack server.
 *
 */
@Service
public class CloudStackStorageOfferingService {

    /** CloudStack server for connectivity. */
    @Autowired
    private CloudStackServer server;

    private StorageOffering storage;

    /**
     * Set values in CloudStack server.
     *
     * @param server
     *            setting apikey, secretkey, URL in cloudStack server.
     */
    public void setServer(CloudStackServer server) {
        this.server = server;
    }

    public String createStorageOffering(String displayText, String name, String response,
            HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("createDiskOffering", optional);
        arguments.add(new NameValuePair("displaytext", displayText));
        arguments.add(new NameValuePair("name", name));
        arguments.add(new NameValuePair("response", response));

        String resp = server.request(arguments);

        return resp;
    }

    public String updateStorageOffering(String diskOfferingId, String response,
              HashMap<String, String> optional) throws Exception {

            LinkedList<NameValuePair> arguments = server.getDefaultQuery("updateDiskOffering", optional);
            arguments.add(new NameValuePair("id", diskOfferingId));
            arguments.add(new NameValuePair("response", response));

            String resp = server.request(arguments);

            return resp;
          }

      public String deleteStorageOffering(String diskOfferingId, String response) throws Exception {

            LinkedList<NameValuePair> arguments = server.getDefaultQuery("deleteDiskOffering", null);
            arguments.add(new NameValuePair("id", diskOfferingId));
            arguments.add(new NameValuePair("response", response));

            String responseDocument = server.request(arguments);

            return responseDocument;
          }

      public String listStorageOfferings(String response,
                HashMap<String, String> optional) throws Exception {

            LinkedList<NameValuePair> arguments
                    = server.getDefaultQuery("listDiskOfferings", optional);
            arguments.add(new NameValuePair("response", response));

            String responseDocument = server.request(arguments);

            return responseDocument;
        }
}
