package ck.panda.rabbitmq.util;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import ck.panda.constants.EventTypes;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.VmInstance.Status;
import ck.panda.service.VirtualMachineService;

/**
 * Resource State listener will listen and update resource status to our App DB when an event directly/from
 * application occurred in CS server.
 */
public class ResourceStateListener implements MessageListener {
    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceStateListener.class);

    /** Virtual machine service references to update . */
    private VirtualMachineService virtualmachineservice;

    /**
     * Inject virtual machine service.
     *
     * @param virtualmachineservice virtualmachineservice object.
     */
    public ResourceStateListener(VirtualMachineService virtualmachineservice) {
        this.virtualmachineservice = virtualmachineservice;
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
        JSONObject instance = new JSONObject(event);
        if (instance != null && !event.trim().isEmpty()) {
            if (instance.has("id") && instance.getString("id") != null) {
                LOGGER.info("VM event UUID", instance.getString("id"));
                VmInstance vmInstance = virtualmachineservice.findByUUID(instance.getString("id"));
                if (vmInstance != null) {
                    if (instance.getString(EventTypes.RESOURCE_STATE).equals("Error")) {
                        vmInstance.setStatus(Status.valueOf(instance.getString(EventTypes.RESOURCE_STATE)));
                        vmInstance.setEventMessage(instance.getString(EventTypes.RESOURCE_STATE) + "occured");
                    }
                    LOGGER.info("VM event message", instance);
                    if (instance != null) {
                        vmInstance.setStatus(Status.valueOf(instance.getString(EventTypes.RESOURCE_STATE)));
                        vmInstance.setEventMessage("");
                    }
                    virtualmachineservice.update(vmInstance);
                }
            }
        }
    }
}
