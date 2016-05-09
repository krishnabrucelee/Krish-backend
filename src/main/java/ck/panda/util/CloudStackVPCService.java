package ck.panda.util;

import java.util.HashMap;
import java.util.LinkedList;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * CloudStack VPC service for connectivity with CloudStack server.
 *
 */
@Service
public class CloudStackVPCService {

    /** Cloudstack server for connectivity. */
    @Autowired
    private CloudStackServer server;

    /**
     * sets api key, secret key and url.
     *
     * @param server sets these values.
     */
    public void setServer(CloudStackServer server) {
        this.server = server;
    }

    /**
     * Creates a VPC.
     *
     * @param cidr cidr address id of the vpc server
     * @param displayText displayText of the vpc server
     * @param name name of the vpc server
     * @param vpcOfferingId vpc offering id
     * @param zoneId zone id
     * @param optional value
     * @param response format
     * @return response sting
     * @throws Exception unhandled errors.
     */
    public String createVPC(String cidr, String zoneId, HashMap<String, String> optional, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("createVPC", optional);
        arguments.add(new NameValuePair("cidr", cidr));
        arguments.add(new NameValuePair("zoneid", zoneId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * List VPCs.
     *
     * @param optional value
     * @param response format
     * @return string value
     * @throws Exception unhandled errors.
     */
    public String listVPCs(HashMap<String, String> optional, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listVPCs", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Retrieves the current status of asynchronous job for vpc.
     *
     * @param asychronousJobid the ID of the asychronous job
     * @param response format
     * @return string value
     * @throws Exception unhandled errors.
     */
    public String vpcJobResult(String asychronousJobid, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("queryAsyncJobResult", null);
        arguments.add(new NameValuePair("jobid", asychronousJobid));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Retrieves the current status of asynchronous job for vpcoffering.
     *
     * @param asychronousJobid the ID of the asychronous job
     * @param response format
     * @return string value
     * @throws Exception unhandled errors.
     */
    public String vpcOfferingJobResult(String asychronousJobid, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("queryAsyncJobResult", null);
        arguments.add(new NameValuePair("jobid", asychronousJobid));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Retrieves the current status of asynchronous job for privategateway.
     *
     * @param asychronousJobid the ID of the asychronous job
     * @param response format
     * @return stirng value
     * @throws Exception unhandled errors.
     */
    public String privateGatewayJobResult(String asychronousJobid, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("queryAsyncJobResult", null);
        arguments.add(new NameValuePair("jobid", asychronousJobid));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Retrieves the current status of asynchronous job for staticroute.
     *
     * @param asychronousJobid the ID of the asychronous job
     * @param response format
     * @return string value
     * @throws Exception unhandled errors.
     */
    public String staticRouteJobResult(String asychronousJobid, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("queryAsyncJobResult", null);
        arguments.add(new NameValuePair("jobid", asychronousJobid));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Delete VPC.
     *
     * @param vpcId vpc id
     * @param response format
     * @return string value
     * @throws Exception unhandled errors.
     */
    public String deleteVPC(String vpcId, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("deleteVPC", null);
        arguments.add(new NameValuePair("id", vpcId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Updates a VPC.
     *
     * @param vpcId the id of the VPC
     * @param vpcName name of the VPC
     * @param optional value
     * @param response format
     * @return string value
     * @throws Exception unhandled errors.
     */
    public String updateVPC(String vpcId, String vpcName,
            HashMap<String, String> optional, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("updateVPC", optional);
        arguments.add(new NameValuePair("id", vpcId));
        arguments.add(new NameValuePair("name", vpcName));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Restart VPC.
     *
     * @param vpcId vpc id
     * @param response format
     * @return string value
     * @throws Exception unhandled errors.
     */
    public String restartVPC(String vpcId, String response, HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("restartVPC", optional);
        arguments.add(new NameValuePair("id", vpcId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Creates a VPCOffering.
     *
     * @param displayText displayText of the vpc server
     * @param name name of the vpc server
     * @param vpcOfferingId the id of the vpc offering
     * @param supportedServices services supported by the vpc offering
     * @param optional value
     * @param response format
     * @return string value
     * @throws Exception unhandled errors.
     */
    public String createVPCOffering(String displayText, String name,
            String vpcOfferingId, String supportedServices, HashMap<String, String> optional, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("createVPCOffering", optional);
        arguments.add(new NameValuePair("displaytext", displayText));
        arguments.add(new NameValuePair("name", name));
        arguments.add(new NameValuePair("vpcofferingid", vpcOfferingId));
        arguments.add(new NameValuePair("supportedservices", supportedServices));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Updates a VPCOffering.
     *
     * @param vpcOfferingId the id of the vpc offering
     * @param optional value
     * @param response format
     * @return string value
     * @throws Exception unhandled errors.
     */
    public String updateVPCOffering(String vpcOfferingId,
            HashMap<String, String> optional, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("updateVPCOffering", optional);
        arguments.add(new NameValuePair("id", vpcOfferingId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Delete VPCOffering.
     *
     * @param vpcOfferingId vpc offering id
     * @param response format
     * @return string value
     * @throws Exception unhandled errors.
     */
    public String deleteVPCOffering(String vpcOfferingId, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("deleteVPCOffering", null);
        arguments.add(new NameValuePair("id", vpcOfferingId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * List VPCOfferings.
     *
     * @param optional value
     * @param response format
     * @return string value
     * @throws Exception unhandled errors.
     */
    public String listVPCOfferings(HashMap<String, String> optional, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listVPCOfferings", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Creates a PrivateGateway.
     *
     * @param gateway the gateway of the Private gateway
     * @param ipAddress the IP address of the Private gateway
     * @param netmask the netmask of the Private gateway
     * @param vlan the Vlan for the private gateway
     * @param vpcId the VPC network belongs to
     * @param optional value
     * @param response format
     * @return string value
     * @throws Exception unhandled errors.
     */
    public String createPrivateGateway(String gateway, String ipAddress, String netmask,
            String vlan, String vpcId, HashMap<String, String> optional, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("createPrivateGateway", optional);
        arguments.add(new NameValuePair("gateway", gateway));
        arguments.add(new NameValuePair("ipaddress", ipAddress));
        arguments.add(new NameValuePair("netmask", netmask));
        arguments.add(new NameValuePair("vlan", vlan));
        arguments.add(new NameValuePair("vpcid", vpcId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * List PrivateGateways.
     *
     * @param optional value
     * @param response format
     * @return string value
     * @throws Exception unhandled errors.
     */
    public String listPrivateGateways(HashMap<String, String> optional, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listPrivateGateways", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Deletes a Private gateway.
     *
     * @param privateGatewayId private gateway id
     * @param response format
     * @return string value
     * @throws Exception unhandled errors.
     */
    public String deletePrivateGateway(String privateGatewayId, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("deletePrivateGateway", null);
        arguments.add(new NameValuePair("id", privateGatewayId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Creates a static route.
     *
     * @param cidr cidr address id of the vpc server
     * @param gatewayId gateway id
     * @param response format
     * @return string value
     * @throws Exception unhandled errors.
     */
    public String createStaticRoute(String cidr, String gatewayId, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("createStaticRoute", null);
        arguments.add(new NameValuePair("cidr", cidr));
        arguments.add(new NameValuePair("gatewayid", gatewayId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Deletes a Static Route.
     *
     * @param staticRouteId static route id
     * @param response format
     * @return string value
     * @throws Exception unhandled errors.
     */
    public String deleteStaticRoute(String staticRouteId, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("deleteStaticRoute", null);
        arguments.add(new NameValuePair("id", staticRouteId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * List StaticRoutes.
     *
     * @param optional value
     * @param response format
     * @return string value
     * @throws Exception unhandled errors.
     */
    public String listStaticRoutes(HashMap<String, String> optional, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listStaticRoutes", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * List VPCOfferings.
     *
     * @param optional value
     * @param response format
     * @return string value
     * @throws Exception unhandled errors.
     */
    public String listNetworkACLLists(HashMap<String, String> optional, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listNetworkACLLists", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * List supported network services.
     *
     * @param optional value
     * @param response format
     * @return string value
     * @throws Exception unhandled errors.
     */
    public String listSupportedNetworkServices(HashMap<String, String> optional, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listSupportedNetworkServices", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * List network service provider services.
     *
     * @param optional value
     * @param response format
     * @return string value
     * @throws Exception unhandled errors.
     */
    public String listNetworkServiceProviders(HashMap<String, String> optional, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listNetworkServiceProviders", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

}
