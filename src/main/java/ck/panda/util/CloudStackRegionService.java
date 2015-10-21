package ck.panda.util;

import java.util.HashMap;
import java.util.LinkedList;

import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * CloudStack Region Service for cloud Stack connectivity with Region.
 *
 */
@Service
public class CloudStackRegionService {

    @Autowired
    private CloudStackServer server;

    public void setServer(CloudStackServer server) {
        this.server = server;
    }

   /* *//**
     * Adds a Region
     *
     * @param regionId Id of the Region
     * @param regionEndPoint Region service endpoint
     * @param regionName Name of the region
     * @return
     * @throws Exception
     *//*
    public AddRegionResponse addRegion(String regionId, String regionEndPoint,
            String regionName, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("addRegion", null);
        arguments.add(new NameValuePair("id", regionId));
        arguments.add(new NameValuePair("endpoint", regionEndPoint));
        arguments.add(new NameValuePair("name", regionName));
        arguments.add(new NameValuePair("response", response));

        Document responseDocument = server.makeRequest(arguments);

        return (AddRegionResponse) responseDocument;
    }

    *//**
     * Updates a region
     *
     * @param regionId Id of the Region
     * @param optional
     * @return
     * @throws Exception
     *//*
    public UpdateRegionResponse updateRegion(String regionId, String response, HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("updateRegion", optional);
        arguments.add(new NameValuePair("id", regionId));
        arguments.add(new NameValuePair("response", response));

        Document responseDocument = server.makeRequest(arguments);

        return (UpdateRegionResponse) responseDocument;
    }

    *//**
     * Removes specified region
     *
     * @param regionId ID of the region to delete
     * @return
     * @throws Exception
     *//*
    public RemoveRegionResponse removeRegion(String regionId, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("removeRegion", null);
        arguments.add(new NameValuePair("id", regionId));
        arguments.add(new NameValuePair("response", response));

        Document responseDocument = server.makeRequest(arguments);

        return (RemoveRegionResponse) responseDocument;

    }
*/
    /**
     * Lists Regions
     *
     * @param optional
     * @return
     * @throws Exception
     */
    public String listRegions(String response, HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listRegions", optional);
        arguments.add(new NameValuePair("response", response));

        String responseDocument = server.request(arguments);

        return responseDocument;
    }

}
