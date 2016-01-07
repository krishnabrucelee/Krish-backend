package ck.panda.rabbitmq.util;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import ck.panda.constants.EventTypes;
import ck.panda.domain.entity.Network;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.VmInstance.Status;
import ck.panda.domain.entity.Volume;
import ck.panda.service.ConvertEntityService;
import ck.panda.service.NetworkService;
import ck.panda.service.VirtualMachineService;
import ck.panda.service.VolumeService;

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

    /** Network Service references to update. */
    private NetworkService networkService;

    /**
     * Inject convert entity service.
     *
     * @param convertEntityService convertEntityService object.
     */
    public ResourceStateListener(ConvertEntityService convertEntityService) {
        this.virtualmachineservice = convertEntityService.getInstanceService();
        this.volumeService = convertEntityService.getVolumeService();
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
                    }
                }
                break;
            case "Volume":
                if (resourceEvent.has("id")) {
                    Volume volume = volumeService.findByUUID(resourceEvent.getString("id"));
                    volume.setStatus(volume.getStatus().valueOf(resourceEvent.getString(EventTypes.RESOURCE_STATE).toUpperCase()));
                    volume.setIsSyncFlag(false);
                    volumeService.update(volume);
                }
                break;
            case "Network":
                if (resourceEvent.has("id")) {
                    Network network = networkService.findByUUID(resourceEvent.getString("id"));
                    network.setStatus(network.getStatus().valueOf(resourceEvent.getString(EventTypes.RESOURCE_STATE)));
                    network.setSyncFlag(false);
                    networkService.update(network);
                }
                break;
            default:
                LOGGER.info("VM event message", event);
                break;
            }

        }
    }
}
