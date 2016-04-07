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
     * Handling VM events and updated those in our application DB according to
     * the type of events.
     *
     * @param event
     *            event String.
     * @throws Exception
     *             exception.
     */
    private void handleVmEvent(String event, Message message) throws Exception {
        LOGGER.info("VM event message", event);
        JSONObject resourceEvent = new JSONObject(event);
        if (resourceEvent != null && !event.trim().isEmpty()) { // Event record
                                                                // for resource
                                                                // state
                                                                // changed.
            Event resourceStateEvent = new Event();
            if (resourceEvent.has(CloudStackConstants.CS_ID)) {
                resourceStateEvent.setEventDateTime(convertEntityService.getTimeService()
                        .convertDateAndTime(resourceEvent.getString(CloudStackConstants.CS_EVENT_DATE_TIME)));
                resourceStateEvent.setEventType(EventType.RESOURCESTATE);
                resourceStateEvent.setResourceUuid(resourceEvent.getString(CloudStackConstants.CS_ID));
                resourceStateEvent.setMessage(resourceEvent.getString(EventTypes.RESOURCE_STATE));
                resourceStateEvent.setStatus(Event.Status.COMPLETED);
                // save the event get from action listener.
                convertEntityService.getWebsocketService().handleEventAction(resourceStateEvent);
            }
            if (resourceEvent.has("resource")) {
                switch (resourceEvent.getString("resource")) {
                case "VirtualMachine":
                    if (resourceEvent.has("id")) {
                        LOGGER.info("VM event UUID", resourceEvent.getString("id"));
                        VmInstance vmInstance = virtualmachineservice.findByUUID(resourceEvent.getString("id"));
                        if (vmInstance != null) {
                            if (resourceEvent.getString(EventTypes.RESOURCE_STATE).equals("Error")) {
                                vmInstance.setStatus(Status
                                        .valueOf(resourceEvent.getString(EventTypes.RESOURCE_STATE).toUpperCase()));
                                vmInstance.setSyncFlag(false);
                                String instanceResponse = cloudStackInstanceService
                                        .queryAsyncJobResult(vmInstance.getEventMessage(), CloudStackConstants.JSON);
                                JSONObject instance = new JSONObject(instanceResponse)
                                        .getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
                                if (instance.getString(CloudStackConstants.CS_JOB_STATUS)
                                        .equals(GenericConstants.ERROR_JOB_STATUS)) {
                                    vmInstance.setEventMessage(instance.getJSONObject(CloudStackConstants.CS_JOB_RESULT)
                                            .getString(CloudStackConstants.CS_ERROR_TEXT));
                                }
                                virtualmachineservice.update(vmInstance);
                                throw new Exception(vmInstance.getEventMessage());
                            }
                            LOGGER.info("VM event message", resourceEvent);
                            if (resourceEvent != null) {
                                vmInstance.setStatus(Status
                                        .valueOf(resourceEvent.getString(EventTypes.RESOURCE_STATE).toUpperCase()));
                                vmInstance.setEventMessage("");
                            }
                            vmInstance.setSyncFlag(false);
                            virtualmachineservice.update(vmInstance);

                            // Detach the instance from volume
                            if (!resourceEvent.getString(EventTypes.RESOURCE_STATE)
                                    .equals(resourceEvent.getString(EventTypes.OLD_RESOURCE_STATE))) {
                                if (resourceEvent.getString(EventTypes.RESOURCE_STATE).equals("Expunging")) {
                                    List<Volume> volumeList = volumeService
                                            .findByInstanceForResourceState(vmInstance.getId());
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
                                    List<PortForwarding> portForwardingList = portForwardingService
                                            .findByInstance(vmInstance.getId());
                                    for (PortForwarding portForwarding : portForwardingList) {
                                        portForwarding.setIsActive(false);
                                        portForwarding.setSyncFlag(false);
                                        portForwardingService.update(portForwarding);
                                    }

                                     List<VmSnapshot> vmSnapshotList = vmSnapshotService.findByVmInstance(vmInstance.getId(), false);
                                        for(VmSnapshot vmSnapshot : vmSnapshotList) {
                                            vmSnapshot.setIsRemoved(true);
                                            vmSnapshot.setStatus(ck.panda.domain.entity.VmSnapshot.Status.Expunging);
                                            vmSnapshot.setSyncFlag(false);
                                             vmSnapshotService.save(vmSnapshot);
                                        }
                                }
                                // if attaching network in stopped vm and while
                                // starting that vm instance update
                                // the public ip address table in as same as in
                                // ACS.
                                if (resourceEvent.getString(EventTypes.RESOURCE_STATE)
                                        .equals(EventTypes.EVENT_STATUS_CREATE)) {
                                    sync.syncIpAddress();
                                }
                                // While vm stopping remove the host
                                if (resourceEvent.getString(EventTypes.RESOURCE_STATE)
                                        .equals(EventTypes.EVENT_STATUS_STOPPED)) {
                                    vmInstance.setHostId(null);
                                    vmInstance.setHost(null);
                                    vmInstance.setHostUuid(null);
                                    virtualmachineservice.update(vmInstance);
                                    // Resource count for domain
                                    HashMap<String, String> domainCountMap = new HashMap<String, String>();
                                    String csResponse = cloudStackResourceCapacity.updateResourceCount(
                                            vmInstance.getDomain().getUuid(), domainCountMap, "json");
                                    convertEntityService.resourceCount(csResponse);
                                }
                                if (resourceEvent.getString(EventTypes.OLD_RESOURCE_STATE)
                                        .equals(EventTypes.EVENT_STATUS_DESTROYED)
                                        && resourceEvent.getString(EventTypes.RESOURCE_STATE)
                                                .equals(EventTypes.EVENT_STATUS_STOPPED)) {
                                    if (!convertEntityService.getDepartmentById(vmInstance.getDepartmentId()).getType()
                                            .equals(AccountType.USER)) {
                                        updateResourceCountService.QuotaUpdateByResourceObject(vmInstance,
                                                "RestoreInstance", vmInstance.getDomainId(), "Domain", "Update");
                                    } else {
                                        if (vmInstance.getProjectId() != null) {
                                            updateResourceCountService.QuotaUpdateByResourceObject(vmInstance,
                                                    "RestoreInstance", vmInstance.getProjectId(), "Project", "Update");
                                        }
                                        if (vmInstance.getDepartmentId() != null) {
                                            updateResourceCountService.QuotaUpdateByResourceObject(vmInstance,
                                                    "RestoreInstance", vmInstance.getDepartmentId(), "Department",
                                                    "Update");
                                        }
                                        if (vmInstance.getDomainId() != null) {
                                            updateResourceCountService.QuotaUpdateByResourceObject(vmInstance,
                                                    "RestoreInstance", vmInstance.getDomainId(), "Domain", "Update");
                                        }
                                    }
                                }
                                if (resourceEvent.getString(EventTypes.RESOURCE_STATE)
                                        .equals(EventTypes.EVENT_STATUS_RUNNING)) {
                                    // Host update & internal name while create
                                    // vm as user.
                                    if (vmInstance.getHostId() == null) {
                                        CloudStackConfiguration cloudConfig = cloudConfigService.find(1L);
                                        server.setServer(cloudConfig.getApiURL(), cloudConfig.getSecretKey(),
                                                cloudConfig.getApiKey());
                                        cloudStackInstanceService.setServer(server);
                                        HashMap<String, String> vmMap = new HashMap<String, String>();
                                        vmMap.put(CloudStackConstants.CS_ID, vmInstance.getUuid());
                                        String response = cloudStackInstanceService
                                                .listVirtualMachines(CloudStackConstants.JSON, vmMap);
                                        JSONArray vmListJSON = null;
                                        JSONObject responseObject = new JSONObject(response)
                                                .getJSONObject(CloudStackConstants.CS_LIST_VM_RESPONSE);
                                        if (responseObject.has(CloudStackConstants.CS_VM)) {
                                            vmListJSON = responseObject.getJSONArray(CloudStackConstants.CS_VM);
                                            // 2. Iterate the json list, convert
                                            // the single json entity to vm.
                                            for (int i = 0, size = vmListJSON.length(); i < size; i++) {
                                                // 2.1 Call convert by passing
                                                // JSONObject to vm entity.
                                                VmInstance CsVmInstance = VmInstance
                                                        .convert(vmListJSON.getJSONObject(i));
                                                // 2.2 Update vm host by
                                                // transient variable.
                                                vmInstance.setHostId(
                                                        convertEntityService.getHostId(CsVmInstance.getTransHostId()));
                                                // 2.3 Update internal name.
                                                vmInstance.setInstanceInternalName(
                                                        CsVmInstance.getInstanceInternalName());
                                                if (vmInstance.getHostId() != null) {
                                                    vmInstance.setPodId(
                                                            convertEntityService.getPodIdByHost(convertEntityService
                                                                    .getHostId(CsVmInstance.getTransHostId())));
                                                }
                                                // 3. Update vm for user vm
                                                // creation.
                                                vmInstance = virtualmachineservice.update(vmInstance);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
                case "Volume":
                    if (resourceEvent.has("id")
                            && resourceEvent.getString(EventTypes.RESOURCE_STATE).equals("Expunged")) {
                        Volume volume = volumeService.findByUUID(resourceEvent.getString("id"));
                        volume.setStatus(volume.getStatus()
                                .valueOf(resourceEvent.getString(EventTypes.RESOURCE_STATE).toUpperCase()));
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
    }
}
