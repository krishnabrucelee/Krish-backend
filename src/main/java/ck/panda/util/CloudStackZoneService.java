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
public class CloudStackZoneService {

    /** CloudStack server for connectivity. */
    @Autowired
    private CloudStackServer server;

    /**
     * Set values in CloudStack server.
     * @param server setting apikey, secretkey, URL in cloudStack server.
     */
    public void setServer(CloudStackServer server) {
        this.server = server;
    }


   /* *//**
     * Creates a Zone.
     *
     * @param dns1 the first DNS for the Zone
     * @param internalDns1 the first internal DNS for the Zone
     * @param name the name of the Zone
     * @param networkType network type of the zone, can be Basic or Advanced
     * @param optional
     * @return
     * @throws Exception
     *//*
    public String createZone(String dns1, String internalDns1, String name,
            String networkType, HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("createZone", optional);
        arguments.add(new NameValuePair("dns1", dns1));
        arguments.add(new NameValuePair("internaldns1", internalDns1));
        arguments.add(new NameValuePair("name", name));
        arguments.add(new NameValuePair("networktype", networkType));

        String responseDocument = server.request(arguments);

        return responseDocument;
    }

    *//**
     * Updates a Zone.
     *
     * @param zoneId the ID of the Zone
     * @param optional
     * @return
     * @throws Exception
     *//*
    public String updateZone(String zoneId,String response,HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("updateZone", optional);
        arguments.add(new NameValuePair("id", zoneId));
        arguments.add(new NameValuePair("response",response));
        String responseDocument = server.request(arguments);

        return  (responseDocument);
    }

    *//**
     * Deletes a Zone.
     *
     * @param zoneId the ID of the Zone
     * @param optional
     * @return
     * @throws Exception
     *//*
    public String deleteZone(String zoneId)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("deleteZone", null);
        arguments.add(new NameValuePair("id", zoneId));

     String responseDocument = server.request(arguments);

        return responseDocument;
    }
*/

    /**
     * Lists zones.
     *
     * @param optional values from cloudstack.
     * @return response document.
     * @throws Exception unhandled errors.
     */
    public String listZones(HashMap<String, String> optional, String response) throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listZones", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);

        return  responseDocument;
    }

    /**
     * Dedicates a Zone.
     *
     * @param domainId the ID of the containing domain
     * @param zoneId the ID of the zone
     * @param optional
     * @return
     * @throws Exception
     *//*
    public DedicateZoneResponse dedicateZone(String domainId,
            String zoneId, HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("dedicateZone", optional);
        arguments.add(new NameValuePair("domainid", domainId));
        arguments.add(new NameValuePair("zoneid", zoneId));

        Document responseDocument = server.makeRequest(arguments);

        return (DedicateZoneResponse) (responseDocument);
    }


    *//**
     * Release dedication of zone.
     *
     * @param zoneId the ID of the Zone
     * @return
     * @throws Exception
     *//*
    public ReleaseDedicatedZoneResponse releaseDedicatedZone(String zoneId)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("releaseDedicatedZone", null);
        arguments.add(new NameValuePair("id", zoneId));

        Document responseDocument = server.makeRequest(arguments);

        return (ReleaseDedicatedZoneResponse) (responseDocument);
    }



    *//**
     * List dedicated zones.
     *
     * @param optional
     * @return
     * @throws Exception
     *//*
    public ListDedicatedZonesResponse listDedicatedZones(HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listDedicatedZones", optional);

        Document responseDocument = server.makeRequest(arguments);

        return (ListDedicatedZonesResponse) (responseDocument);
    }
*/

}
