package ck.panda.util;

import java.util.HashMap;
import java.util.LinkedList;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * CloudStack VPN service for connectivity with CloudStack server.
 *
 */
@Service
public class CSVPNService {

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
     * Creates a remote access vpn.
     *
     * @param publicIpId public ip address id of the vpn server
     * @param optional additional parameters
     * @param response format
     * @return response string.
     * @throws Exception unhandled exceptions.
     */
    public String createRemoteAccessVpn(String publicIpId,
            HashMap<String, String> optional, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("createRemoteAccessVpn", optional);
        arguments.add(new NameValuePair("publicipid", publicIpId));
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }



    /**
     * Destroys a remote access vpn.
     *
     * @param publicIpId public ip address id of the vpn server
     * @param response format
     * @return response string.
     * @throws Exception unhandled exceptions.
     */
    public String deleteRemoteAccessVpn(String publicIpId, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("deleteRemoteAccessVpn", null);
        arguments.add(new NameValuePair("publicipid", publicIpId));
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }


    /**
     * Lists remote access vpns.
     *
     * @param publicIpId public ip address id of the vpn server
     * @param optional additional parameters
     * @param response format
     * @return response string.
     * @throws Exception unhandled exceptions.
     */
    public String listRemoteAccessVpns(String publicIpId,
            HashMap<String, String> optional, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listRemoteAccessVpns", optional);
        arguments.add(new NameValuePair("publicipid", publicIpId));
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }


    /**
     * Adds vpn users.
     *
     * @param password password for the username
     * @param username username for the vpn user
     * @param optional additional parameters
     * @param response format
     * @return response string.
     * @throws Exception unhandled exceptions.
     */
    public String addVpnUser(String password, String username,
            HashMap<String, String> optional, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("addVpnUser", optional);
        arguments.add(new NameValuePair("password", password));
        arguments.add(new NameValuePair("username", username));
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }


    /**
     * Removes a remote access vpn.
     *
     * @param userName public ip address id of the vpn server
     * @param optional additional parameters
     * @param response format
     * @return response string.
     * @throws Exception unhandled exceptions.
     */
    public String removeVpnUser(String userName, HashMap<String, String> optional, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("removeVpnUser", optional);
        arguments.add(new NameValuePair("username", userName));
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }

    /**
     * Lists vpn users.
     *
     * @param optional additional parameters
     * @param response format
     * @return response string.
     * @throws Exception unhandled exceptions.
     */
    public String listVpnUsers(HashMap<String, String> optional, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listVpnUsers", optional);
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }



    /**
     * Creates site to site vpn customer gateway.
     *
     * @param cidrList guest cidr list of the customer gateway
     * @param espPolicy ESP policy of the customer gateway
     * @param gateway public ip address id of the customer gateway
     * @param ikePolicy IKE policy of the customer gateway
     * @param ipsecPsk IPsec Preshared-Key of the customer gateway
     * @param optional additional parameters
     * @param response format
     * @return response string.
     * @throws Exception unhandled exceptions.
     */
    public String createVpnCustomerGateway(String cidrList, String espPolicy,
            String gateway, String ikePolicy, String ipsecPsk, HashMap<String, String> optional, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("createVpnCustomerGateway", optional);
        arguments.add(new NameValuePair("cidrlist", cidrList));
        arguments.add(new NameValuePair("esppolicy", espPolicy));
        arguments.add(new NameValuePair("gateway", gateway));
        arguments.add(new NameValuePair("ikepolicy", ikePolicy));
        arguments.add(new NameValuePair("ipsecpsk", ipsecPsk));
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }

    /**
     * Creates site to site vpn local gateway.
     *
     * @param vpcId public ip address id of the vpn gateway
     * @param response format
     * @return response string.
     * @throws Exception unhandled exceptions.
     */
    public String createVpnGateway(String vpcId, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("createVpnGateway", null);
        arguments.add(new NameValuePair("vpcid", vpcId));
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }

