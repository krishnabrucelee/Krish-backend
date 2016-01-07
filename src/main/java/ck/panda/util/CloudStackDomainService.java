package ck.panda.util;

import java.util.HashMap;
import java.util.LinkedList;

import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * CloudStack Domain service for cloudStack server connectivity with domain.
 *
 */
@Service
public class CloudStackDomainService {

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
     * Creates a domain
     *
     * @param domainName creates domain with this name
     * @param optional
     * @return
     * @throws Exception
     */
    public String createDomain(String domainName, String response, HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("createDomain", optional);
        arguments.add(new NameValuePair("name", domainName));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Updates a domain with a new name
     *
     * @param domainId The ID of domain to update
     * @param optional
     * @return
     * @throws Exception
     */
    public String updateDomain(String domainId, String response, HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("updateDomain", optional);
        arguments.add(new NameValuePair("id", domainId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Deletes a specified domain
     *
     * @param domainId The ID of domain to delete
     * @return
     * @throws Exception
     */
    public String deleteDomain(String domainId, String response) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("deleteDomain", null);
        arguments.add(new NameValuePair("id", domainId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Lists domains and provides detailed information for listed domains.
     *
     * @param optional values from cloudstack.
     * @param response json or xml
     * @return response Document.
     * @throws Exception unhandled errors.
     */
    public String listDomains(String response, HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("listDomains", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Deleted status of domain.
     *
     * @param asychronousJobid job id
     * @param response response type.
     * @return json response.
     * @throws Exception unhandled exceptions.
     */
    public String queryAsyncJobResult(String asychronousJobid, String response) throws Exception {
        LinkedList<NameValuePair> arguments = server.getDefaultQuery("queryAsyncJobResult", null);
        arguments.add(new NameValuePair("jobid", asychronousJobid));
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }
}
