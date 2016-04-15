package ck.panda.rabbitmq.util;

import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import ck.panda.constants.CloudStackConstants;
import ck.panda.constants.EventTypes;
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.CloudStackConfiguration;
import ck.panda.domain.entity.Nic;
import ck.panda.domain.entity.PortForwarding;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.VmSnapshot;
import ck.panda.domain.entity.VmInstance.Status;
import ck.panda.domain.entity.Volume;
import ck.panda.domain.entity.Department.AccountType;
import ck.panda.domain.entity.Event;
import ck.panda.domain.entity.Event.EventType;
import ck.panda.domain.entity.Volume.VolumeType;
import ck.panda.service.CloudStackConfigurationService;
import ck.panda.service.ConvertEntityService;
import ck.panda.service.NetworkService;
import ck.panda.service.NicService;
import ck.panda.service.PortForwardingService;
import ck.panda.service.SyncService;
import ck.panda.service.UpdateResourceCountService;
import ck.panda.service.VirtualMachineService;
import ck.panda.service.VmSnapshotService;
import ck.panda.service.VolumeService;
import ck.panda.util.CloudStackInstanceService;
import ck.panda.util.CloudStackResourceCapacity;
import ck.panda.util.CloudStackServer;

/**
 * Resource State listener will listen and update resource status to our App DB
 * when an event directly/from application occurred in CS server.
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

    /** CloudStack connector reference for instance. */
    private CloudStackInstanceService cloudStackInstanceService;

    /** CloudStack connector. */
    private CloudStackServer server;

    /** CloudStack configuration . */
    private CloudStackConfigurationService cloudConfigService;

    /** Reference of the convert entity service. */
    private ConvertEntityService convertEntityService;

    /** CloudStack Resource Capacity Service. */
    private CloudStackResourceCapacity cloudStackResourceCapacity;

    /** sync service reference. */
    private SyncService sync;

    /** Update Resource Count service reference. */
    private UpdateResourceCountService updateResourceCountService;

    private VmSnapshotService vmSnapshotService;

    private int count = 0;

    /**
     * Inject convert entity service.
     *
     * @param convertEntityService
     *            convertEntityService object.
     * @param sync
     *            Service for sync.
     */
    public ResourceStateListener(ConvertEntityService convertEntityService, SyncService sync) {
        this.convertEntityService = convertEntityService;
        this.virtualmachineservice = convertEntityService.getInstanceService();
        this.volumeService = convertEntityService.getVolumeService();
        this.nicService = convertEntityService.getNicService();
        this.portForwardingService = convertEntityService.getPortForwardingService();
        this.networkService = convertEntityService.getNetworkService();
        this.vmSnapshotService = convertEntityService.getVmSnapshotService();
        this.sync = sync;
        this.cloudStackInstanceService = convertEntityService.getCSInstanceService();
        this.server = convertEntityService.getCSConnecter();
        this.cloudConfigService = convertEntityService.getCSConfig();
        this.cloudStackResourceCapacity = convertEntityService.getCloudStackResourceCapacityService();
        this.updateResourceCountService = convertEntityService.getUpdateResourceCountService();
    }

    @Override
    public void onMessage(Message message) {
        try {
            this.handleResourceEvent(new String(message.getBody()), message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Status of resources are handling and update status message to our App DB.
     *
     * @param eventObject
     *            json object.
     * @throws Exception
     *             exception.
     */
    public void handleResourceEvent(String eventObject, Message message) throws Exception {
        handleVmEvent(eventObject, message);
    }

    /**
     * Handling VM events and updated those in our application DB according to the type of events.
     *
     * @param event event String.
     * @throws Exception exception.
     */
    private void handleVmEvent(String event, Message message) throws Exception {
        LOGGER.info("VM event message", event);
        JSONObject resourceEvent = new JSONObject(event);
        if (resourceEvent != null && !event.trim().isEmpty()) { // Event record for resource state changed.
            Event resourceStateEvent = new Event();
            String routingKey = message.getMessageProperties().getReceivedRoutingKey();
            String[] resourceTypes = routingKey.split("\\.");
            if (resourceEvent.has(CloudStackConstants.CS_ID)) {
                resourceStateEvent.setEventDateTime(convertEntityService.getTimeService()
                        .convertDateAndTime(resourceEvent.getString(CloudStackConstants.CS_EVENT_DATE_TIME)));
                resourceStateEvent.setEventType(EventType.RESOURCESTATE);
                resourceStateEvent.setResourceUuid(resourceEvent.getString(CloudStackConstants.CS_ID));
                resourceStateEvent.setMessage(resourceEvent.getString(EventTypes.RESOURCE_STATE));
                resourceStateEvent.setStatus(Event.Status.COMPLETED);
                if (resourceTypes.length > 1) {
                    resourceStateEvent.setEvent(resourceTypes[3]);
                }
                // Save the event get from action listener.
                convertEntityService.getWebsocketService().handleEventAction(resourceStateEvent, resourceEvent);
            }
        }
    }
}