    /**
     * Creates site to site vpn connection.
     *
     * @param s2sCustomerGatewayId id of the customer gateway
     * @param s2sVpnGatewayId id of the vpn gateway
     * @param response format
     * @return response string.
     * @throws Exception unhandled exceptions.
     */
    public String createVpnConnection(String s2sCustomerGatewayId,
            String s2sVpnGatewayId, String response) throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("createVpnConnection", null);
        arguments.add(new NameValuePair("s2sCustomerGatewayId", s2sCustomerGatewayId));
        arguments.add(new NameValuePair("s2sVpnGatewayId", s2sVpnGatewayId));
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }


    /**
     * Delete site to site vpn customer gateway.
     *
     * @param customerGatewayId id of customer gateway
     * @param response format
     * @return response string.
     * @throws Exception unhandled exceptions.
     */
    public String deleteVpnCustomerGateway(String customerGatewayId, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("deleteVpnCustomerGateway", null);
        arguments.add(new NameValuePair("id", customerGatewayId));
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }



    /**
     * Delete site to site vpn gateway.
     *
     * @param customerGatewayId id of customer gateway
     * @param response format
     * @return response string.
     * @throws Exception unhandled exceptions.
     */
    public String deleteVpnGateway(String customerGatewayId, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("deleteVpnGateway", null);
        arguments.add(new NameValuePair("id", customerGatewayId));
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }


    /**
     * Delete site to site vpn connection.
     *
     * @param vpnConnectionId id of customer gateway
     * @param response format
     * @return response string.
     * @throws Exception unhandled exceptions.
     */
    public String deleteVpnConnection(String vpnConnectionId, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("deleteVpnConnection", null);
        arguments.add(new NameValuePair("id", vpnConnectionId));
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }


    /**
     * Update site to site vpn customer gateway.
     *
     * @param id of the vpn
     * @param cidrList guest cidr list of the customer gateway
     * @param espPolicy ESP policy of the customer gateway
     * @param gateway public ip address id of the customer gateway
     * @param ikePolicy IKE policy of the customer gateway
     * @param ipsecPsk IPsec Preshared-Key of the customer gateway
     * @param optional additional parameters
     * @param response format
     * @return response string.
     * @throws Exception unhandled exceptions.
     */
    public String updateVpnCustomerGateway(String id, String cidrList, String espPolicy,
            String gateway, String ikePolicy, String ipsecPsk, HashMap<String, String> optional, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("createVpnCustomerGateway", optional);
        arguments.add(new NameValuePair("cidrlist", cidrList));
        arguments.add(new NameValuePair("esppolicy", espPolicy));
        arguments.add(new NameValuePair("gateway", gateway));
        arguments.add(new NameValuePair("ikepolicy", ikePolicy));
        arguments.add(new NameValuePair("ipsecpsk", ipsecPsk));
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }



    /**
     * Reset site to site vpn connection.
     *
     * @param vpnConnectionId id of the customer gateway
     * @param optional additional parameters
     * @param response format
     * @return response string.
     * @throws Exception unhandled exceptions.
     */
    public String resetVpnConnection(String vpnConnectionId,
            HashMap<String, String> optional, String response) throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("resetVpnConnection", optional);
        arguments.add(new NameValuePair("id", vpnConnectionId));
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }


    /**
     * Lists site to site vpn customer gateways.
     *
     * @param optional additional parameters
     * @param response format
     * @return response string.
     * @throws Exception unhandled exceptions.
     */
    public String listVpnCustomerGateways(HashMap<String, String> optional, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listVpnCustomerGateways", optional);
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }


    /**
     * Retrieves the current status of asynchronous job for VPN.
     *
     * @param asychronousJobid the ID of the asychronous job
     * @param response format
     * @return response string.
     * @throws Exception unhandled exceptions.
     */
    public String vpnJobResult(String asychronousJobid, String response)
                    throws Exception {

            LinkedList<NameValuePair> arguments =
                    server.getDefaultQuery("queryAsyncJobResult", null);
            arguments.add(new NameValuePair("jobid",  asychronousJobid));
            arguments.add(new NameValuePair("response", response));
            return server.request(arguments);
    }


    /**
     * Retrieves the current status of asynchronous job for remoteaccessvpn.
     *
     * @param asychronousJobid the ID of the asychronous job
     * @param response format
     * @return response string.
     * @throws Exception unhandled exceptions.
     */
    public String remoteAccessVpnJobResult(String asychronousJobid, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("queryAsyncJobResult", null);
        arguments.add(new NameValuePair("jobid", asychronousJobid));
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }


    /**
     * Retrieves the current status of asynchronous job for vpnuser.
     *
     * @param asychronousJobid the ID of the asychronous job
     * @param response format
     * @return response string.
     * @throws Exception unhandled exceptions.
     */
    public String vpnUserJobResult(String asychronousJobid, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("queryAsyncJobResult", null);
        arguments.add(new NameValuePair("jobid", asychronousJobid));
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }


    /**
     * Retrieves the current status of asynchronous job for vpncustomergateway.
     *
     * @param asychronousJobid the ID of the asychronous job
     * @param response format
     * @return response string.
     * @throws Exception unhandled exceptions.
     */
    public String vpnCustomerGatewayJobResult(String asychronousJobid, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("queryAsyncJobResult", null);
        arguments.add(new NameValuePair("jobid", asychronousJobid));
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }


    /**
     * Retrieves the current status of asynchronous job for vpngateway.
     *
     * @param asychronousJobid the ID of the asychronous job
     * @param response format
     * @return response string.
     * @throws Exception unhandled exceptions.
     */
    public String vpnGatewayJobResult(String asychronousJobid, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("queryAsyncJobResult", null);
        arguments.add(new NameValuePair("jobid", asychronousJobid));
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }



    /**
     * Retrieves the current status of asynchronous job for vpnconnection.
     *
     * @param asychronousJobid the ID of the asychronous job
     * @param response format
     * @return response string.
     * @throws Exception unhandled exceptions.
     */
    public String vpnConnectionJobResult(String asychronousJobid, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("queryAsyncJobResult", null);
        arguments.add(new NameValuePair("jobid", asychronousJobid));
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }

    /**
     * Lists site to site vpn gateways.
     *
     * @param optional additional parameters
     * @param response format
     * @return response string.
     * @throws Exception unhandled exceptions.
     */
    public String listVpnGateways(HashMap<String, String> optional, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listVpnGateways", optional);
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }

    /**
     * Lists site to site vpn connection gateways.
     *
     * @param optional additional parameters
     * @param response format
     * @return response string.
     * @throws Exception unhandled exceptions.
     */
    public String listVpnConnections(HashMap<String, String> optional, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listVpnConnections", optional);
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }

}
