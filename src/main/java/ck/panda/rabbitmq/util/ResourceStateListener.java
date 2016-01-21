package ck.panda.rabbitmq.util;

import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import ck.panda.constants.EventTypes;
import ck.panda.domain.entity.Nic;
import ck.panda.domain.entity.PortForwarding;
import ck.panda.domain.entity.ResourceLimitDomain;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.VmInstance.Status;
import ck.panda.domain.entity.Volume;
import ck.panda.domain.entity.ResourceLimitDomain.ResourceType;
import ck.panda.domain.entity.Volume.VolumeType;
import ck.panda.service.ConvertEntityService;
import ck.panda.service.NetworkService;
import ck.panda.service.NicService;
import ck.panda.service.PortForwardingService;
import ck.panda.service.ResourceLimitDomainService;
import ck.panda.service.VirtualMachineService;
import ck.panda.service.VolumeService;
import ck.panda.util.CloudStackResourceCapacity;

/**
 * Resource State listener will listen and update resource status to our App DB when an event directly/from application
 * occurred in CS server.
 */
public class ResourceStateListener implements MessageListener {
    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceStateListener.class);

    /** Virtual machine service references to update . */
    private VirtualMachineService virtualmachineservice;

    /** Volume Service references to update. */
    private VolumeService volumeService;

    /** Nic service for listing nic. */
    private NicService nicService;

    /** Service reference to Port Forwarding. */
    private PortForwardingService portForwardingService;

    /** Network Service references to update. */
    private NetworkService networkService;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** Resource Limit Domain Service. */
    @Autowired
    private ResourceLimitDomainService resourceLimitDomainService;

    /** CloudStack Resource Capacity Service. */
    @Autowired
    private CloudStackResourceCapacity cloudStackResourceCapacity;

    /**
     * Inject convert entity service.
     *
     * @param convertEntityService convertEntityService object.
     */
    public ResourceStateListener(ConvertEntityService convertEntityService) {
        this.virtualmachineservice = convertEntityService.getInstanceService();
        this.volumeService = convertEntityService.getVolumeService();
        this.nicService = convertEntityService.getNicService();
        this.portForwardingService = convertEntityService.getPortForwardingService();
        this.networkService = convertEntityService.getNetworkService();
    }

    @Override
    public void onMessage(Message message) {
        try {
            this.handleResourceEvent(new String(message.getBody()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Status of resources are handling and update status message to our App DB.
     *
     * @param eventObject json object.
     * @throws Exception exception.
     */
    public void handleResourceEvent(String eventObject) throws Exception {
        handleVmEvent(eventObject);
    }

    /**
     * Handling VM events and updated those in our application DB according to the type of events.
     *
     * @param event event String.
     * @throws Exception exception.
     */
    private void handleVmEvent(String event) throws Exception {
        LOGGER.info("VM event message", event);
        JSONObject resourceEvent = new JSONObject(event);
        if (resourceEvent != null && !event.trim().isEmpty()) {
            switch (resourceEvent.getString("resource")) {
            case "VirtualMachine":
                if (resourceEvent.has("id")) {
                    LOGGER.info("VM event UUID", resourceEvent.getString("id"));
                    VmInstance vmInstance = virtualmachineservice.findByUUID(resourceEvent.getString("id"));
                    if (vmInstance != null) {
                        if (resourceEvent.getString(EventTypes.RESOURCE_STATE).equals("Error")) {
                            vmInstance.setStatus(Status.valueOf(resourceEvent.getString(EventTypes.RESOURCE_STATE)));
                            vmInstance.setEventMessage(resourceEvent.getString(EventTypes.RESOURCE_STATE) + "occured");
                        }
                        LOGGER.info("VM event message", resourceEvent);
                        if (resourceEvent != null) {
                            vmInstance.setStatus(Status.valueOf(resourceEvent.getString(EventTypes.RESOURCE_STATE)));
                            vmInstance.setEventMessage("");
                        }
                        vmInstance.setSyncFlag(false);
                        virtualmachineservice.update(vmInstance);

                        // Detach the instance from volume
                        if (resourceEvent.getString(EventTypes.RESOURCE_STATE).equals("Expunging")) {
                            List<Volume> volumeList = volumeService.findByInstanceForResourceState(vmInstance.getId());
                            for (Volume volume : volumeList) {
                                if (volume.getVolumeType().equals(VolumeType.DATADISK)) {
                                    volume.setVmInstanceId(null);
                                    volume.setIsSyncFlag(false);
                                    volumeService.update(volume);
                                }
                            }
                            List<Nic> nicList = nicService.findByInstance(vmInstance.getId());
                            for (Nic nic : nicList) {
                                nic.setIsActive(false);
                                nic.setSyncFlag(false);
                                nicService.updatebyResourceState(nic);
                            }
                            List<PortForwarding> portForwardingList = portForwardingService.findByInstance(vmInstance.getId());
                            for (PortForwarding portForwarding : portForwardingList) {
                                portForwarding.setIsActive(false);
                                portForwarding.setSyncFlag(false);
                                portForwardingService.update(portForwarding);
                            }

                            // Resource count for domain
                            HashMap<String, String> optional = new HashMap<String, String>();
                            if (vmInstance.getProjectId() != null) {
                                optional.put("projectid",
                                        convertEntityService.getProjectById(vmInstance.getProjectId()).getUuid());
                            } else {
                                optional.put("account",
                                        convertEntityService.getDepartmentUsernameById(vmInstance.getDepartmentId()));
                            }
                            String csResponse = cloudStackResourceCapacity.updateResourceCount(vmInstance.getDomain().getUuid(), optional, "json");
                            JSONArray resourceCountArrayJSON = null;
                            JSONObject csCountJson = new JSONObject(csResponse).getJSONObject("updateresourcecountresponse");
                            if (csCountJson.has("resourcecount")) {
                                resourceCountArrayJSON = csCountJson.getJSONArray("resourcecount");
                                for (int i = 0, size = resourceCountArrayJSON.length(); i < size; i++) {
                                    String resourceCount = resourceCountArrayJSON.getJSONObject(i).getString("resourcecount");
                                    String resourceType = resourceCountArrayJSON.getJSONObject(i).getString("resourcetype");
                                    if (!resourceType.equals("5")) {
                                        HashMap<String, String> resourceMap = updateResourceCount(resourceType);
                                        if (resourceMap != null) {
                                            ResourceLimitDomain resourceDomainCount = resourceLimitDomainService
                                                    .findByDomainAndResourceCount(vmInstance.getDomainId(),
                                                            ResourceType.valueOf(resourceMap.get(resourceType)), true);
                                            resourceDomainCount.setUsedLimit(Long.valueOf(resourceCount));
                                            resourceDomainCount.setIsSyncFlag(true);
                                            resourceLimitDomainService.update(resourceDomainCount);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            case "Volume":
                if (resourceEvent.has("id") && resourceEvent.getString(EventTypes.RESOURCE_STATE).equals("Expunged")) {
                    Volume volume = volumeService.findByUUID(resourceEvent.getString("id"));
                    volume.setStatus(volume.getStatus().valueOf(resourceEvent.getString(EventTypes.RESOURCE_STATE).toUpperCase()));
                    volume.setIsActive(false);
                    volume.setIsSyncFlag(false);
                    volumeService.update(volume);
                }
                break;
            case "Network":
                break;
            default:
                LOGGER.info("VM event message", event);
                break;
            }
        }
    }

    /**
     * Update and map the resource count current resource type.
     *
     * @param resourceType resource type of domain resource.
     * @return resource
     */
    private HashMap<String, String> updateResourceCount(String resourceType) {
        HashMap<String, String> resourceMap = new HashMap<>();
        resourceMap.put("0", String.valueOf(ResourceType.Instance));
        resourceMap.put("1", String.valueOf(ResourceType.IP));
        resourceMap.put("2", String.valueOf(ResourceType.Volume));
        resourceMap.put("3", String.valueOf(ResourceType.Snapshot));
        resourceMap.put("4", String.valueOf(ResourceType.Template));
        resourceMap.put("6", String.valueOf(ResourceType.Network));
        resourceMap.put("7", String.valueOf(ResourceType.VPC));
        resourceMap.put("8", String.valueOf(ResourceType.CPU));
        resourceMap.put("9", String.valueOf(ResourceType.Memory));
        resourceMap.put("10", String.valueOf(ResourceType.PrimaryStorage));
        resourceMap.put("11", String.valueOf(ResourceType.SecondaryStorage));
        return resourceMap;
    }
}
