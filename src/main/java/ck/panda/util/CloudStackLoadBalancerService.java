package ck.panda.util;

import java.util.HashMap;
import java.util.LinkedList;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The CloudStack Management Server should be deployed in a multi-node configuration such that it is not susceptible to individual server failures.
 * CloudStack can use a load balancer to provide a virtual IP for multiple Management Servers.
 */
@Service
public class CloudStackLoadBalancerService {

    /** Cloudstack server for connectivity. */
    @Autowired
    private CloudStackServer server;

    /**
     * setServer passes apikey, url, secretkey from UI and aids to establish
     * cloudstack connectivity.
     *
     * @param server sets apikey and url.
     */
    public void setServer(CloudStackServer server) {
        this.server = server;
    }

    /**
     * Creates a load balancer rule.
     *
     * @param algorithm load balancer algorithm(source,roundrobin,leastconn)
     * @param name name of the load balancer rule
     * @param privatePort the private port of the private ip address/virtual
     * machine
     * @param publicPort the public port from where the network traffic will be
     * load balanced from
     * @param optional values to CS.
     * @param response json or xml.
     * @return created load balancer rule.
     * @throws Exception error occurs.
     */
    public String createLoadBalancerRule(String algorithm, String name, String privatePort,
            String publicPort,String response, HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("createLoadBalancerRule", optional);
        arguments.add(new NameValuePair("algorithm", algorithm));
        arguments.add(new NameValuePair("name", name));
        arguments.add(new NameValuePair("privateport", privatePort));
        arguments.add(new NameValuePair("publicport", publicPort));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Deletes a load balancer rule.
     *
     * @param loadBalancerRuleId of the load Balancer.
     * @param response json or xml.
     * @return delete load balancer rule.
     * @throws Exception if error occurs.
     */
    public String deleteLoadBalancerRule(String loadBalancerRuleId, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("deleteLoadBalancerRule", null);
        arguments.add(new NameValuePair("id", loadBalancerRuleId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return  responseDocument;
    }

    /**
     * Removes a virtual machine or a list of virtual machines from a load
     * balancer rule.
     *
     * @param loadBalancerRuleId The ID of the load balancer rule
     * @param virtualMachineIds the list of IDs of the virtual machines
     * @param response json or xml.
     * @return remove load balancer.
     * @throws Exception if error occurs.
     */
    public String removeFromLoadBalancerRule(String loadBalancerRuleId, String response, HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("removeFromLoadBalancerRule", optional);
        arguments.add(new NameValuePair("id", loadBalancerRuleId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return  responseDocument;
    }

    /**
     * Assigns virtual machine or a list of virtual machines to a load balancer
     * rule.
     *
     * @param loadBalancerRuleId The ID of the load balancer rule
     * @param virtualMachineIds the list of IDs of the virtual machines
     * @param response json or xml.
     * @return assign to load Balancer.
     * @throws Exception if error occurs.
     */
    public String assignToLoadBalancerRule(String loadBalancerRuleId, String response, HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("assignToLoadBalancerRule", optional);
        arguments.add(new NameValuePair("id", loadBalancerRuleId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return  responseDocument;
    }

    /**
     * Creates a Load Balancer stickiness policy.
     *
     * @param lbruleId the ID of the load balancer rule
     * @param methodName name of the LB Stickiness policy method
     * @param name name of the LB Stickiness policy
     * @param response json or xml.
     * @param optional values to CS.
     * @return create LB stickiness policy.
     * @throws Exception if error occurs.
     */
    public String createLBStickinessPolicy(String lbruleId,String response, HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("createLBStickinessPolicy", optional);
        arguments.add(new NameValuePair("lbruleid", lbruleId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return  responseDocument;
    }

    /**
     * Deletes a LB stickiness policy.
     *
     * @param lbStickinessPolicyId for load balancer.
     * @param response json or xml.
     * @return delete LB stickiness policy id.
     * @throws Exception if error occurs.
     */
    public String deleteLBStickinessPolicy(String lbStickinessPolicyId,String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("deleteLoadBalancerRule", null);
        arguments.add(new NameValuePair("id", lbStickinessPolicyId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return  responseDocument;
    }

    /**
     * Lists load balancer rules.
     *
     * @param optional values to CS
     * @param response json or xml.
     * @return list load balancer.
     * @throws Exception if error occurs.
     */
    public String listLoadBalancerRules(String response, HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listLoadBalancerRules", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Lists LBStickiness policies.
     *
     * @param optional values to CS.
     * @param response json or xml.
     * @return lst load balancer stickiness policy.
     * @throws Exception if error occurs.
     */
    public String listLBStickinessPolicies(String response,
            HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listLBStickinessPolicies", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Lists load balancer HealthCheck policies.
     *
     * @param lbRuleId of the load balancer.
     * @param optional values to CS.
     * @param response json or xml.
     * @return list load Balancer health Check policy.
     * @throws Exception if error occurs.
     */
    public String listLBHealthCheckPolicies(String lbRuleId, String response,
            HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listLBHealthCheckPolicies", optional);
        arguments.add(new NameValuePair("lbruleid", lbRuleId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return  responseDocument;
    }

    /**
     * Creates a Load Balancer health check policy.
     *
     * @param lbRuleId the ID of the load balancer rule
     * @param optional values to CS.
     * @param response json or xml.
     * @return create LB health check policy.
     * @throws Exception if error occurs.
     */
    public String createLBHealthCheckPolicy(String lbRuleId,String response, HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("createLBHealthCheckPolicy", optional);
        arguments.add(new NameValuePair("lbruleid", lbRuleId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return  responseDocument;
    }

    /**
     * Deletes a load balancer HealthCheck policy.
     *
     * @param lbHealthCheckPolicyId of the Load Balancer.
     * @param response json or xml.
     * @return delete LB health check policy.
     * @throws Exception if error occurs.
     */
    public String deleteLBHealthCheckPolicy(String lbHealthCheckPolicyId, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("deleteLBHealthCheckPolicy", null);
        arguments.add(new NameValuePair("id", lbHealthCheckPolicyId));
        arguments.add(new NameValuePair("response", response));

        String responseDocument = server.request(arguments);
        return  responseDocument;
    }

    /**
     * List all virtual machine instances that are assigned to a load balancer
     * rule.
     *
     * @param lbRuleId the ID of the load balancer rule
     * @param optional values to CS.
     * @param response json or xml.
     * @return list Load Balancer Rule.
     * @throws Exception if error occurs.
     */
    public String listLoadBalancerRuleInstances(String lbRuleId,String response,HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listLoadBalancerRuleInstances", optional);
        arguments.add(new NameValuePair("id", lbRuleId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return  responseDocument;
    }

    /**
     * Updates load balancer.
     *
     * @param lbRuleId the ID of the load balancer rule
     * @param optional values to CS.
     * @param response json or xml.
     * @return update Load Balancer.
     * @throws Exception if error occurs.
     */
    public String updateLoadBalancerRule(String lbRuleId,String response, HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("updateLoadBalancerRule", optional);
        arguments.add(new NameValuePair("id", lbRuleId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return  responseDocument;
    }

    /**
     * Creates a global load balancer rule.
     *
     * @param gslbDomainName domain name for the GSLB service
     * @param gslbServiceType GSLB service type (tcp, udp)
     * @param loadBalancerRuleName name of the load balancer rule
     * @param regionId region where the global load balancer is going to be
     * created
     * @param optional values to CS.
     * @param response json or xml.
     * @return create Load balancer rule.
     * @throws Exception if error occurs.
     */
    public String createGlobalLoadBalancerRule(String gslbDomainName,
            String gslbServiceType, String loadBalancerRuleName, String regionId,String response,HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("createGlobalLoadBalancerRule", optional);
        arguments.add(new NameValuePair("gslbdomainname", gslbDomainName));
        arguments.add(new NameValuePair("gslbservicetype", gslbServiceType));
        arguments.add(new NameValuePair("loadbalancerrulename", loadBalancerRuleName));
        arguments.add(new NameValuePair("regionid", regionId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return  responseDocument;
    }

    /**
     * Deletes a global load balancer rule.
     *
     * @param globalLoadBalancerRuleId the ID of the global load balancer rule
     * @param response json or xml.
     * @return delete Global Laod Balancer rule.
     * @throws Exception if error occurs.
     */
    public String deleteGlobalLoadBalancerRule(String globalLoadBalancerRuleId, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("deleteGlobalLoadBalancerRule", null);
        arguments.add(new NameValuePair("id", globalLoadBalancerRuleId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return  responseDocument;
    }

    /**
     * Update global load balancer rules.
     *
     * @param id the ID of the global load balancer rule
     * @param optional values to CS>
     * @param response json or xml.
     * @return update Global Load Balancer rule.
     * @throws Exception if error occurs.
     */
    public String updateGlobalLoadBalancerRule(String id,String response,
            HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("updateGlobalLoadBalancerRule", optional);
        arguments.add(new NameValuePair("id", id));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return  responseDocument;
    }

    /**
     * Lists load balancer rules.
     *
     * @param optional values to CS.
     * @param response json or xml.
     * @return list Global Load Balancer rule.
     * @throws Exception if error occurs.
     */
    public String listGlobalLoadBalancerRules(String response,HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listGlobalLoadBalancerRules", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Removes a load balancer rule association with global load balancer rule.
     *
     * @param loadBalancerRuleId the ID of the load balancer rule
     * @param loadBalancerRuleList the list load balancer rules that will be
     * assigned to gloabal load balacner rule
     * @param optional values to CS.
     * @param response json or xml.
     * @return remove from Global Load Balancer rule.
     * @throws Exception if error occurs.
     */
    public String removeFromGlobalLoadBalancerRule(String loadBalancerRuleId,
            String loadBalancerRuleList,String response,HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("removeFromGlobalLoadBalancerRule", optional);
        arguments.add(new NameValuePair("id", loadBalancerRuleId));
        arguments.add(new NameValuePair("loadbalancerrulelist", loadBalancerRuleList));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return  responseDocument;
    }

    /**
     * Creates a Load Balancer.
     *
     * @param algorithm the load balancer algorithm
     * @param instancePort the TCP port of the virtual machine where the network
     * traffic will be load balanced to
     * @param name name of the Load Balancer
     * @param networkId the guest network the Load Balancer will be created for
     * @param scheme the load balancer scheme
     * @param sourceIpAddressNetworkId the network id of the source ip address
     * @param sourcePort the source port the network traffic will be load
     * balanced from
     * @param response json or xml.
     * @param optional values to CS.
     * @return create load balancer.
     * @throws Exception if error occurs.
     */
    public String createLoadBalancer(String algorithm, String instancePort,
            String name, String networkId, String scheme, String sourceIpAddressNetworkId,
            String sourcePort,String response, HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("createLoadBalancer", optional);
        arguments.add(new NameValuePair("algorithm", algorithm));
        arguments.add(new NameValuePair("instanceport", instancePort));
        arguments.add(new NameValuePair("name", name));
        arguments.add(new NameValuePair("networkid", networkId));
        arguments.add(new NameValuePair("scheme", scheme));
        arguments.add(new NameValuePair("sourceipaddressnetworkid", sourceIpAddressNetworkId));
        arguments.add(new NameValuePair("sourceport", sourcePort));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Lists Load Balancers.
     *
     * @param optional values to CS.
     * @param response json or xml.
     * @return List load balancers.
     * @throws Exception if error occurs.
     */
    public String listLoadBalancers(String response,HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listLoadBalancers", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Retrieves the current status of asynchronous job for loadbalancerrule.
     *
     * @param asychronousJobid the ID of the asychronous job.
     * @param response json or xml.
     * @return load balancer rulr.
     * @throws Exception if error occurs.
     */
    public String loadBalancerRuleJobResult(String asychronousJobid, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("queryAsyncJobResult", null);
        arguments.add(new NameValuePair("jobid", asychronousJobid));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Retrieves the current status of asynchronous job for lbStickinessPolicy.
     *
     * @param asychronousJobid the ID of the asychronous job
     * @param response json or xml.
     * @return lb Stickiness Policy Job result.
     * @throws Exception if error occurs.
     */
    public String lbStickinessPolicyJobResult(String asychronousJobid, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("queryAsyncJobResult", null);
        arguments.add(new NameValuePair("jobid", asychronousJobid));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Retrieves the current status of asynchronous job for lbHealthCheckPolicy.
     *
     * @param asychronousJobid the ID of the asychronous job
     * @param response json or xml.
     * @return lb Stickiness policy.
     * @throws Exception if error occurs.
     */
    public String lbHealthCheckPolicyJobResult(String asychronousJobid,String response) throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("queryAsyncJobResult", null);
        arguments.add(new NameValuePair("jobid", asychronousJobid));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return  responseDocument;
    }

    /**
     * Retrieves the current status of asynchronous job for lbHealthCheckPolicy.
     *
     * @param asychronousJobid the ID of the asychronous job
     * @param response json or xml.
     * @return global Load Balancer Rule Job Result.
     * @throws Exception if error occurs.
     */
    public String globalLoadBalancerRuleJobResult(String asychronousJobid,String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("queryAsyncJobResult", null);
        arguments.add(new NameValuePair("jobid", asychronousJobid));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Retrieves the current status of asynchronous job for loadBalancer.
     *
     * @param asychronousJobid the ID of the asychronous job.
     * @param response json or xml.
     * @return Load balancer job result.
     * @throws Exception if error occurs.
     */
    public String loadBalancerJobResult(String asychronousJobid,String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("queryAsyncJobResult", null);
        arguments.add(new NameValuePair("jobid", asychronousJobid));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Deletes a load balancer.
     *
     * @param loadBalancerId the ID of the load balancer
     * @param response json or xml.
     * @return delete Load Balancer.
     * @throws Exception if error occurs.
     */
    public String deleteLoadBalancer(String loadBalancerId, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("deleteLoadBalancer", null);
        arguments.add(new NameValuePair("id", loadBalancerId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return  responseDocument;
    }

    /**
     * Add F5 Load Balancer.
     *
     * @param networkDeviceType supports only F5BigIpLoadBalancer
     * @param password Credentials to reach F5 BigIP load balancer device
     * @param physicalNetworkId the Physical Network ID
     * @param url URL of the F5 load balancer appliance.
     * @param userName Credentials to reach F5 BigIP load balancer device
     * @param optional values to CS.
     * @param response json or xml.
     * @return add F5 load balancer.
     * @throws Exception if error occurs.
     */
    public String addF5LoadBalancer(String networkDeviceType, String password,
            String physicalNetworkId, String url, String userName, String response,HashMap<String, String> optional) throws Exception {
        LinkedList<NameValuePair> arguments = server.getDefaultQuery("addF5LoadBalancer", optional);
        arguments.add(new NameValuePair("networkdevicetype", networkDeviceType));
        arguments.add(new NameValuePair("password", password));
        arguments.add(new NameValuePair("physicalnetworkid", physicalNetworkId));
        arguments.add(new NameValuePair("url", url));
        arguments.add(new NameValuePair("username", userName));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return  responseDocument;

    }

    /**
     * configures a F5 load balancer device.
     *
     * @param lbDeviceId F5 load balancer device ID
     * @param optional values to CS.
     * @param response json or xml.
     * @return configure F5 Load Balancer.
     * @throws Exception if error occurs.
     */
    public String configureF5LoadBalancer(String lbDeviceId,String response,HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("configureF5LoadBalancer", optional);
        arguments.add(new NameValuePair("lbdeviceid", lbDeviceId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * delete a F5 load balancer device.
     *
     * @param lbDeviceId netscaler load balancer device ID
     * @param response json or xml.
     * @return delete F5 Load Balancer.
     * @throws Exception if error occurs.
     */
    public String deleteF5LoadBalancer(String lbDeviceId, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("deleteLoadBalancer", null);
        arguments.add(new NameValuePair("lddeviceid", lbDeviceId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * List F5 Load Balancers.
     *
     * @param optional values to CS.
     * @param response json or xml.
     * @return list F5 Load balancer.
     * @throws Exception if error occurs.
     */
    public String listF5LoadBalancers(String response,HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listF5LoadBalancers", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return  responseDocument;
    }

    /**
     * Add Net scaler Load Balancer.
     *
     * @param networkDeviceType Netscaler device type supports NetscalerMPXLoadBalancer, NetscalerVPXLoadBalancer, NetscalerSDXLoadBalancer.
     * @param password Credentials to reach netscaler load balancer device
     * @param physicalNetworkId the Physical Network ID
     * @param url URL of the netscaler load balancer appliance..
     * @param userName Credentials to reach netscaler load balancer device.
     * @param optional values to Cs.
     * @param response json or xml.
     * @return add Net Scaler Load Balancer.
     * @throws Exception if error occurs.
     */
    public String addNetscalerLoadBalancer(String networkDeviceType, String password,
            String physicalNetworkId, String url, String userName, String response,HashMap<String, String> optional) throws Exception {
        LinkedList<NameValuePair> arguments = server.getDefaultQuery("addNetscalerLoadBalancer", optional);
        arguments.add(new NameValuePair("networkdevicetype", networkDeviceType));
        arguments.add(new NameValuePair("password", password));
        arguments.add(new NameValuePair("physicalnetworkid", physicalNetworkId));
        arguments.add(new NameValuePair("url", url));
        arguments.add(new NameValuePair("username", userName));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return  responseDocument;

    }

    /**
     * Configure Net scaler Load Balancer.
     *
     * @param lbDeviceId netscaler load balancer device ID
     * @param optional values to Cs.
     * @param response json or xml.
     * @return configureNetscalerLoadBalancer
     * @throws Exception if errror occurs.
     */
    public String configureNetscalerLoadBalancer(String lbDeviceId,String response,HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("configureNetscalerLoadBalancer", optional);
        arguments.add(new NameValuePair("lbdeviceid", lbDeviceId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Delete Net scaler Load Balancer.
     *
     * @param lbDeviceId netscaler load balancer device ID
     * @param response json or xml.
     * @return deleteNetscalerLoadBalancer
     * @throws Exception if error occurs.
     */
    public String deleteNetscalerLoadBalancer(String lbDeviceId,String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("deleteNetscalerLoadBalancer", null);
        arguments.add(new NameValuePair("lddeviceid", lbDeviceId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * List Net scale Load Balancer.
     *
     * @param optional values to Cs.
     * @param response json or xml.
     * @return list Net scaler Load Balancers.
     * @throws Exception if error occurs.
     */
    public String listNetscalerLoadBalancers(String response,HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listNetscalerLoadBalancers", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return  responseDocument;
    }


    /**
     * Update LB Health Check Policy.
     *
     * @param id of the Load Balancer Health Check policy.
     * @param optional values to Cs.
     * @param response json or xml.
     * @return update Health Check policy.
     * @throws Exception if error occurs.
     */
    public String updateLBHealthCheckPolicy(String id,String response,
            HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("updateLBHealthCheckPolicy", optional);
        arguments.add(new NameValuePair("id", id));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return  responseDocument;
    }

    /**
     * Update LB stickiness Policy.
     *
     * @param id of the Load Balancer Stickiness Policy.
     * @param optional values to CS.
     * @param response json or xml.
     * @return updateLBStickinessPolicy.
     * @throws Exception if error occurs.
     */
    public String updateLBStickinessPolicy(String id,String response,
            HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("updateLBStickinessPolicy", optional);
        arguments.add(new NameValuePair("id", id));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }
}
