package ck.panda.util;

import java.util.HashMap;
import java.util.LinkedList;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * CloudStack Compute offering service for cloudStack server connectivity with compute offers.
 */
@Service
public class CloudStackComputeOffering {

    /** Cloudstack server for connectivity. */
    @Autowired
    private CloudStackServer server;

    /**
     * Sets api key , secret key and url.
     *
     * @param server sets these values.
     */
    public void setServer(CloudStackServer server) {
        this.server = server;
    }

    /**
     * Create service offering.
     *
     * @param name of the offer
     * @param displayText description for the offer
     * @param response json or xml response
     * @param optional values from cloustack server.
     * @return response Document
     * @throws Exception unhandled errors.
     */
    public String createComputeOffering(String name, String displayText, String response,
            HashMap<String, String> optional) throws Exception {

        System.err.println("optional" + optional);

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("createServiceOffering", optional);
        arguments.add(new NameValuePair("displaytext", displayText));
        arguments.add(new NameValuePair("name", name));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Deletes a service offering.
     * 
     * @param serviceOfferingId to delete offer
     * @param response json or xml
     * @return response Document
     * @throws Exception unhandled errors.
     */
    public String deleteComputeOffering(String serviceOfferingId, String response) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("deleteServiceOffering", null);
        arguments.add(new NameValuePair("id", serviceOfferingId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Update service offering.
     *
     * @param serviceOfferingId id of the service to be updated.
     * @param name of the service offering
     * @param displayText description of the service offering
     * @param response json or xml
     * @param optional from cloudstack
     * @return response Document
     * @throws Exception unhandled errors.
     */
    public String updateComputeOffering(String serviceOfferingId, String name, String displayText, String response,
            HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("updateServiceOffering", optional);
        arguments.add(new NameValuePair("id", serviceOfferingId));
        arguments.add(new NameValuePair("name", name));
        arguments.add(new NameValuePair("displaytext", displayText));
        arguments.add(new NameValuePair("response", response));

        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Lists all available service offerings.
     *
     * @param response json or xml
     * @param optional from cloudstack
     * @return response Document.
     * @throws Exception unhandled errors.
     */
    public String listComputeOfferings(String response, HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("listServiceOfferings", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }
}
