package ck.panda.util;

import java.util.HashMap;
import java.util.LinkedList;
import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * CloudStack Snapshot service for taking snapshots in CloudStack server.
 *
 */
@Service
public class CloudStackSnapshotService {

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
     * Creates an instant snapshot of a volume.
     *
     * @param diskVolumeId The ID of the disk volume
     * @param optional from cloudstack.
     * @param response json or xml
     * @return response Document.
     * @throws Exception unhandled errors.
     */
    public String createSnapshot(String diskVolumeId, HashMap<String, String> optional, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("createSnapshot", optional);
        arguments.add(new NameValuePair("volumeid", diskVolumeId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Lists all available snapshots for the account.
     *
     * @param optional values from cloudstack
     * @param response json or xml
     * @return response Document.
     * @throws Exception unhandled errors.
     */
    public String listSnapshots(HashMap<String, String> optional, String response) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("listSnapshots", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Deletes a snapshot of a disk volume.
     *
     * @param snapshotId The ID of the snapshot
     * @param response json or xml
     * @return response Document.
     * @throws Exception unhandled errors.
     */
    public String deleteSnapshot(String response, String snapshotId) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("deleteSnapshot", null);
        arguments.add(new NameValuePair("id", snapshotId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Creates a snapshot policy for the account.
     *
     * @param snapshotPolicyIntervalType valid values are HOURLY, DAILY, WEEKLY, and MONTHLY
     * @param snapshotPolicyMaxSnaps maximum number of snapshots to retain
     * @param snapshotPolicySchedule time the snapshot is scheduled to be taken. Format is:* if HOURLY, MM* if DAILY,
     *            MM:HH* if WEEKLY, MM:HH:DD (1-7)* if MONTHLY, MM:HH:DD (1-28)
     * @param snapshotPolicyTimeZone Specifies a timezone for this command. For more information on the timezone
     *            parameter, see Time Zone Format.
     * @param diskvolumeId the ID of the disk volume
     * @return response Document.
     * @throws Exception unhandled errors.
     */
    public String createSnapshotPolicy(String snapshotPolicyIntervalType, String snapshotPolicyMaxSnaps,
             String snapshotPolicyTimeZone, String diskvolumeId, String response, HashMap<String, String> optional) throws Exception {
        LinkedList<NameValuePair> arguments = server.getDefaultQuery("createSnapshotPolicy", optional);
        arguments.add(new NameValuePair("intervaltype", snapshotPolicyIntervalType));
        arguments.add(new NameValuePair("maxsnaps", snapshotPolicyMaxSnaps));
        arguments.add(new NameValuePair("timezone", snapshotPolicyTimeZone));
        arguments.add(new NameValuePair("volumeid", diskvolumeId));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Deletes snapshot policies for the account.
     *
     * @param optional values from cloudstack.
     * @return response Document.
     * @throws Exception unhandled errors.
     */
    public String deleteSnapshotPolicies(String snapshotPolicyUuid, String response) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("deleteSnapshotPolicies", null);
        arguments.add(new NameValuePair("id", snapshotPolicyUuid));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Lists snapshot policies.
     *
     * @param diskvolumeId the ID of the disk volume
     * @param optional values from cloud Stack.
     * @return response Document.
     * @throws Exception unhandled errors.
     */
    public String listSnapshotPolicies(String diskvolumeId, HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("listSnapshotPolicies", optional);

        arguments.add(new NameValuePair("volumeid", diskvolumeId));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Retrieves the current status of asynchronous job for snapshot.
     *
     * @param asychronousJobid the ID of the asychronous job
     * @return response Document.
     * @param response json or xml.
     * @throws Exception unhandled errors.
     */
    public String snapshotJobResult(String asychronousJobid, String response) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("queryAsyncJobResult", null);
        arguments.add(new NameValuePair("jobid", asychronousJobid));
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Creates snapshot for a vm.
     *
     * @param virtualmachineid for an instance.
     * @param optional values.
     * @return response Document.
     * @throws Exception unhandled errors.
     */
    public String createVMSnapshot(String virtualmachineid, HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("createVMSnapshot", optional);
        arguments.add(new NameValuePair("virtualmachineid", virtualmachineid));
        arguments.add(new NameValuePair("response", "json"));
        String responseDocument = server.request(arguments);

        return responseDocument;
    }

    /**
     * List virtual machine snapshot by conditions.
     *
     * @param optional values from cloudstack.
     * @return response Doucment.
     * @throws Exception unhandled errors.
     */
    public String listVMSnapshot(HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("listVMSnapshot", optional);
        arguments.add(new NameValuePair("response", "json"));
        String responseDocument = server.request(arguments);

        return responseDocument;
    }

    /**
     * Deletes a vmsnapshot.
     *
     * @param vmsnapshotid for vm snapshot.
     * @return response Document.
     * @throws Exception unhandled errors.
     */
    public String deleteVMSnapshot(String vmsnapshotid) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("deleteVMSnapshot", null);
        arguments.add(new NameValuePair("vmsnapshotid", vmsnapshotid));
        arguments.add(new NameValuePair("response", "json"));
        String responseDocument = server.request(arguments);

        return responseDocument;
    }

    /**
     * Revert VM from a vmsnapshot.
     *
     * @param vmsnapshotid for vmsnapshot.
     * @return response.
     * @throws Exception unhandled errors.
     */
    public String revertToVMSnapshot(String vmsnapshotid) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("revertToVMSnapshot", null);
        arguments.add(new NameValuePair("vmsnapshotid", vmsnapshotid));
        arguments.add(new NameValuePair("response", "json"));
        String responseDocument = server.request(arguments);

        return responseDocument;
    }

    /**
     * Revert VM from a vmsnapshot.
     *
     * @param vmsnapshotid for vmsnapshot.
     * @return response.
     * @throws Exception unhandled errors.
     */
    public String revertSnapshot(String snapshotid, String response) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("revertSnapshot", null);
        arguments.add(new NameValuePair("id", snapshotid));
        arguments.add(new NameValuePair("response", "json"));
        String responseDocument = server.request(arguments);
        return responseDocument;
    }

    /**
     * Retrieves the current status of asynchronous job for VM snapshot.
     *
     * @param asychronousJobid Asynchronous job response from cloudstack.
     * @return response
     * @throws Exception unhandled errors.
     */
    public String vmSnapshotJobResult(String asychronousJobid) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("queryAsyncJobResult", null);
        arguments.add(new NameValuePair("jobid", asychronousJobid));
        arguments.add(new NameValuePair("response", "json"));
        String responseDocument = server.request(arguments);

        return responseDocument;
    }
}
