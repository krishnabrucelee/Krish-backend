package ck.panda.util;

import java.util.HashMap;
import java.util.LinkedList;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ck.panda.service.ResourceLimitDomainService;

/**
 * CloudStack resource capacity service for cloudStack connectivity with the Cloud Stack server.
 *
 */
@Service
public class CloudStackResourceCapacity {

    /** CloudStack server for connectivity. */
    @Autowired
    private CloudStackServer server;

    /** Resource Limit Domain Service. */
    @Autowired
    private ResourceLimitDomainService resourceLimitDomainService;

    /**
     * Set values in CloudStack server.
     *
     * @param server setting apikey, secretkey, URL in cloudStack server.
     */
    public void setServer(CloudStackServer server) {
        this.server = server;
    }

    /**
     * Lists of resources capacity.
     *
     * @param optional values from cloudstack.
     * @param response json or xml
     * @return response json string.
     * @throws Exception unhandled errors.
     */
    public String listCapacity(HashMap<String, String> optional, String response) throws Exception {
        LinkedList<NameValuePair> arguments = server.getDefaultQuery("listCapacity", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Update resource count.
     *
     * @param optional values from cloudstack.
     * @param response json or xml
     * @return response json string.
     * @throws Exception unhandled errors.
     */
    public String updateResourceCount(String  domainId, HashMap<String, String> optional, String response) throws Exception {
        LinkedList<NameValuePair> arguments = server.getDefaultQuery("updateResourceCount", optional);
        arguments.add(new NameValuePair("domainid", domainId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Lists of resources max limit.
     *
     * @param optional values from cloudstack.
     * @param response json or xml
     * @return response json string.
     * @throws Exception unhandled errors.
     */
    public String listResourceLimits(HashMap<String, String> optional, String response) throws Exception {
        LinkedList<NameValuePair> arguments = server.getDefaultQuery("listResourceLimits", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Lists of public ip addresses.
     *
     * @param optional values from cloudstack.
     * @param response json or xml
     * @return response json string.
     * @throws Exception unhandled errors.
     */
    public String listPublicIpAddress(HashMap<String, String> optional, String response) throws Exception {
        LinkedList<NameValuePair> arguments = server.getDefaultQuery("listPublicIpAddresses", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }
}
