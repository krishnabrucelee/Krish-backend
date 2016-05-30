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
    public String createVolume(String diskVolumeName, String zone, String response,
            HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("createVolume", optional);
        arguments.add(new NameValuePair("name", diskVolumeName));
        arguments.add(new NameValuePair("zoneid", zone));
        arguments.add(new NameValuePair("response", response));

        String createResponse = server.request(arguments);

        return createResponse;
    }

    /**
     * Deletes a detached disk volume.
     *
     * @param diskVolumeId The ID of the disk volume
     * @param response json
     * @return delete response
     * @throws Exception error
     */
    public String deleteVolume(String diskVolumeId, String response) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("deleteVolume", null);
        arguments.add(new NameValuePair("id", diskVolumeId));
        arguments.add(new NameValuePair("response", response));

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
    public String listVolumes(String response, HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("listVolumes", optional);
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
    public String volumeJobResult(String asychronousJobid, String response) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("queryAsyncJobResult", null);
        arguments.add(new NameValuePair("jobid", asychronousJobid));
        arguments.add(new NameValuePair("response", response));
        String jobResponse = server.request(arguments);

        return jobResponse;
    }

    /**
     * Attaches a disk volume to a virtual machine.
     *
     * @param diskVolumeId The ID of the disk volume
     * @param optional optional
     * @param response response
     * @return response
     * @throws Exception Exception
     */
    public String attachVolume(String diskVolumeId, String response, HashMap<String, String> optional)
            throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("attachVolume", optional);
        arguments.add(new NameValuePair("id", diskVolumeId));
        arguments.add(new NameValuePair("response", response));

        String responseDocument = server.request(arguments);

        return responseDocument;
    }

    /**
     * Detaches a disk volume from a virtual machine.
     *
     * @param optional option
     * @param response response
     * @return response
     * @throws Exception Exception
     */
    public String detachVolume(String response, HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("detachVolume", optional);
        arguments.add(new NameValuePair("response", response));
        String responseDocument = server.request(arguments);

        return responseDocument;
    }

    /**
     * Resize a disk volume from a virtual machine.
     *
     * @param optional optional values
     * @param volumeId volume id
     * @param diskOfferingId disk offering id
     * @param response json response
     * @return resize response
     * @throws Exception error
     */
    public String resizeVolume(String volumeId, String response,
            HashMap<String, String> optional) throws Exception {
        LinkedList<NameValuePair> arguments = server.getDefaultQuery("resizeVolume", optional);
        arguments.add(new NameValuePair("id", volumeId));
        arguments.add(new NameValuePair("response", response));
        String resizeResponse = server.request(arguments);

        return resizeResponse;

    }

    /**
     * Uploads the Volume.
     *
     * @param name The name of the disk volume
     * @param url The url path of the volume
     * @param format The format of the volume
     * @param zoneId The zone for the volume to upload it
     * @param optional optional values
     * @param response json response
     * @return uploadResponse uploaded volume response
     * @throws Exception error
     */
    public String uploadVolume(String name, String format, String zoneId, String url, String response,
            HashMap<String, String> optional) throws Exception {

        LinkedList<NameValuePair> arguments = server.getDefaultQuery("uploadVolume", optional);
        arguments.add(new NameValuePair("name", name));
        arguments.add(new NameValuePair("url", url));
        arguments.add(new NameValuePair("format", format));
        arguments.add(new NameValuePair("zoneId", zoneId));
        arguments.add(new NameValuePair("response", response));

        String uploadResponse = server.request(arguments);

        return uploadResponse;
    }

}
