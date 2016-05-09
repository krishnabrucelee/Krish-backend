package ck.panda.service;

import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import ck.panda.constants.CloudStackConstants;
import ck.panda.constants.EventTypes;
import ck.panda.domain.entity.CloudStackConfiguration;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.Event;
import ck.panda.domain.entity.Network;
import ck.panda.domain.entity.Nic;
import ck.panda.domain.entity.PortForwarding;
import ck.panda.domain.entity.VPC;
import ck.panda.domain.entity.VPC.Status;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.VmSnapshot;
import ck.panda.domain.entity.Volume;
import ck.panda.domain.entity.Volume.VolumeType;
import ck.panda.util.CloudStackInstanceService;
import ck.panda.util.CloudStackResourceCapacity;
import ck.panda.util.CloudStackServer;
import ck.panda.util.CloudStackSnapshotService;
import ck.panda.util.CloudStackVolumeService;
import ck.panda.util.ConfigUtil;

/**
 * Websocket Service Implementation.
 */
@Service
public class WebsocketServiceImpl implements WebsocketService {
    /** Constant for Cloud stack volume. */
    public static final String CS_VOLUME = "volume";

    /** Constant for Cloud stack volume list response. */
    public static final String CS_LIST_VOLUME_RESPONSE = "listvolumesresponse";

    /** Cloud stack vpc service reference. */
    @Autowired
    private VPCService vpcService;

    /** Simple messaging template for send and receive messages. */
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /** VM snapshot object name. */
    public static final String VM_SNAPSHOT = "vmSnapshot";

    /** VM snapshot object name. */
    public static final String VM_SNAPSHOT_ID = "vmsnapshotid";

    /** VM snapshot object name. */
    public static final String VolumeId = "id";

    /** VM snapshot memory. */
    public static final String VM_SNAPSHOT_MEMORY = "snapshotmemory";

    /** Cloudstack snapshot service reference. */
    @Autowired
    private CloudStackSnapshotService cssnapshot;

    /** Department Service reference. */
    @Autowired
    private DepartmentService departmentService;

    /** Domain Service reference. */
    @Autowired
    private DomainService domainService;

    /** Domain Service reference. */
    @Autowired
    private VolumeService volumeService;

