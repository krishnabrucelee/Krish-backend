package ck.panda.util;

import java.util.HashMap;
import java.util.LinkedList;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Cloud stack Resource limit connector.
 *
 */
@Service
public class CloudStackResourceLimitService {

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
     * Updates resource limits for an account or domain.
     *
     * @param resourceType Type of resource to update
     * @param response JSON response.
     * @param optional optional values for storage offering.
     * @return updated resource response
     * @throws Exception error.
     */
    public String updateResourceLimit(Integer resourceType, String response, HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("updateResourceLimit", optional);
        arguments.add(new NameValuePair("resourcetype", resourceType.toString()));
        arguments.add(new NameValuePair("response", response));

        String updateResponse = server.request(arguments);

        return updateResponse;
    }

    /**
     * Lists resource limits.
     *
     * @param response JSON response.
     * @param optional optional values for storage offering.
     * @return list resource response
     * @throws Exception error
     */
    public String listResourceLimits(String response, HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("listResourceLimits", optional);
        arguments.add(new NameValuePair("response", response));

        String listResponse = server.request(arguments);

        return listResponse;
    }

    /**
     * Recalculate and update resource count for an account or domain.
     *
     * @param domainId updates resource counts for a specified account in this
     * domain
     * @param optional optional values
     * @param response json response
     * @return Resource Count Response
     * @throws Exception errors.
     */
    public String updateResourceCount(String domainId, String response,
            HashMap<String, String> optional)
            throws Exception {
        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("updateResourceCount", optional);
        arguments.add(new NameValuePair("domainid", domainId));
        arguments.add(new NameValuePair("response", response));
        String countResponse = server.request(arguments);
        return countResponse;
    }

}
