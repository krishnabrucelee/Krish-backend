package ck.panda.util;

import java.util.HashMap;
import java.util.LinkedList;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * CloudStack SSH key service for cloudStack connectivity with the Cloud Stack server.
 */
@Service
public class CloudStackSSHService {

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
     * Resets the SSH Key for virtual machine The virtual machine must be in a "Stopped" state.
     *
     *
     * @param virtualMachineId The ID of the virtual machine
     * @param sshKeyPair name of the ssh key pair used to login to the virtual machine
     * @param optional values for SSH Key
     * @param response JSON response
     * @return resetResponse
     * @throws Exception if error occurs
     */
    public String resetSSHKeyForVirtualMachine(String virtualMachineId, String sshKeyPair, String response,
            HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("resetSSHKeyForVirtualMachine", optional);
        arguments.add(new NameValuePair("id", virtualMachineId));
        arguments.add(new NameValuePair("keypair", sshKeyPair));
        arguments.add(new NameValuePair("response", response));

        String resetResponse = server.request(arguments);

        return resetResponse;
    }

    /**
     * Register a public key in a keypair under a certain name.
     *
     * @param keyPairName Name of the keypair
     * @param publicKey Public key material of the keypair
     * @param optional values for SSH Key
     * @param response JSON response
     * @return registerResponse
     * @throws Exception if error occurs
     */
    public String registerSSHKeyPair(String keyPairName, String publicKey, String response,
            HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("registerSSHKeyPair", optional);
        arguments.add(new NameValuePair("name", keyPairName));
        arguments.add(new NameValuePair("publickey", publicKey));
        arguments.add(new NameValuePair("response", response));

        String registerResponse = server.request(arguments);

        return registerResponse;
    }

    /**
     * Create a new keypair and returns the private key.
     *
     * @param keyPairName Name of the keypair
     * @param optional values for SSH Key
     * @param response JSON response
     * @return createResponse
     * @throws Exception if error occurs
     */
    public String createSSHKeyPair(String keyPairName, String response, HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("createSSHKeyPair", optional);
        arguments.add(new NameValuePair("name", keyPairName));
        arguments.add(new NameValuePair("response", response));

        String createResponse = server.request(arguments);

        return createResponse;
    }

    /**
     * Deletes a keypair by name.
     *
     * @param keyPairName The Name of the keypair
     * @param optional values for SSH Key
     * @param response JSON response
     * @return deleteResponse
     * @throws Exception if error occurs
     */
    public String deleteSSHKeyPair(String keyPairName, String response, HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("deleteSSHKeyPair", optional);
        arguments.add(new NameValuePair("name", keyPairName));
        arguments.add(new NameValuePair("response", response));

        String deleteResponse = server.request(arguments);

        return deleteResponse;
    }

    /**
     * List registered key pairs.
     *
     * @param optional values for SSH Key
     * @param response JSON response
     * @return listResponse
     * @throws Exception if error occurs
     */
    public String listSSHKeyPairs(String response, HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("listSSHKeyPairs", optional);
        arguments.add(new NameValuePair("response", response));
        String listResponse = server.request(arguments);

        return listResponse;
    }

}
