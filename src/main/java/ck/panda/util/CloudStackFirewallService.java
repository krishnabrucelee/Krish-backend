package ck.panda.util;

import java.util.HashMap;
import java.util.LinkedList;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * CloudStack creates port forwarding rule, ingress rule and egress rule for a IP address in a Network .
 *
 */
@Service
public class CloudStackFirewallService {

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
     * Lists all port forwarding rules for an IP address.
     *
     * @param optional values to Cloud Stack.
     * @param response json or xml
     * @return list port forwarding rules.
     * @throws Exception if error occurs.
     */
    public String listPortForwardingRules(HashMap<String, String> optional, String response) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("listPortForwardingRules", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Create port forwarding rule for an IP address.
     *
     * @param portForwardingRuleIpAddressid of the port forwarding rule
     * @param portForwardingRulePrivatePort for a network.
     * @param portForwardingRuleProtocol for a network.
     * @param portForwardingRulePublicPort for a network.
     * @param portForwardingRuleVirtualMachineId for an Ip address.
     * @param optional values from CS.
     * @param response josn or xml.
     * @return port forwarding rule.
     * @throws Exception unhandled errors.
     */
    public String createPortForwardingRule(String portForwardingRuleIpAddressid,String portForwardingRulePrivatePort, String portForwardingRuleProtocol,
            String portForwardingRulePublicPort, String portForwardingRuleVirtualMachineId,
            HashMap<String, String> optional, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("createPortForwardingRule", optional);
        arguments.add(new NameValuePair("ipaddressid", portForwardingRuleIpAddressid));
        arguments.add(new NameValuePair("privateport", portForwardingRulePrivatePort));
        arguments.add(new NameValuePair("protocol", portForwardingRuleProtocol));
        arguments.add(new NameValuePair("publicport", portForwardingRulePublicPort));
        arguments.add(new NameValuePair("virtualmachineid", portForwardingRuleVirtualMachineId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Deletes a port forwarding rule .
     *
     * @param portForwardingRuleId the ID of the port forwarding rule
     * @param response json or xml.
     * @return delete port forwarding rule.
     * @throws Exception if error occurs.
     */
    public String deletePortForwardingRule(String portForwardingRuleId, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("deletePortForwardingRule", null);
        arguments.add(new NameValuePair("id", portForwardingRuleId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }


    /**
     * Updates a port forwarding rule.
     *
     * @param portForwardingRuleIpAddressid the IP address id of the port forwarding rule
     * @param portForwardingRulePrivatePort the starting port of port forwarding rule's private port range
     * @param portForwardingRuleProtocol the protocol for the port fowarding rule. Valid values are TCP or UDP.
     * @param portForwardingRulePublicPort the s tarting port of port forwarding rule's public port range
     * @param response json or xml.
     * @param optional values to Cloud Stack.
     * @return update port forwarding rule.
     * @throws Exception unhandled errors.
     */
    public String updatePortForwardingRule(String portForwardingRuleIpAddressid,
            String portForwardingRulePrivatePort, String portForwardingRuleProtocol,
            String portForwardingRulePublicPort, String response, HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("UpdatePortForwardingRule", optional);
        arguments.add(new NameValuePair("ipaddressid", portForwardingRuleIpAddressid));
        arguments.add(new NameValuePair("privateport", portForwardingRulePrivatePort));
        arguments.add(new NameValuePair("protocol", portForwardingRuleProtocol));
        arguments.add(new NameValuePair("publicport", portForwardingRulePublicPort));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Create firewall rule for an Ip Address.
     *
     * @param ipAddressId the IP address id of the port forwarding rule
     * @param firewallProtocol the protocol for the firewall rule. Valid values are TCP/UDP/ICMP.
     * @param response json or xml
     * @param optional values to Cloud Stack.
     * @return create firewall.
     * @throws Exception if error occurs.
     */
    public String createFirewallRule(String ipAddressId,
            String firewallProtocol,String response,HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("createFirewallRule", optional);
        arguments.add(new NameValuePair("ipaddressid", ipAddressId));
        arguments.add(new NameValuePair("protocol", firewallProtocol));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Deletes a firewall rule.
     *
     * @param firewallRuleId the ID of the firewall rule
     * @param response json or xml.
     * @return delete firewall rule.
     * @throws Exception if error occurs.
     */
    public String deleteFirewallRule(String firewallRuleId,String response)
            throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("deleteFirewallRule", null);
        arguments.add(new NameValuePair("id", firewallRuleId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Lists all firewall rules for an IP address.
     *
     * @param optional values to CloudStack.
     * @param response json or xml.
     * @return list Firewall rule.
     * @throws Exception if error occurs.
     */
    public String listFirewallRules(String response,HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("listFirewallRules", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Creates a egress firewall rule for a given network.
     *
     * @param networkId of an IP address.
     * @param firewallProtocol the protocol for the firewall rule. Valid values are TCP/UDP/ICMP.
     * @param optional values to Cloud Stack.
     * @param response json or xml.
     * @return create egress firewall.
     * @throws Exception if error occurs.
     */
    public String createEgressFirewallRule(String networkId,
            String firewallProtocol, String response,HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("createEgressFirewallRule", optional);
        arguments.add(new NameValuePair("networkid", networkId));
        arguments.add(new NameValuePair("protocol", firewallProtocol));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Deletes an egress firewall rule.
     *
     * @param firewallRuleId the ID of the firewall rule.
     * @param response json or xml.
     * @return delete egress firewall rule.
     * @throws Exception if error occurs.
     */
    public String deleteEgressFirewallRule(String firewallRuleId, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("deleteEgressFirewallRule", null);
        arguments.add(new NameValuePair("id", firewallRuleId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Lists all egress firewall rules for network id.
     *
     * @param optional values to Cloudstack.
     * @param response json or xml.
     * @return list egress rules.
     * @throws Exception if error occurs.
     */
    public String listEgressFirewallRules(String response, HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("listEgressFirewallRules", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Retrieves the current status of asynchronous job for fire wall rules.
     *
     * @param asychronousJobid the ID of the asychronous job
     * @return response.
     * @throws Exception if error occurs.
     */
    public String firewallJobResult(String asychronousJobid, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("queryAsyncJobResult", null);
        arguments.add(new NameValuePair("jobid", asychronousJobid));
         arguments.add(new NameValuePair("response",response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }



   }
