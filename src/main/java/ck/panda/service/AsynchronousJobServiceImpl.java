package ck.panda.service;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ck.panda.constants.EventTypes;
import ck.panda.domain.entity.CloudStackConfiguration;
import ck.panda.domain.entity.Network;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.Volume;
import ck.panda.domain.repository.jpa.VolumeRepository;
import ck.panda.util.CloudStackInstanceService;
import ck.panda.util.CloudStackServer;
import ck.panda.util.error.exception.ApplicationException;

/**
 * Synchronization of all the asynchronous data from the cloudStack.
 *
 */
@Service
public class AsynchronousJobServiceImpl implements AsynchronousJobService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncServiceImpl.class);

    /** Virtual machine Service for listing vms. */
    @Autowired
    private VirtualMachineService virtualMachineService;

    /** VolumeRepository repository reference. */
    @Autowired
    private VolumeRepository volumeRepo;

    /** NetworkOfferingService for listing network offers in cloudstack server. */
    @Autowired
    private NetworkService networkService;

    /** Volume Service for listing volumes. */
    @Autowired
    private VolumeService volumeService;

    /** CloudStack connector. */
    @Autowired
    private CloudStackServer server;

    /** CloudStack configuration . */
    @Autowired
    private CloudStackConfigurationService cloudConfigService;

    /** Secret key value is append. */
    @Value(value = "${aes.salt.secretKey}")
    private String secretKey;

    /** CloudStack connector reference for instance. */
    @Autowired
    private CloudStackInstanceService cloudStackInstanceService;

    /**
     * Sync status of Asynchronous resource with Cloud Server.
     *
     * @param Object response object.
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    @Override
    public void syncResourceStatus(JSONObject eventObject) throws Exception {
        CloudStackConfiguration cloudConfig = cloudConfigService.find(1L);
        server.setServer(cloudConfig.getApiURL(), cloudConfig.getSecretKey(), cloudConfig.getApiKey());
        cloudStackInstanceService.setServer(server);
        String eventObjectResult = cloudStackInstanceService.queryAsyncJobResult(eventObject.getString("jobId"), "json");
        JSONObject jobresult = new JSONObject(eventObjectResult).getJSONObject("queryasyncjobresultresponse")
                .getJSONObject("jobresult");
        String commandText = null;
        if (eventObject.getString("commandEventType").contains(".")) {
            commandText = eventObject.getString("commandEventType").substring(0, eventObject.getString("commandEventType").indexOf('.', 0)) + ".";
        } else {
            commandText = eventObject.getString("commandEventType");
        }

        switch (commandText) {
        case EventTypes.EVENT_VM:
            LOGGER.debug("VM Sync", eventObject.getString("jobId") + "===" + eventObject.getString("commandEventType"));
            syncvirtualmachine(jobresult, eventObject.getString("commandEventType"));
            break;
        case EventTypes.EVENT_NETWORK:
            if (!eventObject.getString("commandEventType").contains("OFFERING")) {
                LOGGER.debug("Network sync", eventObject.getString("jobId") + "===" + eventObject.getString("commandEventType"));
                syncNetwork(jobresult, eventObject.getString("commandEventType"), eventObject);
            }
            break;
        default:
            LOGGER.debug("No sync required", eventObject.getString("jobId") + "===" + eventObject.getString("commandEventType"));
        }
    }

    /**
     * @param jobresult job result
     * @param commandEventType type of action
     * @throws Exception cloudstack unhandled errors
     */
    public void syncvirtualmachine(JSONObject jobresult, String commandEventType) throws Exception {

        if (jobresult.has("virtualmachine")) {
            VmInstance vmInstance = VmInstance.convert(jobresult.getJSONObject("virtualmachine"));
            VmInstance instance = virtualMachineService.findByUUID(vmInstance.getUuid());
            instance.setSyncFlag(false);
            // 3.1 Find the corresponding CS server vm object by finding it in a
            // hash using uuid
            if (vmInstance.getUuid().equals(instance.getUuid())) {
                VmInstance csVm = vmInstance;
                instance.setName(csVm.getName());
                instance.setCpuCore(csVm.getCpuCore());
                instance.setDomainId(csVm.getDomainId());
                instance.setStatus(csVm.getStatus());
                instance.setZoneId(csVm.getZoneId());
                instance.setHostId(csVm.getHostId());
                instance.setPodId(csVm.getPodId());
                instance.setComputeOfferingId(csVm.getComputeOfferingId());
                instance.setCpuSpeed(csVm.getCpuSpeed());
                instance.setMemory(csVm.getMemory());
                instance.setCpuUsage(csVm.getCpuUsage());
                instance.setPasswordEnabled(csVm.getPasswordEnabled());
                instance.setPassword(csVm.getPassword());
                instance.setIso(csVm.getIso());
                instance.setIsoName(csVm.getIsoName());
                instance.setIpAddress(csVm.getIpAddress());
                instance.setNetworkId(csVm.getNetworkId());
                LOGGER.debug("sync VM for ASYNC");
                // VNC password set.
                if (csVm.getPassword() != null) {
                    String strEncoded = Base64.getEncoder().encodeToString(secretKey.getBytes("utf-8"));
                    byte[] decodedKey = Base64.getDecoder().decode(strEncoded);
                    SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
                    String encryptedPassword = new String(EncryptionUtil.encrypt(csVm.getPassword(), originalKey));
                    LOGGER.debug("sync VM for pass" + encryptedPassword);
                    instance.setVncPassword(encryptedPassword);
                }
                // 3.2 If found, update the vm object in app db
                virtualMachineService.update(instance);
                syncVolume();
            }
        }
    }

    /**
     * Sync with CloudStack server Network.
     *
     * @param jobresult job result
     * @param commandEventType action type
     * @param eventObject network event object
     * @throws ApplicationException unhandled application errors
     * @throws Exception cloudstack unhandled errors
     */
    public void syncNetwork(JSONObject jobresult, String commandEventType, JSONObject eventObject) throws ApplicationException, Exception {

        if (commandEventType.equals("NETWORK.UPDATE")) {
            Network csNetwork = Network.convert(jobresult.getJSONObject("network"));
            Network network = networkService.findByUUID(csNetwork.getUuid());
            network.setSyncFlag(false);
            if (csNetwork.getUuid().equals(network.getUuid())) {
                Network csNet = csNetwork;
                network.setName(csNet.getName());
                network.setDomainId(csNet.getDomainId());
                network.setZoneId(csNet.getZoneId());
                network.setDisplayText(csNet.getDisplayText());
                networkService.update(network);
            }
        }
        if (commandEventType.equals("NETWORK.DELETE")) {
            JSONObject json = new JSONObject(eventObject.getString("cmdInfo"));
            Network network = networkService.findByUUID(json.getString("id"));
            network.setSyncFlag(false);
            networkService.softDelete(network);
        }
    }

    /**
     * Sync with Cloud Server Volume.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    public void syncVolume() throws ApplicationException, Exception {

        // 1. Get all the StorageOffering objects from CS server as hash
        List<Volume> volumeList = volumeService.findAllFromCSServer();
        HashMap<String, Volume> csVolumeMap = (HashMap<String, Volume>) Volume.convert(volumeList);

        // 2. Get all the osType objects from application
        List<Volume> appvolumeServiceList = volumeService.findAll();

        // 3. Iterate application osType list
        for (Volume volume : appvolumeServiceList) {
            volume.setIsSyncFlag(false);
            // 3.1 Find the corresponding CS server osType object by finding it
            // in a hash using uuid
            if (csVolumeMap.containsKey(volume.getUuid())) {
                Volume csvolume = csVolumeMap.get(volume.getUuid());
                volume.setName(csvolume.getName());
                volume.setStorageOfferingId(csvolume.getStorageOfferingId());
                volume.setZoneId(csvolume.getZoneId());
                volume.setDomainId(csvolume.getDomainId());
                volume.setDepartmentId(csvolume.getDepartmentId());
                volume.setVmInstanceId(csvolume.getVmInstanceId());
                volume.setVolumeType(csvolume.getVolumeType());
                volume.setIsActive(true);
                if (volume.getDiskSize() != null) {
                    volume.setDiskSize(csvolume.getDiskSize());
                } else {
                    volume.setDiskSize(csvolume.getStorageOffering().getDiskSize());
                }
                if (volume.getProjectId() != null) {
                    volume.setProjectId(csvolume.getProjectId());
                }
                volume.setChecksum(csvolume.getChecksum());
                volume.setStatus(csvolume.getStatus());
                volume.setDiskMaxIops(csvolume.getDiskMaxIops());
                volume.setDiskMinIops(csvolume.getDiskMinIops());
                volume.setCreatedDateTime(csvolume.getCreatedDateTime());
                volume.setUpdatedDateTime(csvolume.getUpdatedDateTime());
                // 3.2 If found, update the osType object in app db
                volumeService.update(volume);

                // 3.3 Remove once updated, so that we can have the list of cs
                // osType which is not added in the app
                csVolumeMap.remove(volume.getUuid());
            } else {
                volume.setIsActive(false);
                volume.setStatus(Volume.Status.DESTROY);
                volumeRepo.save(volume);
            }
        }
        // 4. Get the remaining list of cs server hash osType object, then
        // iterate and
        // add it to app db
        for (String key : csVolumeMap.keySet()) {

            volumeService.save(csVolumeMap.get(key));
        }
    }
}