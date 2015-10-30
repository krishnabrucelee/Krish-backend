package ck.panda.util;

import java.util.HashMap;
import java.util.LinkedList;

import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

/**
 * @author Dev-Cloud
 *
 */
@Service
public class CloudStackInstanceService {
	/** Cloudstack server for connectivity. */
    @Autowired
    private CloudStackServer server;

    /** sets api key , secret key and url.
     * @param server sets these values.
     */
    public void setServer(CloudStackServer server) {
        this.server = server;
    }

    /**
     * Deploy new virtual machine in cloudstack.
     *
     * @param serviceOfferingId compute offer id.
     * @param templateId template id.
     * @param zoneId zone id.
     * @param response response type.
     * @param optional parameters
     * @return json response.
     * @throws Exception cloudstack connector exception.
     */
    public String deployVirtualMachine(String serviceOfferingId,
            String templateId, String zoneId, String response, HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("deployVirtualMachine", optional);
        arguments.add(new NameValuePair("serviceofferingid", serviceOfferingId));
        arguments.add(new NameValuePair("templateid", templateId));
        arguments.add(new NameValuePair("zoneid", zoneId));
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }

    /**
     * Destroy the existing Virtual machine.
     *
     * @param virtualMachineId virtual machine id.
     * @param response response type.
     * @return json response.
     * @throws Exception cloud stack connector exception.
     */
    public String destroyVirtualMachine(String virtualMachineId, String response) throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("destroyVirtualMachine", null);
        arguments.add(new NameValuePair("id", virtualMachineId));
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }

    /**
     * Reboots a virtual machine.
     *
     * @param virtualMachineId The ID of the virtual machine.
     * @return json response.
     * @throws Exception connector exception.
     */
    public String rebootVirtualMachine(String virtualMachineId,String response) throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("rebootVirtualMachine",null);
        arguments.add(new NameValuePair("id", virtualMachineId));
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }

    /**
     * Starts a virtual machine.
     *
     * @param virtualMachineId The ID of the virtual machine.
     * @return response json.
     * @throws Exception
     */
    public String startVirtualMachine(String virtualMachineId,String response) throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("startVirtualMachine", null);
        arguments.add(new NameValuePair("id", virtualMachineId));
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }

    /**
     * event status of virtual machine.
     *
     * @param virtualMachineId The ID of the virtual machine
     * @param optional
     * @return json response.
     * @throws Exception
     */
    public String queryAsyncJobResult(String asychronousJobid, String response)
            throws Exception {
        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("queryAsyncJobResult", null);
        arguments.add(new NameValuePair("jobid", asychronousJobid));
        arguments.add(new NameValuePair("response", response));
        return  server.request(arguments);
    }

    /**
     * Stops a virtual machine.
     *
     * @param virtualMachineId The ID of the virtual machine
     * @param optional
     * @return json response.
     * @throws Exception
     */
    public String stopVirtualMachine(String virtualMachineId,String response,
            HashMap<String, String> optional)
            throws Exception {
    	optional.put("force","true");
        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("stopVirtualMachine", optional);
        arguments.add(new NameValuePair("id", virtualMachineId));
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }

    /**
     * Resets the password for virtual machine. The virtual machine must be in a "Stopped" state and the template must
     * already support this feature for this command to take effect. [async]
     *
     * @param virtualMachineId
     * @param optional
     * @return
     * @throws Exception
     */
    public String resetPasswordForVirtualMachine(String virtualMachineId)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("resetPasswordForVirtualMachine", null);
        arguments.add(new NameValuePair("id", virtualMachineId));
        return  server.request(arguments);
    }

    /**
     * Changes the service offering for a virtual machine. The virtual machine must be in a "Stopped" state for this
     * command to take effect.
     *
     * @param virtualMachineId The ID of the virtual machine
     * @param serviceOfferingId the service offering ID to apply to the virtual machine
     * @param optional
     * @return
     * @throws Exception
     */
    public String changeServiceForVirtualMachine(String virtualMachineId,
            String serviceOfferingId) throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("changeServiceForVirtualMachine", null);
        arguments.add(new NameValuePair("id", virtualMachineId));
        arguments.add(new NameValuePair("serviceofferingid", serviceOfferingId));
        return  server.request(arguments);
    }

    /**
     * Updates parameters of a virtual machine.
     *
     * @param virtualMachineId The ID of the virtual machine
     * @param optional
     * @return
     * @throws Exception
     */
    public String updateVirtualMachine(String virtualMachineId,
            HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("updateVirtualMachine", optional);
        arguments.add(new NameValuePair("id", virtualMachineId));
        return server.request(arguments);
    }

    /**
     * Recovers a virtual machine.
     *
     * @param virtualMachineId The ID of the virtual machine
     * @return
     * @throws Exception
     */
    public String recoverVirtualMachine(String virtualMachineId)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("recoverVirtualMachine", null);
        arguments.add(new NameValuePair("id", virtualMachineId));
        return  server.request(arguments);
    }

    /**
     * List the virtual machines owned by the account.
     *
     * @param optional
     * @return
     * @throws Exception
     */
    public String listVirtualMachines(String response,HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listVirtualMachines", optional);
        arguments.add(new NameValuePair("response", response));
        return server.request(arguments);
    }

    /**
     * Returns an encrypted password for the virtual machine
     *
     * @param virtualMachineId the ID of the virtual machine
     * @return
     * @throws Exception
     */
    public String getVMPassword(String virtualMachineId)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("getVMPassword", null);
        arguments.add(new NameValuePair("id", virtualMachineId));
        return server.request(arguments);
    }

    /**
     * attempts Migration of a Virtual Machine to a different host or Root volume of the Virtual Machine to a different
     * storage pool
     *
     * @param virtualMachineId the ID of the virtual machine
     * @param optional
     * @return
     * @throws Exception
     */
    public String migrateVirtualMachine(String virtualMachineId,
            HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("migrateVirtualMachine", optional);
        arguments.add(new NameValuePair("virtualmachineid", virtualMachineId));
        return server.request(arguments);
    }

    /**
     * Move a user virtual Machine to another user under same domain.
     *
     * @param newName Account name of the new virtual Machine owner.
     * @param newDomainId Domain id of the new virtual Machine owner.
     * @param virtualMachineId the virtual Machine ID of the user virtual machine to be moved
     * @param optional
     * @return
     * @throws Exception
     */
    public String assignVirtualMachine(String newName,
            String newDomainId, String virtualMachineId,
            HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("assignVirtualMachine", optional);
        arguments.add(new NameValuePair("account", newName));
        arguments.add(new NameValuePair("domainid", newDomainId));
        arguments.add(new NameValuePair("virtualmachineid", virtualMachineId));
        return server.request(arguments);
    }

    /**
     * Restore a Virtual Machine to original template or specific snapshot
     *
     * @param virtualMachineId Virtual Machine ID
     * @return
     * @throws Exception
     */
    public String restoreVirtualMachine(String virtualMachineId) throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("restoreVirtualMachine", null);
        arguments.add(new NameValuePair("virtualmachineid", virtualMachineId));
        return server.request(arguments);
    }

    /**
     * Retrieves the current status of asynchronous job for VirtualMachine.
     *
     * @param asychronousJobid the ID of the asychronous job
     * @return
     * @throws Exception
     */
    public String virtualMachineJobResult(String asychronousJobid)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("queryAsyncJobResult", null);
        arguments.add(new NameValuePair("jobid", asychronousJobid));
        return server.request(arguments);
    }


    /**
     * attempts Migration of a Virtual Machine to a different host or Root volume of the Virtual Machine to a different
     * storage pool
     *
     * @param virtualMachineId the ID of the virtual machine
     * @param optional
     * @return
     * @throws Exception
     */
    public String migrateVirtualMachineWithVolume(String hostId, String virtualMachineId,
            HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("migrateVirtualMachineWithVolume", optional);
        arguments.add(new NameValuePair("hostid", hostId));
        arguments.add(new NameValuePair("virtualmachineid", virtualMachineId));
        return server.request(arguments);
    }

    /**
     * cleans VM Reservations in database
     *
     * @param optional
     * @return
     * @throws Exception
     */
    public String cleanVMReservations()
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("cleanVMReservations", null);
        return server.request(arguments);
    }


    /**
     * Adds VM to specified network by creating a NIC
     *
     * @param networkId
     *
     * @param virtualMachineId the ID of the virtual machine
     * @param optional
     * @return
     * @throws Exception
     */
    public String addNicToVirtualMachine(String networkId, String virtualMachineId,
            HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("addNicToVirtualMachine", optional);
        arguments.add(new NameValuePair("networkid", networkId));
        arguments.add(new NameValuePair("virtualmachineid", virtualMachineId));
        return server.request(arguments);
    }


    /**
     * Removes VM from specified network by deleting a NIC
     *
     * @param nicId The ID of the network interface card
     * @param virtualmachineId The ID of the virtual machine
     * @param optional
     * @return
     * @throws Exception
     */
    public String removeNicFromVirtualMachine(String nicId,
            String virtualMachineId, HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("removeNicFromVirtualMachine", optional);
        arguments.add(new NameValuePair("nicid", nicId));
        arguments.add(new NameValuePair("virtualmachineid", virtualMachineId));
        return server.request(arguments);
    }



    /**
     * Changes the default NIC on a VM
     *
     * @param nicId The ID of the network interface card
     * @param virtualmachineId The ID of the virtual machine
     * @param optional
     * @return
     * @throws Exception
     */
    public String updateDefaultNicForVirtualMachine(String nicId,
            String virtualMachineId, HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("updateDefaultNicForVirtualMachine", optional);
        arguments.add(new NameValuePair("nicid", nicId));
        arguments.add(new NameValuePair("virtualmachineid", virtualMachineId));
        return server.request(arguments);
    }



}
