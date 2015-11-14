/**
 *
 */
package ck.panda.util;

import java.util.HashMap;
import java.util.LinkedList;

import org.apache.commons.httpclient.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Cloud stack volume service.
 */
@Service
public class CloudStackVolumeService {

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
     * Creates a disk volume from a disk offering. This disk volume must still be attached to a virtual machine to make
     * use of it.
     *
     * @param diskVolumeName The name of the disk volume
     * @param zone zone
     * @param diskOffering storage offering
     * @param response json
     * @param optional optional values for creating volumes
     * @return created response
     * @throws Exception error
     */
    public String createVolume(String diskVolumeName, String diskOffering, String zone, String response, HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("createVolume", optional);
        arguments.add(new NameValuePair("name", diskVolumeName));
        arguments.add(new NameValuePair("diskofferingid", diskOffering));
        arguments.add(new NameValuePair("zoneid", zone));
        arguments.add(new NameValuePair("response", response));

        String createResponse = server.request(arguments);

        return createResponse;
    }


    /**
     * Deletes a detached disk volume.
     *
     * @param diskVolumeId The ID of the disk volume
     * @return delete response
     * @throws Exception error
     */
    public String deleteVolume(String diskVolumeId)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("deleteVolume", null);
        arguments.add(new NameValuePair("id", diskVolumeId));

        String deleteResponse = server.request(arguments);

        return deleteResponse;

    }


    /**
     * Lists all volumes.
     *
     * @param optional optional values
     * @param response json
     * @return list response.
     * @throws Exception error
     */
    public String listVolumes(String response, HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("listVolumes", optional);
        arguments.add(new NameValuePair("response", response));

        String listResponse = server.request(arguments);

        return listResponse;
    }

    /**
     * Retrieves the current status of asynchronous job for volume.
     *
     * @param asychronousJobid the ID of the asychronous job
     * @param response json
     * @return job response
     * @throws Exception error
     */
    public String volumeJobResult(String asychronousJobid, String response)
            throws Exception {

        LinkedList<NameValuePair> arguments
                = server.getDefaultQuery("queryAsyncJobResult", null);
        arguments.add(new NameValuePair("jobid", asychronousJobid));
        arguments.add(new NameValuePair("response", response));
        String jobResponse = server.request(arguments);

        return jobResponse;
    }

}