    /** Autowired Project Service. */
    @Autowired
    private ProjectService projectService;

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AsynchronousJobServiceImpl.class);

    /** Update quota constants. */
    public static final String CS_Instance = "Instance", CS_Network = "Network", CS_IP = "IP", CS_Volume = "Volume",
            CS_Domain = "Domain", CS_Project = "Project", CS_Department = "Department", CS_Expunging = "Expunging",
            CS_UploadVolume = "UploadVolume", CS_Destroy = "Destroy", Update = "update", Delete = "delete";

    /** Event notification service for tracking. */
    @Autowired
    private EventNotificationService eventNotificationService;

    /** CloudStack connector reference for instance. */
    @Autowired
    private CloudStackInstanceService cloudStackInstanceService;

    /** Cloud stack configuration utility class. */
    @Autowired
    private ConfigUtil config;

    /** CloudStack connector. */
    @Autowired
    private CloudStackServer server;

    /** CloudStack configuration . */
    @Autowired
    private CloudStackConfigurationService cloudConfigService;

    /** sync service reference. */
    @Autowired
    private SyncService sync;

    /** Update Resource Count service reference. */
    @Autowired
    private UpdateResourceCountService updateResourceCountService;

    /** Vm snapshot reference. */
    @Autowired
    private VmSnapshotService vmSnapshotService;

    /** Service reference to Port Forwarding. */
    @Autowired
    private PortForwardingService portForwardingService;

    /** Lists types of Volumes in cloudstack server. */
    @Autowired
    private CloudStackVolumeService csVolumeService;

    /** Nic service for listing nic. */
    @Autowired
    private NicService nicService;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** Virtual machine service for get status of vm. */
    @Autowired
    private VirtualMachineService virtualMachineService;

    /** Network service for get status of network. */
    @Autowired
    private NetworkService networkService;

    /** Asynchronous service. */
    @Autowired
    private AsynchronousJobService asyncService;

    @Override
    public void handleEventAction(Event event, JSONObject eventObject) throws Exception {
        event.setIsActive(true);
        event.setIsArchive(false);
        ObjectMapper eventmapper = new ObjectMapper();
        if (event != null) {
            if (event.getEvent() != null) {
                if (event.getEventType().equals(Event.EventType.ASYNC)) {
                    String instanceResponse = cloudStackInstanceService.queryAsyncJobResult(event.getJobId(),CloudStackConstants.JSON);
                    JSONObject instance = new JSONObject(instanceResponse).getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
                    if (instance.has(CloudStackConstants.CS_JOB_INSTANCE_ID)) {
                        event.setResourceUuid(instance.getString(CloudStackConstants.CS_JOB_INSTANCE_ID));
                    }
                    asyncService.syncResourceStatus(eventObject);
                }
                if (event.getEventType().equals(Event.EventType.ACTION)
                        && event.getStatus().equals(Event.Status.INFO)) {
                    String message = eventmapper.writeValueAsString(event);
                    messagingTemplate.convertAndSend(CloudStackConstants.CS_ACTION_MAP + event.getEventOwnerId(),
                            message);
                }
                if (event.getMessage() != null && (event.getStatus().equals(Event.Status.FAILED)
                        || event.getStatus().equals(Event.Status.ERROR))) {
                    String message = eventmapper.writeValueAsString(event);
					if (event.getStatus().equals(Event.Status.FAILED) && (eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE).startsWith(EventTypes.EVENT_VM_SNAPSHOT))) {
						if (eventObject.has(CloudStackConstants.CS_CMD_INFO)) {
							JSONObject json = new JSONObject(eventObject.getString(CloudStackConstants.CS_CMD_INFO));
							if (eventObject.getString(CloudStackConstants.CS_STATUS)
									.equalsIgnoreCase(CloudStackConstants.CS_STATUS_FAILED)) {
								VmSnapshot persistVmSnapshot = vmSnapshotService
										.findByUUID(json.getString(VM_SNAPSHOT_ID));
								HashMap<String, String> optional = new HashMap<String, String>();
								optional.put(VM_SNAPSHOT_ID, json.getString(VM_SNAPSHOT_ID));
								config.setServer(1L);
								// 1. Get the list of vm snapshot from CS server using CS connector.
								String response = cssnapshot.listVMSnapshot(optional);
								JSONArray vmSnapshotListJSON = null;
								JSONObject responseObject = new JSONObject(response)
										.getJSONObject(CloudStackConstants.CS_LIST_VM_SNAPSHOT_RESPONSE);
								if (responseObject.has(VM_SNAPSHOT)) {
									vmSnapshotListJSON = responseObject.getJSONArray(VM_SNAPSHOT);
									// 2.1 Call convert by passing JSONObject to vm snapshot entity and Add the converted vm snapshot entity to list.
									VmSnapshot vmSnapshot = VmSnapshot.convert(vmSnapshotListJSON.getJSONObject(0));
									if (vmSnapshot != null) {
										persistVmSnapshot.setStatus(vmSnapshot.getStatus());
										persistVmSnapshot.setIsCurrent(vmSnapshot.getIsCurrent());
										persistVmSnapshot.setSyncFlag(vmSnapshot.getSyncFlag());
										if(persistVmSnapshot.getStatus().equals(VmSnapshot.Status.Expunging)){
											persistVmSnapshot.setIsRemoved(true);
										} else {
											persistVmSnapshot.setIsRemoved(false);
										}
									}
									vmSnapshotService.update(persistVmSnapshot);
								}
							}
						}
                    }
					if (event.getStatus().equals(Event.Status.FAILED)
							&& eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE).startsWith(EventTypes.EVENT_VOLUME_ATTACH)) {
						if (eventObject.has(CloudStackConstants.CS_CMD_INFO)) {
							JSONObject json = new JSONObject(eventObject.getString(CloudStackConstants.CS_CMD_INFO));
							if (eventObject.getString(CloudStackConstants.CS_STATUS)
									.equalsIgnoreCase(CloudStackConstants.CS_STATUS_FAILED)) {
								Volume persistVolume = volumeService.findByUUID(json.getString(VolumeId));
								if (persistVolume != null) {
									HashMap<String, String> volumeMap = new HashMap<String, String>();
									config.setServer(1L);
									volumeMap.put(CloudStackConstants.CS_ID, json.getString(VolumeId));
									String response = csVolumeService.listVolumes(CloudStackConstants.JSON, volumeMap);
									JSONArray volumeListJSON = null;
									JSONObject responseObject = new JSONObject(response)
											.getJSONObject(CS_LIST_VOLUME_RESPONSE);
									if (responseObject.has(CS_VOLUME)) {
										volumeListJSON = responseObject.getJSONArray(CS_VOLUME);
										Volume volume = Volume.convert(volumeListJSON.getJSONObject(0));
										persistVolume
												.setZoneId(convertEntityService.getZoneId(volume.getTransZoneId()));
										persistVolume.setDomainId(
												convertEntityService.getDomainId(volume.getTransDomainId()));
										persistVolume.setStorageOfferingId(convertEntityService
												.getStorageOfferId(volume.getTransStorageOfferingId()));
										persistVolume.setVmInstanceId(
												convertEntityService.getVmInstanceId(volume.getTransvmInstanceId()));
										if (volume.getTransProjectId() != null) {
											persistVolume.setProjectId(
													convertEntityService.getProjectId(volume.getTransProjectId()));
											persistVolume.setDepartmentId(
													projectService.find(volume.getProjectId()).getDepartmentId());
										} else {
											// departmentRepository.findByUuidAndIsActive(volume.getTransDepartmentId(),
											// true);
											Domain domain = domainService.find(volume.getDomainId());
											persistVolume.setDepartmentId(
													convertEntityService.getDepartmentByUsernameAndDomains(
															volume.getTransDepartmentId(), domain));
										}
										persistVolume.setIsSyncFlag(false);
										volumeService.update(persistVolume);
									}
								}
							}
						}
					}

					if (event.getStatus().equals(Event.Status.FAILED) && eventObject
							.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE).startsWith(EventTypes.EVENT_VPC)) {
						if (eventObject.has(CloudStackConstants.CS_CMD_INFO)) {
							JSONObject json = new JSONObject(eventObject.getString(CloudStackConstants.CS_CMD_INFO));
							if (eventObject.getString(CloudStackConstants.CS_STATUS)
									.equalsIgnoreCase(CloudStackConstants.CS_STATUS_FAILED)) {
								if (eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE)
										.equalsIgnoreCase(EventTypes.EVENT_VPC_CREATE)) {
									VPC persistVpc = vpcService.findByUUID(json.getString(CloudStackConstants.CS_ID));
									if (persistVpc != null) {
										persistVpc.setIsActive(false);
										persistVpc.setStatus(Status.INACTIVE);
										persistVpc.setSyncFlag(false);
										vpcService.save(persistVpc);
									}
								} if (eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE)
										.equalsIgnoreCase(EventTypes.EVENT_VPC_DELETE)) {
									VPC persistVpc = vpcService.findByUUID(json.getString(CloudStackConstants.CS_ID));
									if (persistVpc != null) {
										persistVpc.setIsActive(true);
										persistVpc.setSyncFlag(false);
										vpcService.save(persistVpc);
									}
								} if (eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE)
										.equalsIgnoreCase(EventTypes.EVENT_VPC_UPDATE)) {
									VPC persistVpc = vpcService.findByUUID(json.getString(CloudStackConstants.CS_ID));
									if (persistVpc != null) {
										persistVpc.setIsActive(true);
										persistVpc.setSyncFlag(false);
										vpcService.save(persistVpc);
									}
								} if (eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE)
										.equalsIgnoreCase(EventTypes.EVENT_VPC_RESTART)) {
									VPC persistVpc = vpcService.findByUUID(json.getString(CloudStackConstants.CS_ID));
									if (persistVpc != null) {
										persistVpc.setIsActive(true);
										persistVpc.setRedundantVPC(false);
										persistVpc.setCleanUpVPC(false);
										persistVpc.setSyncFlag(false);
										vpcService.save(persistVpc);
									}
								}
							}
						}
					}
                    messagingTemplate.convertAndSend(CloudStackConstants.CS_ERROR_MAP + event.getEventOwnerId(),
                            message);
                }
                if (event.getStatus().equals(Event.Status.SUCCEEDED) || event.getStatus().equals(Event.Status.INFO)) {
                    String message = eventmapper.writeValueAsString(event);
                    messagingTemplate.convertAndSend(CloudStackConstants.CS_ASYNC_MAP
                            + event.getEvent().substring(0, event.getEvent().indexOf('.', 0))
                            + CloudStackConstants.CS_SEPERATOR + event.getEventOwnerId(), message);
                }
                if (event.getEventType().equals(Event.EventType.RESOURCESTATE)) {
                    if (eventObject.has(EventTypes.RESOURCE_STATE) && eventObject.has(EventTypes.OLD_RESOURCE_STATE)) {
                        if (!eventObject.getString(EventTypes.RESOURCE_STATE).equalsIgnoreCase(eventObject.getString(EventTypes.OLD_RESOURCE_STATE))) {
                            String resourceCurrentStatus = null;
                            if (event.getResourceUuid() != null) {
                                String message = eventmapper.writeValueAsString(event);
                                if (event.getEvent().equals("Volume")) {
                                    Volume volume = volumeService.findByUUID(event.getResourceUuid());
                                    if (volume != null) {
                                        if (event.getMessage().equalsIgnoreCase("Expunged")) {
                                            volume.setStatus(Volume.Status.valueOf(event.getMessage().toUpperCase()));
                                            volume.setIsActive(false);
                                            volume.setIsSyncFlag(false);
                                            volumeService.update(volume);
                                            messagingTemplate.convertAndSend(CloudStackConstants.CS_RESOURCE_MAP + event.getEvent(), message);
                                        }
                                        if (event.getMessage().equalsIgnoreCase("Uploaded")) {
                                            HashMap<String, String> volumeMap = new HashMap<String, String>();
                                            config.setServer(1L);
                                            volumeMap.put("id", event.getResourceUuid());
                                            String response = csVolumeService.listVolumes(CloudStackConstants.JSON,volumeMap);
                                            JSONArray volumeListJSON = null;
                                            JSONObject responseObject = new JSONObject(response).getJSONObject(CS_LIST_VOLUME_RESPONSE);
                                            if (responseObject.has(CS_VOLUME)) {
                                                volumeListJSON = responseObject.getJSONArray(CS_VOLUME);
                                                Volume csVolume = Volume.convert(volumeListJSON.getJSONObject(0));
                                                volume.setDiskSize(csVolume.getDiskSize());
                                                volume.setStatus(Volume.Status.valueOf(event.getMessage().toUpperCase()));
                                                volumeService.update(volume);
                                                messagingTemplate.convertAndSend(CloudStackConstants.CS_RESOURCE_MAP + event.getEvent(),message);
                                            }
                                        }
                                        resourceCurrentStatus = volume.getStatus().toString();
                                        if (resourceCurrentStatus != null && !resourceCurrentStatus.equalsIgnoreCase(event.getMessage())) {
                                            message = eventmapper.writeValueAsString(event);
                                            messagingTemplate.convertAndSend(CloudStackConstants.CS_RESOURCE_MAP + event.getEvent(), message);
                                        }
                                    }
                                }
								if (event.getEvent().equals("VirtualMachine")) {
									if (eventObject.getString(EventTypes.RESOURCE_STATE)
											.equalsIgnoreCase(VmInstance.Status.STOPPED.name()) && eventObject.getString(EventTypes.OLD_RESOURCE_STATE)
											.equalsIgnoreCase(VmInstance.Status.DESTROYED.name())) {
										Thread.sleep(5000);
									}
									VmInstance vmInstance = virtualMachineService.findByUUID(event.getResourceUuid());
									if (vmInstance != null) {
										if (!eventObject.getString(EventTypes.RESOURCE_STATE).equalsIgnoreCase(
												vmInstance.getStatus().name())) {
											resourceCurrentStatus = vmInstance.getStatus().toString();
											if (resourceCurrentStatus != null
													&& !resourceCurrentStatus.equalsIgnoreCase(event.getMessage())) {
												vmInstance.setStatus(
														VmInstance.Status.valueOf(event.getMessage().toUpperCase()));
												vmInstance.setSyncFlag(false);
												virtualMachineService.update(vmInstance);
												if (event.getMessage().equals("Expunging")) {
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

													List<VmSnapshot> vmSnapshotList = vmSnapshotService
															.findByVmInstance(vmInstance.getId(), false);
													for (VmSnapshot vmSnapshot : vmSnapshotList) {
														vmSnapshot.setIsRemoved(true);
														vmSnapshot.setStatus(
																ck.panda.domain.entity.VmSnapshot.Status.Expunging);
														vmSnapshot.setSyncFlag(false);
														vmSnapshotService.save(vmSnapshot);
													}
												}
												if (event.getMessage().equals(EventTypes.EVENT_STATUS_CREATE)) {
													sync.syncIpAddress();
												}
												if (event.getMessage().equals(EventTypes.EVENT_STATUS_STOPPED) || event
														.getMessage().equals(EventTypes.EVENT_STATUS_DESTROYED)) {
													vmInstance.setHostId(null);
													vmInstance.setHost(null);
													vmInstance.setHostUuid(null);
													virtualMachineService.update(vmInstance);
												}
												if (eventObject.getString(EventTypes.OLD_RESOURCE_STATE)
														.equalsIgnoreCase(EventTypes.EVENT_STATUS_DESTROYED)
														&& eventObject.getString(EventTypes.RESOURCE_STATE)
																.equalsIgnoreCase(EventTypes.EVENT_STATUS_STOPPED)) {
													// Resource count for domain
													if (vmInstance.getProjectId() != null) {
														updateResourceCountService.QuotaUpdateByResourceObject(
																vmInstance, "RestoreInstance",
																vmInstance.getProjectId(), "Project", "Update");
													} else {
														updateResourceCountService.QuotaUpdateByResourceObject(
																vmInstance, "RestoreInstance",
																vmInstance.getDepartmentId(), "Department", "Update");
													}
												}
												if (event.getMessage().equalsIgnoreCase("Expunging")) {
													if (vmInstance.getProjectId() != null) {
														updateResourceCountService.QuotaUpdateByResourceObject(
																vmInstance, "Expunging", vmInstance.getProjectId(),
																"Project", "delete");
													} else {
														updateResourceCountService.QuotaUpdateByResourceObject(
																vmInstance, "Expunging", vmInstance.getDepartmentId(),
																"Department", "delete");
													}
												}
												if (event.getMessage()
														.equalsIgnoreCase(EventTypes.EVENT_STATUS_RUNNING)) {
													if (vmInstance.getHostId() == null) {
														CloudStackConfiguration cloudConfig = cloudConfigService
																.find(1L);
														server.setServer(cloudConfig.getApiURL(),
																cloudConfig.getSecretKey(), cloudConfig.getApiKey());
														cloudStackInstanceService.setServer(server);
														HashMap<String, String> vmMap = new HashMap<String, String>();
														vmMap.put(CloudStackConstants.CS_ID, vmInstance.getUuid());
														String response = cloudStackInstanceService
																.listVirtualMachines(CloudStackConstants.JSON, vmMap);
														JSONArray vmListJSON = null;
														JSONObject responseObject = new JSONObject(response)
																.getJSONObject(CloudStackConstants.CS_LIST_VM_RESPONSE);
														if (responseObject.has(CloudStackConstants.CS_VM)) {
															vmListJSON = responseObject
																	.getJSONArray(CloudStackConstants.CS_VM);
															// 2. Iterate the json list, convert the single json entity to vm.
															for (int i = 0, size = vmListJSON.length(); i < size; i++) {
																// 2.1 Call convert by passing JSONObject to vm entity.
																VmInstance CsVmInstance = VmInstance
																		.convert(vmListJSON.getJSONObject(i));
																// 2.2 Update vm host by transient variable.
																vmInstance.setHostId(convertEntityService
																		.getHostId(CsVmInstance.getTransHostId()));
																// 2.3 Update internal name.
																vmInstance.setInstanceInternalName(
																		CsVmInstance.getInstanceInternalName());
																if (vmInstance.getHostId() != null) {
																	vmInstance.setPodId(convertEntityService
																			.getPodIdByHost(convertEntityService
																					.getHostId(CsVmInstance
																							.getTransHostId())));
																}
																// 3. Update vm for user vm creation.
																vmInstance = virtualMachineService.update(vmInstance);
															}
														}
													}
												}
												message = eventmapper.writeValueAsString(event);
												messagingTemplate.convertAndSend(
														CloudStackConstants.CS_RESOURCE_MAP + event.getEvent(),
														message);
											}
										}
									}
								}
                                if (event.getEvent().equals("Network")) {
                                    Network network = networkService.findByUUID(event.getResourceUuid());
                                    if (network != null) {
                                        resourceCurrentStatus = network.getStatus().toString();
                                        if (resourceCurrentStatus != null && !resourceCurrentStatus.equalsIgnoreCase(event.getMessage())) {
                                            message = eventmapper.writeValueAsString(event);
                                            messagingTemplate.convertAndSend(CloudStackConstants.CS_RESOURCE_MAP + event.getEvent(), message);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (event.getEventType().equals(Event.EventType.ACTION) || event.getEventType().equals(Event.EventType.ALERT)) {
            eventNotificationService.save(event);
        }
    }
}
