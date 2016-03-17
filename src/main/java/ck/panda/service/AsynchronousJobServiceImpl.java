package ck.panda.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import ck.panda.constants.CloudStackConstants;
import ck.panda.constants.EventTypes;
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.Event;
import ck.panda.domain.entity.Event.EventType;
import ck.panda.domain.entity.FirewallRules;
import ck.panda.domain.entity.FirewallRules.Purpose;
import ck.panda.domain.entity.IpAddress.VpnState;
import ck.panda.domain.entity.IpAddress;
import ck.panda.domain.entity.LbStickinessPolicy;
import ck.panda.domain.entity.LbStickinessPolicy.StickinessMethod;
import ck.panda.domain.entity.LoadBalancerRule;
import ck.panda.domain.entity.LoadBalancerRule.State;
import ck.panda.domain.entity.Network;
import ck.panda.domain.entity.NetworkOffering;
import ck.panda.domain.entity.Nic;
import ck.panda.domain.entity.PortForwarding;
import ck.panda.domain.entity.Snapshot;
import ck.panda.domain.entity.Template;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.VmIpaddress;
import ck.panda.domain.entity.VmSnapshot;
import ck.panda.domain.entity.Volume;
import ck.panda.domain.entity.VpnUser;
import ck.panda.domain.entity.Department.AccountType;
import ck.panda.domain.entity.Volume.Status;
import ck.panda.rabbitmq.util.ResponseEvent;
import ck.panda.util.CloudStackInstanceService;
import ck.panda.util.CloudStackLoadBalancerService;
import ck.panda.util.CloudStackNetworkOfferingService;
import ck.panda.util.CloudStackNicService;
import ck.panda.util.CloudStackVolumeService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.EncryptionUtil;
import ck.panda.util.JsonUtil;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;

/**
 * Synchronization of all the asynchronous data from the cloudStack.
 *
 */
@PropertySource(value = "classpath:permission.properties")
@Service
public class AsynchronousJobServiceImpl implements AsynchronousJobService {

      /** Constant for user entity. */
    private static final String USER_DISABLE = "USER.DISABLE";

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AsynchronousJobServiceImpl.class);

    /** Update quota constants. */
    public static final String CS_Instance = "Instance", CS_Network = "Network", CS_IP = "IP", CS_Volume = "Volume",
            CS_Domain = "Domain", CS_Project = "Project", CS_Department = "Department", CS_Expunging = "Expunging",
            CS_UploadVolume = "UploadVolume", CS_Destroy = "Destroy", Update = "update", Delete = "delete";

    /** Cloud stack configuration reference. */
    @Autowired
    private ConfigUtil configUtil;

    /** Virtual machine Service for listing vms. */
    @Autowired
    private VirtualMachineService virtualMachineService;

    /**
     * NetworkOfferingService for listing network offers in cloudstack server.
     */
    @Autowired
    private NetworkService networkService;

    /** Volume Service for listing volumes. */
    @Autowired
    private VolumeService volumeService;

    /** Ip address Service for listing ipaddress. */
    @Autowired
    private IpaddressService ipService;

    /** Ip address Service for listing ipaddress. */
    @Autowired
    private VmIpaddressService vmIpService;

    /** CloudStack connector reference for instance. */
    @Autowired
    private CloudStackInstanceService cloudStackInstanceService;

    /** Template Service for listing templates. */
    @Autowired
    private TemplateService templateService;

    /** Egress Service for listing egressrules. */
    @Autowired
    private EgressRuleService egressRuleService;

    /** Hypervisor service reference. */
    @Autowired
    private HypervisorService hypervisorService;

    /**
     * NetworkOfferingService for listing network offers in cloudstack server.
     */
    @Autowired
    private NetworkOfferingService networkOfferingService;

    /** CloudStack Network Offering service for connectivity with cloudstack. */
    @Autowired
    private CloudStackNetworkOfferingService csNetworkOfferingService;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** Autowired Project Service. */
    @Autowired
    private ProjectService projectService;

    /** Domain Service reference. */
    @Autowired
    private DomainService domainService;

    /** Nic service for listing nic. */
    @Autowired
    private NicService nicService;

    /** Websocket service reference. */
	@Autowired
	private WebsocketService websocketService;

    /** CloudStack connector reference for instance. */
    @Autowired
    private CloudStackNicService cloudStackNicService;

    /** Lists types of Volumes in cloudstack server. */
    @Autowired
    private CloudStackVolumeService csVolumeService;

    /** Service reference to Port Forwarding. */
    @Autowired
    private PortForwardingService portForwardingService;

    /** Service reference to Load Balancer. */
    @Autowired
    private LoadBalancerService loadBalancerService;

    /** Service reference to Load Balancer. */
    @Autowired
    private LbStickinessPolicyService lbPolicyService;

    /** Service reference to Snapshot. */
    @Autowired
    private SnapshotService snapShotService;

    /**
     *  Service reference to User.
     */
    @Autowired
    private UserService userService;
    /**
     *  Service reference to vmSnapshot Sevice.
     */
    @Autowired
    private VmSnapshotService vmSnapshotService;

    /** Cloud stack firewall service. */
    @Autowired
    private CloudStackLoadBalancerService cloudStackLoadBalancerService;

    /** Message source attribute. */
    @Autowired
    private MessageSource messageSource;

    /** Update Resource Count service reference. */
    @Autowired
    private UpdateResourceCountService updateResourceCountService;

    /** Sync service. */
    @Autowired
    private SyncService syncService;

    /** For listing VPN user list from cloudstack server. */
    @Autowired
    private VpnUserService vpnUserService;

    /** Secret key value is append. */
    @Value(value = "${aes.salt.secretKey}")
    private String secretKey;

    /** Asynchronous job id. */
    public static final String CS_ASYNC_JOB_ID = "jobId";

    /** Command type. */
    public static final String CS_COMMAND = "command";

    /**
     * Sync with CloudStack server list via Asynchronous Job.
     *
     * @param Object
     *            response object.
     * @throws ApplicationException
     *             unhandled application errors.
     * @throws Exception
     *             cloudstack unhandled errors.
     */
	@Override
	public void syncResourceStatus(JSONObject eventObject) throws Exception {
		Event asyncJobEvent = new Event();
		// Event record.
		configUtil.setServer(1L);
		String eventObjectResult = cloudStackInstanceService.queryAsyncJobResult(eventObject.getString(CS_ASYNC_JOB_ID),
				CloudStackConstants.JSON);

		JSONObject jobResultResponse = new JSONObject(eventObjectResult)
				.getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
		JSONObject jobResult = null;
		if (jobResultResponse.has(CloudStackConstants.CS_JOB_RESULT)) {
			jobResult = jobResultResponse.getJSONObject(CloudStackConstants.CS_JOB_RESULT);
		}

		String commandText = null;
		if (eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE).contains(".")) {
			commandText = eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE).substring(0,
					eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE).indexOf('.', 0)) + ".";
		} else {
			commandText = eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE);
		}
		// Event record for async call.
		asyncJobEvent.setEvent(eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE));
		asyncJobEvent.setEventDateTime(convertEntityService.getTimeService().getCurrentDateAndTime());
		asyncJobEvent.setEventOwnerId(
				convertEntityService.getOwnerByUuid(eventObject.getString(CloudStackConstants.CS_USER)));
		asyncJobEvent.setEventType(EventType.ASYNC);
		JSONObject json = new JSONObject(eventObject.getString(CloudStackConstants.CS_CMD_INFO));
		if (eventObject.getString(CloudStackConstants.CS_STATUS)
				.equalsIgnoreCase(CloudStackConstants.CS_STATUS_FAILED)) {
			asyncJobEvent.setMessage(jobResult.getString(CloudStackConstants.CS_ERROR_TEXT));
			asyncJobEvent.setStatus(Event.Status.FAILED);
			if (json.has(CloudStackConstants.CS_UUID)) {
				asyncJobEvent.setResourceUuid(json.getString(CloudStackConstants.CS_UUID));
			}
			switch (eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE)) {
			case EventTypes.EVENT_VM_SNAPSHOT_CREATE:
				syncService.syncVmSnapshots();
				break;
			default:
				LOGGER.debug("No async required");
			}
		} else {
			if (eventObject.has(CloudStackConstants.CS_INSTANCE_UUID)) {
				asyncJobEvent.setResourceUuid(eventObject.getString(CloudStackConstants.CS_INSTANCE_UUID));
			} else {
				asyncJobEvent.setResourceUuid(json.getString(CloudStackConstants.CS_UUID));
			}
			asyncJobEvent.setStatus(
					Event.Status.valueOf(eventObject.getString(CloudStackConstants.CS_STATUS).toUpperCase()));
		}
		asyncJobEvent.setEventStartId(json.getString(CloudStackConstants.CS_EVENT_ID));
		asyncJobEvent.setJobId(eventObject.getString(CloudStackConstants.CS_ASYNC_JOB_ID));
		// websocket record for async call.
		websocketService.handleEventAction(asyncJobEvent);
		if (eventObject.getString(CloudStackConstants.CS_STATUS)
				.equalsIgnoreCase(CloudStackConstants.CS_STATUS_SUCCEEDED) && jobResult != null) {
			switch (commandText) {
			case EventTypes.EVENT_VM:
				LOGGER.debug("VM Sync", eventObject.getString(CS_ASYNC_JOB_ID) + "==="
						+ eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE));
				syncVirtualMachine(jobResult, eventObject);
				break;
			case EventTypes.EVENT_NETWORK:
				if (!eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE).contains("OFFERING")) {

					LOGGER.debug("Network sync", eventObject.getString(CS_ASYNC_JOB_ID) + "==="
							+ eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE));
					if (eventObject.getString(CloudStackConstants.CS_EVENT_STATUS).equals("FAILED")) {
						Network network = Network.convert(jobResult.getJSONObject("network"));
					}
					asyncNetwork(jobResult, eventObject);
				}
				break;
			case EventTypes.EVENT_FIREWALL:
				if (eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE).contains("FIREWALL")) {
					LOGGER.debug("Firewall sync", eventObject.getString(CS_ASYNC_JOB_ID) + "==="
							+ eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE));
					asyncFirewall(jobResult, eventObject);
				}
				break;
			case EventTypes.EVENT_NAT:
				if (eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE).contains("STATICNAT")) {
					LOGGER.debug("Nat sync", eventObject.getString(CS_ASYNC_JOB_ID) + "==="
							+ eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE));
					asyncNat(jobResult, eventObject);
				}
				break;
			case EventTypes.EVENT_TEMPLATE:
				LOGGER.debug("templates sync", eventObject.getString(CS_ASYNC_JOB_ID) + "==="
						+ eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE));
				asyncTemplates(eventObject);
				break;
			case EventTypes.EVENT_ISO:
				LOGGER.debug("ISO sync", eventObject.getString(CS_ASYNC_JOB_ID) + "==="
						+ eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE));
				// Update attach/detach ISO
				if (eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE).equals(EventTypes.EVENT_ISO_ATTACH)
						|| eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE)
								.equals(EventTypes.EVENT_ISO_DETACH)) {
					VmInstance vmInstance = VmInstance.convert(jobResult.getJSONObject(CloudStackConstants.CS_VM));
					VmInstance instance = virtualMachineService.findByUUID(vmInstance.getUuid());
					instance.setIsoName(vmInstance.getIsoName());
					instance.setIsoId(convertEntityService.getIso(vmInstance.getIso()));
					virtualMachineService.update(instance);
				} else {
					asyncTemplates(eventObject);
				}
				break;
			case EventTypes.EVENT_VOLUME:
				LOGGER.debug("Volume sync", eventObject.getString(CS_ASYNC_JOB_ID) + "==="
						+ eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE));
				if (eventObject.getString(CloudStackConstants.CS_EVENT_STATUS).equals("FAILED")) {
					Volume volume = Volume.convert(jobResult.getJSONObject("volume"));
				}
				asyncVolume(jobResult, eventObject);
				break;
			case EventTypes.EVENT_NIC:
				LOGGER.debug("NIC sync", eventObject.getString(CS_ASYNC_JOB_ID) + "==="
						+ eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE));
				asyncNic(jobResult, eventObject);
				break;
			case EventTypes.EVENT_PORTFORWARDING:
				LOGGER.debug("NET sync", eventObject.getString(CS_ASYNC_JOB_ID) + "==="
						+ eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE));
				asyncNet(jobResult, eventObject);
				break;
			case EventTypes.EVENT_LOADBALANCER:
				LOGGER.debug("LB sync", eventObject.getString(CS_ASYNC_JOB_ID) + "==="
						+ eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE));
				asyncLb(jobResult, eventObject);
				break;
			case EventTypes.EVENT_SNAPSHOT:
				LOGGER.debug("Snapshot sync", eventObject.getString(CS_ASYNC_JOB_ID) + "==="
						+ eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE));
				asyncSnapshot(jobResult, eventObject);
				break;
			case EventTypes.EVENT_UNKNOWN:
				String event = eventObject.getString(CS_COMMAND);
				if (event.contains(CS_COMMAND)) {
					LOGGER.debug("Snapshot sync", eventObject.getString(CS_ASYNC_JOB_ID) + "==="
							+ eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE));
					asyncSnapshot(jobResult, eventObject);
				}
				break;
			case EventTypes.EVENT_VM_SNAPSHOT:
				LOGGER.debug("VM snapshot sync", eventObject.getString(CS_ASYNC_JOB_ID) + "==="
						+ eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE));
				asyncVMSnapshot(jobResult, eventObject);
				break;
			case EventTypes.EVENT_VPN:
				LOGGER.debug("VPN sync", eventObject.getString(CS_ASYNC_JOB_ID) + "==="
						+ eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE));
				asyncVpn(jobResult, eventObject);
				break;
			case EventTypes.EVENT_USER:
				LOGGER.debug("User sync", eventObject.getString(CS_ASYNC_JOB_ID) + "==="
						+ eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE));
				asyncUser(jobResult, eventObject);
				break;
			default:
				LOGGER.debug("No sync required", eventObject.getString(CS_ASYNC_JOB_ID) + "==="
						+ eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE));
			}
		}
	}

    /**
     * Sync with CloudStack server virtual machine.
     *
     * @param jobResult
     *            job result
     * @param eventObject
     *            network event object
     * @throws Exception
     *             cloudstack unhandled errors
     */
    public void syncVirtualMachine(JSONObject jobResult, JSONObject eventObject) throws Exception {

        if (jobResult.has("virtualmachine")) {
            VmInstance vmInstance = VmInstance.convert(jobResult.getJSONObject("virtualmachine"));
            VmInstance instance = virtualMachineService.findByUUID(vmInstance.getUuid());
            VmInstance vmIn = null;
            if (instance != null) {
                instance.setSyncFlag(false);
                // 3.1 Find the corresponding CS server vm object by finding it
                // in a
                // hash using uuid
                if (vmInstance.getUuid().equals(instance.getUuid())) {
                    VmInstance csVm = vmInstance;
                    if (csVm != null) {
                        if (volumeService.findByInstanceAndVolumeType(instance.getId()) != null) {
                            csVm.setVolumeSize(
                                    volumeService.findByInstanceAndVolumeType(instance.getId()).getDiskSize());
                        }
                    }
                    csVm.setDomainId(convertEntityService.getDomainId(csVm.getTransDomainId()));
                    csVm.setZoneId(convertEntityService.getZoneId(csVm.getTransZoneId()));
                    csVm.setNetworkId(convertEntityService.getNetworkId(csVm.getTransNetworkId()));
                    csVm.setProjectId(convertEntityService.getProjectId(csVm.getTransProjectId()));
                    csVm.setHostId(convertEntityService.getHostId(csVm.getTransHostId()));
                    csVm.setInstanceOwnerId(convertEntityService.getUserByName(csVm.getTransDisplayName(),
                            convertEntityService.getDomain(csVm.getTransDomainId())));
                    csVm.setDepartmentId(convertEntityService.getDepartmentByUsernameAndDomains(
                            csVm.getTransDepartmentId(), convertEntityService.getDomain(csVm.getTransDomainId())));
                    if (csVm.getTransProjectId() != null) {
                        csVm.setDepartmentId(convertEntityService.getProject(csVm.getTransProjectId()).getDepartmentId());
                    }
                    csVm.setTemplateId(convertEntityService.getTemplateId(csVm.getTransTemplateId()));
                    csVm.setComputeOfferingId(convertEntityService.getComputeOfferId(csVm.getTransComputeOfferingId()));
                    if (csVm.getTransKeypairName() != null) {
                        instance.setKeypairId(convertEntityService.getSSHKeyByNameAndDepartment(csVm.getTransKeypairName(), csVm.getDepartmentId()).getId());
                    }
                    if (csVm.getHostId() != null) {
                        csVm.setPodId(convertEntityService
                                .getPodIdByHost(convertEntityService.getHostId(csVm.getTransHostId())));
                    }
                    if (csVm.getTransHypervisor() != null) {
                        if (hypervisorService.findByName(csVm.getTransHypervisor()) != null) {
                            csVm.setHypervisorId(hypervisorService.findByName(csVm.getTransHypervisor()).getId());
                        }
                    }
                    instance.setName(csVm.getName());
                    if (csVm.getCpuCore() != null) {
                        instance.setCpuCore(csVm.getCpuCore());
                    }
                    if (csVm.getDomainId() != null) {
                        instance.setDomainId(csVm.getDomainId());
                    }
                    instance.setStatus(csVm.getStatus());
                    if (csVm.getZoneId() != null) {
                        instance.setZoneId(csVm.getZoneId());
                    }
                    if (csVm.getHostId() != null) {
                        instance.setHostId(csVm.getHostId());
                    }
                    if (csVm.getPodId() != null) {
                        instance.setPodId(csVm.getPodId());
                    }
                    if (csVm.getComputeOfferingId() != null) {
                        instance.setComputeOfferingId(csVm.getComputeOfferingId());
                    }
                    if (csVm.getCpuSpeed() != null) {
                        instance.setCpuSpeed(csVm.getCpuSpeed());
                    }
                    if (csVm.getMemory() != null) {
                        instance.setMemory(csVm.getMemory());
                    }
                    if (csVm.getCpuUsage() != null) {
                        instance.setCpuUsage(csVm.getCpuUsage());
                    }
                    instance.setDiskIoRead(csVm.getDiskIoRead());
                    instance.setHypervisorId(csVm.getHypervisorId());
                    instance.setDiskIoWrite(csVm.getDiskIoWrite());
                    instance.setDiskKbsRead(csVm.getDiskKbsRead());
                    instance.setDiskKbsWrite(csVm.getDiskKbsWrite());
                    instance.setNetworkKbsRead(csVm.getNetworkKbsRead());
                    instance.setNetworkKbsWrite(csVm.getNetworkKbsWrite());
                    instance.setPasswordEnabled(csVm.getPasswordEnabled());
                    if (csVm.getPassword() != null) {
                        instance.setPassword(csVm.getPassword());
                    }
                    instance.setIso(csVm.getIso());
                    instance.setIsoName(csVm.getIsoName());
                    if (csVm.getIpAddress() != null) {
                        instance.setIpAddress(csVm.getIpAddress());
                    }
                    if (csVm.getNetworkId() != null) {
                        instance.setNetworkId(csVm.getNetworkId());
                    }
                    if (csVm.getInstanceInternalName() != null) {
                        instance.setInstanceInternalName(csVm.getInstanceInternalName());
                    }
                    if (csVm.getVolumeSize() != null) {
                        instance.setVolumeSize(csVm.getVolumeSize());
                    }
                    instance.setDisplayName(csVm.getDisplayName());
                    if (csVm.getDepartmentId() != null) {
                        instance.setDepartmentId(csVm.getDepartmentId());
                    }
                    if (csVm.getProjectId() != null) {
                        instance.setProjectId(csVm.getProjectId());
                        instance.setDepartmentId(convertEntityService.getProjectById(csVm.getProjectId()).getDepartmentId());
                    }
                    if (csVm.getInstanceOwnerId() != null) {
                        instance.setInstanceOwnerId(csVm.getInstanceOwnerId());
                    }
                    if (instance.getTemplateId() != null) {
                        instance.setOsType(convertEntityService.getTemplateById(instance.getTemplateId()).getDisplayText());
                    }
                    instance.setTemplateName(csVm.getTemplateName());
                    LOGGER.debug("sync VM for ASYNC");
                    // VNC password set.
                    if (csVm.getPassword() != null) {
                        String strEncoded = Base64.getEncoder().encodeToString(secretKey.getBytes("utf-8"));
                        byte[] decodedKey = Base64.getDecoder().decode(strEncoded);
                        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
                        String encryptedPassword = new String(EncryptionUtil.encrypt(csVm.getPassword(), originalKey));
                        instance.setVncPassword(encryptedPassword);
                    }
                    // 3.2 If found, update the vm object in app db
                    vmIn = virtualMachineService.update(instance);
                }
            } else {
                vmInstance.setDomainId(convertEntityService.getDomainId(vmInstance.getTransDomainId()));
                vmInstance.setZoneId(convertEntityService.getZoneId(vmInstance.getTransZoneId()));
                vmInstance.setNetworkId(convertEntityService.getNetworkId(vmInstance.getTransNetworkId()));
                vmInstance.setProjectId(convertEntityService.getProjectId(vmInstance.getTransProjectId()));
                vmInstance.setHostId(convertEntityService.getHostId(vmInstance.getTransHostId()));
                vmInstance.setInstanceOwnerId(convertEntityService.getUserByName(vmInstance.getTransDisplayName(),
                        convertEntityService.getDomain(vmInstance.getTransDomainId())));
                if (vmInstance.getTransHypervisor() != null) {
                    if (hypervisorService.findByName(vmInstance.getTransHypervisor()) != null) {
                        vmInstance.setHypervisorId(hypervisorService.findByName(vmInstance.getTransHypervisor()).getId());
                    }
                }
                vmInstance.setDepartmentId(
                        convertEntityService.getDepartmentByUsernameAndDomains(vmInstance.getTransDepartmentId(),
                                convertEntityService.getDomain(vmInstance.getTransDomainId())));
                vmInstance.setTemplateId(convertEntityService.getTemplateId(vmInstance.getTransTemplateId()));
                vmInstance.setComputeOfferingId(
                        convertEntityService.getComputeOfferId(vmInstance.getTransComputeOfferingId()));
                if (vmInstance.getHostId() != null) {
                    vmInstance.setPodId(convertEntityService
                            .getPodIdByHost(convertEntityService.getHostId(vmInstance.getTransHostId())));
                }
                if (vmInstance.getTransKeypairName() != null) {
                    vmInstance.setKeypairId(convertEntityService.getSSHKeyByNameAndDepartment(vmInstance.getTransKeypairName(), vmInstance.getDepartmentId()).getId());
                }
                if (vmInstance.getTemplateId() != null) {
                    vmInstance.setOsType(convertEntityService.getTemplateById(vmInstance.getTemplateId()).getDisplayText());
                }
                vmInstance.setTemplateName(vmInstance.getTemplateName());

                vmIn = virtualMachineService.update(vmInstance);
            }
            if (eventObject.getString("commandEventType").equals(EventTypes.EVENT_VM_CREATE)) {
                Errors errors = new Errors(messageSource);
                this.assignNicTovM(vmIn);
                this.assignVolumeTovM(vmIn);
                if (volumeService.findByInstanceAndVolumeType(vmIn.getId()) != null) {
                    vmIn.setVolumeSize(volumeService.findByInstanceAndVolumeType(vmIn.getId()).getDiskSize());
                    vmIn = virtualMachineService.update(vmIn);
                }
                // Host update & internal name while create vm as user.
                if (vmIn.getHostId() == null) {
                    HashMap<String, String> vmMap = new HashMap<String, String>();
                    vmMap.put(CloudStackConstants.CS_ID, vmIn.getUuid());
                    configUtil.setServer(1L);
                    String response = cloudStackInstanceService.listVirtualMachines(CloudStackConstants.JSON, vmMap);
                    JSONArray vmListJSON = null;
                    JSONObject responseObject = new JSONObject(response)
                            .getJSONObject(CloudStackConstants.CS_LIST_VM_RESPONSE);
                    if (responseObject.has(CloudStackConstants.CS_VM)) {
                        vmListJSON = responseObject.getJSONArray(CloudStackConstants.CS_VM);
                        // 2. Iterate the json list, convert the single json
                        // entity to vm.
                        for (int i = 0, size = vmListJSON.length(); i < size; i++) {
                            // 2.1 Call convert by passing JSONObject to vm
                            // entity.
                            VmInstance CsVmInstance = VmInstance.convert(vmListJSON.getJSONObject(i));
                            // 2.2 Update vm host by transient variable.
                            vmIn.setHostId(convertEntityService.getHostId(CsVmInstance.getTransHostId()));
                            // 2.3 Update internal name.
                            vmIn.setInstanceInternalName(CsVmInstance.getInstanceInternalName());
                            if (vmIn.getHostId() != null) {
                                vmIn.setPodId(convertEntityService
                                        .getPodIdByHost(convertEntityService.getHostId(CsVmInstance.getTransHostId())));
                            }
                            // 3. Update vm for user vm creation.
                            vmIn = virtualMachineService.update(vmIn);
                        }
                    }
                }
                if (!convertEntityService.getDepartmentById(vmIn.getDepartmentId()).getType()
                        .equals(AccountType.USER)) {
                    updateResourceCountService.QuotaUpdateByResourceObject(vmIn, CS_Instance, vmIn.getDomainId(),
                            CS_Domain, Update);
                } else {
                    if (vmIn.getProjectId() != null) {
                        updateResourceCountService.QuotaUpdateByResourceObject(vmIn, CS_Instance, vmIn.getProjectId(),
                                CS_Project, Update);
                    }
                    if (vmIn.getDepartmentId() != null) {
                        updateResourceCountService.QuotaUpdateByResourceObject(vmIn, CS_Instance,
                                vmIn.getDepartmentId(), CS_Department, Update);
                    }
                    if (vmIn.getDomainId() != null) {
                        updateResourceCountService.QuotaUpdateByResourceObject(vmIn, CS_Instance, vmIn.getDomainId(),
                                CS_Domain, Update);
                    }
                }
                Event asyncJobEvent = new Event();
				// Event record for async call.
				asyncJobEvent.setEvent(eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE));
				asyncJobEvent.setEventDateTime(convertEntityService.getTimeService().getCurrentDateAndTime());
				asyncJobEvent.setEventOwnerId(convertEntityService.getOwnerByUuid(eventObject.getString(CloudStackConstants.CS_USER)));
				asyncJobEvent.setEventType(EventType.ASYNC);
				JSONObject json = new JSONObject(eventObject.getString(CloudStackConstants.CS_CMD_INFO));
				if (eventObject.getString(CloudStackConstants.CS_STATUS).equalsIgnoreCase(CloudStackConstants.CS_STATUS_FAILED)) {
					asyncJobEvent.setMessage(jobResult.getString(CloudStackConstants.CS_ERROR_TEXT));
					asyncJobEvent.setStatus(Event.Status.FAILED);
					asyncJobEvent.setResourceUuid(json.getString(CloudStackConstants.CS_UUID));
				} else {
					if (eventObject.has(CloudStackConstants.CS_INSTANCE_UUID)) {
						asyncJobEvent.setResourceUuid(eventObject.getString(CloudStackConstants.CS_INSTANCE_UUID));
					} else {
						asyncJobEvent.setResourceUuid(json.getString(CloudStackConstants.CS_UUID));
					}
					asyncJobEvent.setStatus(Event.Status.valueOf(eventObject.getString(CloudStackConstants.CS_STATUS).toUpperCase()));
				}
				asyncJobEvent.setEventStartId(json.getString(CloudStackConstants.CS_EVENT_ID));
				asyncJobEvent.setJobId(eventObject.getString(CloudStackConstants.CS_ASYNC_JOB_ID));
				// websocket record for async call.
				websocketService.handleEventAction(asyncJobEvent);
            }
            if (eventObject.getString("commandEventType").equals(EventTypes.EVENT_VM_DESTROY)) {
                if (!convertEntityService.getDepartmentById(vmIn.getDepartmentId()).getType()
                        .equals(AccountType.USER)) {
                    updateResourceCountService.QuotaUpdateByResourceObject(vmIn, CS_Destroy, vmIn.getDomainId(),
                            CS_Domain, Delete);
                } else {
                    if (vmIn.getProjectId() != null) {
                        updateResourceCountService.QuotaUpdateByResourceObject(vmIn, CS_Destroy, vmIn.getProjectId(),
                                CS_Project, Delete);
                    }
                    if (vmIn.getDepartmentId() != null) {
                        updateResourceCountService.QuotaUpdateByResourceObject(vmIn, CS_Destroy, vmIn.getDepartmentId(),
                                CS_Department, Delete);
                    }
                    if (vmIn.getDomainId() != null) {
                        updateResourceCountService.QuotaUpdateByResourceObject(vmIn, CS_Destroy, vmIn.getDomainId(),
                                CS_Domain, Delete);
                    }
                }
                Event asyncJobEvent = new Event();
				// Event record for async call.
				asyncJobEvent.setEvent(eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE));
				asyncJobEvent.setEventDateTime(convertEntityService.getTimeService().getCurrentDateAndTime());
				asyncJobEvent.setEventOwnerId(convertEntityService.getOwnerByUuid(eventObject.getString(CloudStackConstants.CS_USER)));
				asyncJobEvent.setEventType(EventType.ASYNC);
				JSONObject json = new JSONObject(eventObject.getString(CloudStackConstants.CS_CMD_INFO));
				if (eventObject.getString(CloudStackConstants.CS_STATUS).equalsIgnoreCase(CloudStackConstants.CS_STATUS_FAILED)) {
					asyncJobEvent.setMessage(jobResult.getString(CloudStackConstants.CS_ERROR_TEXT));
					asyncJobEvent.setStatus(Event.Status.FAILED);
					asyncJobEvent.setResourceUuid(json.getString(CloudStackConstants.CS_UUID));
				} else {
					if (eventObject.has(CloudStackConstants.CS_INSTANCE_UUID)) {
						asyncJobEvent.setResourceUuid(eventObject.getString(CloudStackConstants.CS_INSTANCE_UUID));
					} else {
						asyncJobEvent.setResourceUuid(json.getString(CloudStackConstants.CS_UUID));
					}
					asyncJobEvent.setStatus(Event.Status.valueOf(eventObject.getString(CloudStackConstants.CS_STATUS).toUpperCase()));
				}
				asyncJobEvent.setEventStartId(json.getString(CloudStackConstants.CS_EVENT_ID));
				asyncJobEvent.setJobId(eventObject.getString(CloudStackConstants.CS_ASYNC_JOB_ID));
				// websocket record for async call.
				websocketService.handleEventAction(asyncJobEvent);
            }
        }
    }

    /**
     * Get the default NIC when creating the template.
     *
     * @param vmInstance
     *            instance details
     * @throws ApplicationException
     *             unhandled application errors
     * @throws Exception
     *             cloudstack unhandled errors
     */
    public void assignNicTovM(VmInstance vmInstance) throws ApplicationException, Exception {
        HashMap<String, String> nicMap = new HashMap<String, String>();
        nicMap.put("virtualmachineid", vmInstance.getUuid());
        configUtil.setServer(1L);
        String listNic = cloudStackNicService.listNics(nicMap, "json");
        JSONArray nicListJSON = new JSONObject(listNic).getJSONObject("listnicsresponse").getJSONArray("nic");
        for (int i = 0; i < nicListJSON.length(); i++) {
            Nic nic = nicService.findbyUUID(nicListJSON.getJSONObject(i).getString("id"));
            if (nic != null) {
                nic.setSyncFlag(false);
                nic.setUuid(nicListJSON.getJSONObject(i).getString("id"));
                nic.setVmInstanceId(virtualMachineService
                        .findByUUID(nicListJSON.getJSONObject(i).getString("virtualmachineid")).getId());
                nic.setNetworkId(
                        networkService.findByUUID(nicListJSON.getJSONObject(i).getString("networkid")).getId());
                nic.setNetMask(nicListJSON.getJSONObject(i).getString("netmask"));
                nic.setGateway(nicListJSON.getJSONObject(i).getString("gateway"));
                nic.setIpAddress(nicListJSON.getJSONObject(i).getString("ipaddress"));
                nic.setIsDefault(nicListJSON.getJSONObject(i).getBoolean("isdefault"));
                nic.setIsActive(true);
                if (nicService.findbyUUID(nic.getUuid()) == null) {
                    nicService.save(nic);
                }
            } else {
                nic = new Nic();
                nic.setSyncFlag(false);
                nic.setUuid(nicListJSON.getJSONObject(i).getString("id"));
                nic.setVmInstanceId(virtualMachineService
                        .findByUUID(nicListJSON.getJSONObject(i).getString("virtualmachineid")).getId());
                nic.setNetworkId(
                        networkService.findByUUID(nicListJSON.getJSONObject(i).getString("networkid")).getId());
                nic.setNetMask(nicListJSON.getJSONObject(i).getString("netmask"));
                nic.setGateway(nicListJSON.getJSONObject(i).getString("gateway"));
                nic.setIpAddress(nicListJSON.getJSONObject(i).getString("ipaddress"));
                nic.setIsDefault(nicListJSON.getJSONObject(i).getBoolean("isdefault"));
                nic.setIsActive(true);
                if (nicService.findbyUUID(nic.getUuid()) == null) {
                    nicService.save(nic);
                }
            }
        }
    }

    /**
     * Get the default Volume when creating the template.
     *
     * @param vmInstance
     *            instance details
     * @throws ApplicationException
     *             unhandled application errors
     * @throws Exception
     *             cloudstack unhandled errors
     */
    public void assignVolumeTovM(VmInstance vmInstance) throws ApplicationException, Exception {
        HashMap<String, String> volumeMap = new HashMap<String, String>();
        volumeMap.put("virtualmachineid", vmInstance.getUuid());
        if (vmInstance.getProjectId() != null) {
            volumeMap.put("projectid", convertEntityService.getProjectById(vmInstance.getProjectId()).getUuid());
        } else {
            volumeMap.put("domainid", convertEntityService.getDomainById(vmInstance.getDomainId()).getUuid());
            volumeMap.put("account", convertEntityService.getDepartmentUsernameById(vmInstance.getDepartmentId()));
        }
        configUtil.setServer(1L);
        String listVolume = csVolumeService.listVolumes("json", volumeMap);
        JSONArray volumeListJSON = new JSONObject(listVolume).getJSONObject("listvolumesresponse")
                .getJSONArray("volume");
        for (int i = 0; i < volumeListJSON.length(); i++) {
            Volume volume = Volume.convert(volumeListJSON.getJSONObject(i));
            volume.setIsSyncFlag(false);
            volume.setZoneId(convertEntityService.getZoneId(volume.getTransZoneId()));
            volume.setDomainId(convertEntityService.getDomainId(volume.getTransDomainId()));
            volume.setStorageOfferingId(convertEntityService.getStorageOfferId(volume.getTransStorageOfferingId()));
            volume.setVmInstanceId(convertEntityService.getVmInstanceId(volume.getTransvmInstanceId()));
            if (volume.getTransProjectId() != null) {
                volume.setProjectId(convertEntityService.getProjectId(volume.getTransProjectId()));
                volume.setDepartmentId(projectService.find(volume.getProjectId()).getDepartmentId());
            } else {
                Domain domain = domainService.find(volume.getDomainId());
                volume.setDepartmentId(
                        convertEntityService.getDepartmentByUsernameAndDomains(volume.getTransDepartmentId(), domain));
            }
            volume.setDiskSizeFlag(true);
            if (volumeService.findByUUID(volume.getUuid()) == null) {
                volumeService.save(volume);
            }
        }
    }

    /**
     * Sync with CloudStack server Network from Asynchronous Job.
     *
     * @param jobResult
     *            job result
     * @param eventObject
     *            network event object
     * @throws ApplicationException
     *             unhandled application errors
     * @throws Exception
     *             cloudstack unhandled errors
     */
    public void asyncNetwork(JSONObject jobResult, JSONObject eventObject) throws ApplicationException, Exception {

        configUtil.setServer(1L);
        if (eventObject.getString("commandEventType").equals("NETWORK.UPDATE")) {
            Network csNetwork = Network.convert(jobResult.getJSONObject("network"));
            Network network = networkService.findByUUID(csNetwork.getUuid());
            network.setSyncFlag(false);
            if (csNetwork.getUuid().equals(network.getUuid())) {
                Network csNet = csNetwork;
                network.setName(csNet.getName());
                network.setDomainId(csNet.getDomainId());
                network.setZoneId(csNet.getZoneId());
                network.setDisplayText(csNet.getDisplayText());
                network.setDomainId(convertEntityService.getDomainId(csNet.getTransDomainId()));
                network.setZoneId(convertEntityService.getZoneId(csNet.getTransZoneId()));
                network.setNetworkOfferingId(
                        convertEntityService.getNetworkOfferingId(csNet.getTransNetworkOfferingId()));
                network.setDepartmentId(convertEntityService.getDepartmentByUsernameAndDomains(
                        csNet.getTransDepartmentId(), domainService.find(network.getDomainId())));
                network.setProjectId(convertEntityService.getProjectId(csNet.getTransProjectId()));
                networkService.update(network);
            }
        }
        if (eventObject.getString("commandEventType").equals("NETWORK.DELETE")) {
            JSONObject json = new JSONObject(eventObject.getString("cmdInfo"));
            Network network = networkService.findByUUID(json.getString("id"));
            network.setSyncFlag(false);
            network.setIsActive(false);
            Errors errors = new Errors(messageSource);
            networkService.softDelete(network);
            networkService.ipRelease(network);
            if (!convertEntityService.getDepartmentById(network.getDepartmentId()).getType().equals(AccountType.USER)) {
                updateResourceCountService.QuotaUpdateByResourceObject(network, CS_Network, network.getDomainId(),
                        CS_Domain, Delete);
            } else {
                if (network.getProjectId() != null) {
                    updateResourceCountService.QuotaUpdateByResourceObject(network, CS_Network, network.getProjectId(),
                            CS_Project, Delete);
                }
                if (network.getDepartmentId() != null) {
                    updateResourceCountService.QuotaUpdateByResourceObject(network, CS_Network,
                            network.getDepartmentId(), CS_Department, Delete);
                }
                if (network.getDomainId() != null) {
                    updateResourceCountService.QuotaUpdateByResourceObject(network, CS_Network, network.getDomainId(),
                            CS_Domain, Delete);
                }
            }
        }

        if (eventObject.getString("commandEventType").equals("NETWORK.RESTART")) {
            JSONObject json = new JSONObject(eventObject.getString("cmdInfo"));
            Network network = networkService.findByUUID(json.getString("id"));
            network.setSyncFlag(false);
            network.setNetworkRestart(true);
            networkService.update(network);
        }

        if (eventObject.getString("commandEventType").equals("NETWORK.RESTART")) {
            JSONObject json = new JSONObject(eventObject.getString("cmdInfo"));
            Network network = networkService.findByUUID(json.getString("id"));
            network.setSyncFlag(false);
            network.setNetworkRestart(true);
            networkService.update(network);
        }
    }

    /**
     * Sync function for the User.
     *
     * @param jobResult result
     * @param eventObject events
     * @throws ApplicationException exception
     * @throws Exception exception
     */
    public void asyncUser(JSONObject jobResult, JSONObject eventObject) throws ApplicationException, Exception {
        configUtil.setServer(1L);
        if (eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE).equals(USER_DISABLE)) {
            User csUser = User.convert(jobResult.getJSONObject(CloudStackConstants.CS_USER));
            User user = userService.findByUuIdAndIsActive(csUser.getUuid(), true);
            if (csUser.getUuid().equals(user.getUuid())) {
                User csUserResponse = csUser;
                user.setDomainId(convertEntityService.getDomainId(csUserResponse.getTransDomainId()));
                user.setDepartmentId(convertEntityService.getDepartmentId(csUserResponse.getTransDepartment()));
                user.setSyncFlag(false);
                user.setStatus(csUserResponse.getStatus());
                userService.update(user);
            }

        }
    }

    /**
     * Sync with CloudStack server Firewall from Asynchronous Job.
     *
     * @param jobResult
     *            job result
     * @param eventObject
     *            network event object
     * @throws ApplicationException
     *             unhandled application errors
     * @throws Exception
     *             cloudstack unhandled errors
     */
    public void asyncFirewall(JSONObject jobResult, JSONObject eventObject) throws ApplicationException, Exception {
        if (eventObject.getString("commandEventType").equals("FIREWALL.EGRESS.OPEN")) {
            FirewallRules csFirewallRule = FirewallRules.convert(jobResult.getJSONObject("firewallrule"),
                    FirewallRules.TrafficType.EGRESS, FirewallRules.Purpose.FIREWALL);
            FirewallRules egress = egressRuleService.findByUUID(csFirewallRule.getUuid());
            if (egress != null) {
                egress.setSyncFlag(false);
                if (csFirewallRule.getUuid().equals(egress.getUuid())) {
                    FirewallRules csFirewall = csFirewallRule;
                    egress.setUuid(csFirewall.getUuid());
                    egress.setProtocol(csFirewall.getProtocol());
                    egress.setDisplay(csFirewall.getDisplay());
                    egress.setSourceCIDR(csFirewall.getSourceCIDR());
                    egress.setState(csFirewall.getState());
                    egress.setStartPort(csFirewall.getStartPort());
                    egress.setEndPort(csFirewall.getEndPort());
                    egress.setIcmpCode(csFirewall.getIcmpCode());
                    egress.setIcmpMessage(csFirewall.getIcmpMessage());
                    egress.setPurpose(csFirewall.getPurpose());
                    egress.setTrafficType(csFirewall.getTrafficType());
                    egress.setNetworkId(convertEntityService.getNetworkByUuid(csFirewall.getTransNetworkId()));
                    egress.setDepartmentId(convertEntityService
                            .getNetworkById(convertEntityService.getNetworkByUuid(csFirewall.getTransNetworkId()))
                            .getDepartmentId());
                    egress.setProjectId(convertEntityService
                            .getNetworkById(convertEntityService.getNetworkByUuid(csFirewall.getTransNetworkId()))
                            .getProjectId());
                    egress.setDomainId(convertEntityService
                            .getNetworkById(convertEntityService.getNetworkByUuid(csFirewall.getTransNetworkId()))
                            .getDomainId());
                    egress.setIsActive(csFirewall.getIsActive());
                    egressRuleService.update(egress);
                }
            } else {
                csFirewallRule.setNetworkId(convertEntityService.getNetworkByUuid(csFirewallRule.getTransNetworkId()));
                csFirewallRule.setDepartmentId(convertEntityService
                        .getNetworkById(convertEntityService.getNetworkByUuid(csFirewallRule.getTransNetworkId()))
                        .getDepartmentId());
                csFirewallRule.setProjectId(convertEntityService
                        .getNetworkById(convertEntityService.getNetworkByUuid(csFirewallRule.getTransNetworkId()))
                        .getProjectId());
                csFirewallRule.setDomainId(convertEntityService
                        .getNetworkById(convertEntityService.getNetworkByUuid(csFirewallRule.getTransNetworkId()))
                        .getDomainId());
                egressRuleService.save(csFirewallRule);
            }
        }
        if (eventObject.getString("commandEventType").equals("FIREWALL.EGRESS.CLOSE")) {
            JSONObject json = new JSONObject(eventObject.getString("cmdInfo"));
            FirewallRules egressRule = egressRuleService.findByUUID(json.getString("id"));
            if (egressRule != null) {
                egressRule.setSyncFlag(false);
                egressRuleService.softDelete(egressRule);
            }
        }
        if (eventObject.getString("commandEventType").equals("FIREWALL.CLOSE")) {
            JSONObject json = new JSONObject(eventObject.getString("cmdInfo"));
            FirewallRules ingressRule = egressRuleService.findByUUID(json.getString("id"));
            if (ingressRule != null) {
                ingressRule.setSyncFlag(false);
                egressRuleService.softDelete(ingressRule);
            }
        }
        if (eventObject.getString("commandEventType").equals("FIREWALL.OPEN")) {
            FirewallRules csFirewallRule = FirewallRules.convert(jobResult.getJSONObject("firewallrule"),
                    FirewallRules.TrafficType.INGRESS, FirewallRules.Purpose.FIREWALL);
            FirewallRules ingress = egressRuleService.findByUUID(csFirewallRule.getUuid());
            if (ingress != null) {
                ingress.setSyncFlag(false);
                if (csFirewallRule.getUuid().equals(ingress.getUuid())) {
                    FirewallRules csFirewall = csFirewallRule;
                    ingress.setUuid(csFirewall.getUuid());
                    ingress.setProtocol(csFirewall.getProtocol());
                    ingress.setDisplay(csFirewall.getDisplay());
                    ingress.setSourceCIDR(csFirewall.getSourceCIDR());
                    ingress.setState(csFirewall.getState());
                    ingress.setStartPort(csFirewall.getStartPort());
                    ingress.setEndPort(csFirewall.getEndPort());
                    ingress.setIcmpCode(csFirewall.getIcmpCode());
                    ingress.setIcmpMessage(csFirewall.getIcmpMessage());
                    ingress.setPurpose(csFirewall.getPurpose());
                    ingress.setTrafficType(csFirewall.getTrafficType());
                    ingress.setNetworkId(convertEntityService.getNetworkByUuid(csFirewall.getTransNetworkId()));
                    ingress.setDepartmentId(convertEntityService
                            .getNetworkById(convertEntityService.getNetworkByUuid(csFirewall.getTransNetworkId()))
                            .getDepartmentId());
                    ingress.setProjectId(convertEntityService
                            .getNetworkById(convertEntityService.getNetworkByUuid(csFirewall.getTransNetworkId()))
                            .getProjectId());
                    ingress.setDomainId(convertEntityService
                            .getNetworkById(convertEntityService.getNetworkByUuid(csFirewall.getTransNetworkId()))
                            .getDomainId());
                    ingress.setIsActive(csFirewall.getIsActive());
                    ingress.setIpAddressId(ipService.findbyUUID(csFirewall.getTransIpaddressId()).getId());
                    egressRuleService.update(ingress);
                }
            } else {
                csFirewallRule.setNetworkId(convertEntityService.getNetworkByUuid(csFirewallRule.getTransNetworkId()));
                csFirewallRule.setDepartmentId(convertEntityService
                        .getNetworkById(convertEntityService.getNetworkByUuid(csFirewallRule.getTransNetworkId()))
                        .getDepartmentId());
                csFirewallRule.setProjectId(convertEntityService
                        .getNetworkById(convertEntityService.getNetworkByUuid(csFirewallRule.getTransNetworkId()))
                        .getProjectId());
                csFirewallRule.setDomainId(convertEntityService
                        .getNetworkById(convertEntityService.getNetworkByUuid(csFirewallRule.getTransNetworkId()))
                        .getDomainId());
                csFirewallRule.setIpAddressId(ipService.findbyUUID(csFirewallRule.getTransIpaddressId()).getId());
                egressRuleService.save(csFirewallRule);
            }
        }

    }

    /**
     * Sync with CloudStack server Ip address for sourcenat from Asynchronous
     * Job.
     *
     * @param jobResult
     *            job result
     * @param eventObject
     *            network event object
     * @throws ApplicationException
     *             unhandled application errors
     * @throws Exception
     *             cloudstack unhandled errors
     */
    public void asyncNat(JSONObject jobResult, JSONObject eventObject) throws ApplicationException, Exception {
        if (eventObject.getString("commandEventType").equals("STATICNAT.DISABLE")) {
            JSONObject json = new JSONObject(eventObject.getString("cmdInfo"));
            IpAddress ipAddress = ipService.findbyUUID(json.getString("ipaddressid"));
            if (ipAddress != null) {
                ipAddress.setSyncFlag(false);
                ipAddress.setIsStaticnat(false);
                ipAddress.setVmInstanceId(null);
                ipService.update(ipAddress);
            }
        }
    }

    /**
     * Sync with CloudStack server Ip address from Asynchronous Job.
     *
     * @param jobResult
     *            job result
     * @param eventObject
     *            network event object
     * @throws ApplicationException
     *             unhandled application errors
     * @throws Exception
     *             cloudstack unhandled errors
     */
    public void asyncIpAddress(JSONObject jobResult, JSONObject eventObject) throws ApplicationException, Exception {
        Errors errors = null;
        configUtil.setServer(1L);
        if (eventObject.getString("commandEventType").equals("NET.IPASSIGN")) {
            IpAddress ipaddress = IpAddress.convert(jobResult.getJSONObject("ipaddress"));
            IpAddress persistIp = ipService.findbyUUID(ipaddress.getUuid());
            if (persistIp != null) {
                persistIp.setSyncFlag(false);
                if (ipaddress.getUuid().equals(persistIp.getUuid())) {
                    IpAddress csIp = ipaddress;
                    persistIp.setUuid(csIp.getUuid());
                    persistIp.setIsStaticnat(csIp.getIsStaticnat());
                    persistIp.setVlan(csIp.getVlan());
                    persistIp.setPublicIpAddress(csIp.getPublicIpAddress());
                    persistIp.setDisplay(csIp.getDisplay());
                    persistIp.setIsSourcenat(csIp.getIsSourcenat());
                    persistIp.setState(IpAddress.State.ALLOCATED);
                    persistIp.setIsActive(true);
                    persistIp.setSyncFlag(false);
                    persistIp.setNetworkId(convertEntityService.getNetworkByUuid(csIp.getTransNetworkId()));
                    persistIp.setDepartmentId(convertEntityService
                            .getNetworkById(convertEntityService.getNetworkByUuid(csIp.getTransNetworkId()))
                            .getDepartmentId());
                    persistIp.setProjectId(convertEntityService
                            .getNetworkById(convertEntityService.getNetworkByUuid(csIp.getTransNetworkId()))
                            .getProjectId());
                    persistIp.setZoneId(convertEntityService
                            .getNetworkById(convertEntityService.getNetworkByUuid(csIp.getTransNetworkId()))
                            .getZoneId());
                    persistIp.setDomainId(convertEntityService
                            .getNetworkById(convertEntityService.getNetworkByUuid(csIp.getTransNetworkId()))
                            .getDomainId());
                    ipService.update(persistIp);
                    //Resource Count update
                    if (!convertEntityService.getDepartmentById(persistIp.getDepartmentId()).getType()
                            .equals(AccountType.USER)) {
                        updateResourceCountService.QuotaUpdateByResourceObject(persistIp, CS_IP, persistIp.getDomainId(), CS_Domain, Update);
                    } else {
                        if (persistIp.getProjectId() != null) {
                            updateResourceCountService.QuotaUpdateByResourceObject(persistIp, CS_IP, persistIp.getProjectId(), CS_Project, Update);
                        }
                        if (persistIp.getDepartmentId() != null) {
                            updateResourceCountService.QuotaUpdateByResourceObject(persistIp, CS_IP, persistIp.getDepartmentId(), CS_Department, Update);
                        }
                        if (persistIp.getDomainId() != null) {
                            updateResourceCountService.QuotaUpdateByResourceObject(persistIp, CS_IP, persistIp.getDomainId(), CS_Domain, Update);
                        }
                    }
                }
            } else {
                ipaddress.setState(IpAddress.State.ALLOCATED);
                ipaddress.setIsActive(ipaddress.getIsActive());
                ipaddress.setNetworkId(convertEntityService.getNetworkByUuid(ipaddress.getTransNetworkId()));
                ipaddress.setDepartmentId(convertEntityService
                        .getNetworkById(convertEntityService.getNetworkByUuid(ipaddress.getTransNetworkId()))
                        .getDepartmentId());
                ipaddress.setZoneId(convertEntityService
                        .getNetworkById(convertEntityService.getNetworkByUuid(ipaddress.getTransNetworkId()))
                        .getZoneId());
                ipaddress.setProjectId(convertEntityService
                        .getNetworkById(convertEntityService.getNetworkByUuid(ipaddress.getTransNetworkId()))
                        .getProjectId());
                ipaddress.setDomainId(convertEntityService
                        .getNetworkById(convertEntityService.getNetworkByUuid(ipaddress.getTransNetworkId()))
                        .getDomainId());
                ipService.save(ipaddress);
            }
        }
        if (eventObject.getString("commandEventType").equals("NET.IPRELEASE")) {
            JSONObject json = new JSONObject(eventObject.getString("cmdInfo"));
            IpAddress ipAddress = ipService.findbyUUID(json.getString("id"));
            if (ipAddress != null) {
                ipAddress.setSyncFlag(false);
                ipService.softDelete(ipAddress);
                //Resource Count delete
                if (!convertEntityService.getDepartmentById(ipAddress.getDepartmentId()).getType()
                        .equals(AccountType.USER)) {
                    updateResourceCountService.QuotaUpdateByResourceObject(ipAddress, CS_IP, ipAddress.getDomainId(), CS_Domain, Delete);
                } else {
                    if (ipAddress.getProjectId() != null) {
                        updateResourceCountService.QuotaUpdateByResourceObject(ipAddress, CS_IP, ipAddress.getProjectId(), CS_Project, Delete);
                    }
                    if (ipAddress.getDepartmentId() != null) {
                        updateResourceCountService.QuotaUpdateByResourceObject(ipAddress, CS_IP, ipAddress.getDepartmentId(), CS_Department, Delete);
                    }
                    if (ipAddress.getDomainId() != null) {
                        updateResourceCountService.QuotaUpdateByResourceObject(ipAddress, CS_IP, ipAddress.getDomainId(), CS_Domain, Delete);
                    }
                }
            }
        }

    }

    /**
     * Sync with CloudStack server Network from Asynchronous Job.
     *
     * @param eventObject
     *            template event object
     * @throws ApplicationException
     *             unhandled application errors
     * @throws Exception
     *             cloudstack unhandled errors
     */
    public void asyncTemplates(JSONObject eventObject) throws ApplicationException, Exception {

        if (eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE).equals(EventTypes.EVENT_TEMPLATE_DELETE)) {
            JSONObject json = new JSONObject(eventObject.getString(CloudStackConstants.CS_CMD_INFO));
            Template template = templateService.findByUUID(json.getString(CloudStackConstants.CS_ID));
            template.setSyncFlag(false);
            templateService.softDelete(template);
        }
        if (eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE)
                .equals(EventTypes.EVENT_ISO_TEMPLATE_DELETE)) {
            JSONObject json = new JSONObject(eventObject.getString(CloudStackConstants.CS_CMD_INFO));
            Template template = templateService.findByUUID(json.getString(CloudStackConstants.CS_ID));
            template.setSyncFlag(false);
            templateService.softDelete(template);
        }
    }

    /**
     * Sync with Cloud Server Volume.
     *
     * @param jobResult
     *            job result
     * @param eventObject
     *            volume event object
     * @throws ApplicationException
     *             unhandled application errors.
     * @throws Exception
     *             cloudstack unhandled errors.
     */
    public void asyncVolume(JSONObject jobResult, JSONObject eventObject) throws ApplicationException, Exception {

        if (eventObject.getString("commandEventType").equals("VOLUME.CREATE")
                || eventObject.getString("commandEventType").equals("VOLUME.UPLOAD")) {
            Errors errors = null;
            Volume volume = Volume.convert(jobResult.getJSONObject("volume"));
            volume.setIsSyncFlag(false);
            volume.setZoneId(convertEntityService.getZoneId(volume.getTransZoneId()));
            volume.setDomainId(convertEntityService.getDomainId(volume.getTransDomainId()));
            volume.setStorageOfferingId(convertEntityService.getStorageOfferId(volume.getTransStorageOfferingId()));
            volume.setVmInstanceId(convertEntityService.getVmInstanceId(volume.getTransvmInstanceId()));
            if (volume.getTransProjectId() != null) {
                volume.setProjectId(convertEntityService.getProjectId(volume.getTransProjectId()));
                volume.setDepartmentId(projectService.find(volume.getProjectId()).getDepartmentId());
            } else {
                Domain domain = domainService.find(volume.getDomainId());
                volume.setDepartmentId(
                        convertEntityService.getDepartmentByUsernameAndDomains(volume.getTransDepartmentId(), domain));
            }
            if (eventObject.getString("commandEventType").equals("VOLUME.UPLOAD")) {
                volume.setDiskSizeFlag(false);
                volume.setIsActive(true);
                // volume.setDiskSize(diskSize);
            } else {
                volume.setDiskSizeFlag(true);
            }
            if (jobResult.getJSONObject("volume").getString("state").equalsIgnoreCase("ALLOCATED")) {
                volume.setStatus(Status.ALLOCATED);
            }
            if (eventObject.getString("commandEventType").equals("VOLUME.UPLOAD")) {
                //Resource count update for upload volume.
                if (!convertEntityService.getDepartmentById(volume.getDepartmentId()).getType().equals(AccountType.USER)) {
                    updateResourceCountService.QuotaUpdateByResourceObject(volume, CS_UploadVolume, volume.getDomainId(),
                            CS_Domain, Update);
                } else {
                    if (volume.getProjectId() != null) {
                        updateResourceCountService.QuotaUpdateByResourceObject(volume, CS_UploadVolume, volume.getProjectId(),
                                CS_Project, Update);
                    }
                    if (volume.getDepartmentId() != null) {
                        updateResourceCountService.QuotaUpdateByResourceObject(volume, CS_UploadVolume,
                                volume.getDepartmentId(), CS_Department, Update);
                    }
                    if (volume.getDomainId() != null) {
                        updateResourceCountService.QuotaUpdateByResourceObject(volume, CS_UploadVolume, volume.getDomainId(),
                                CS_Domain, Update);
                    }
                }
            }
            if (volumeService.findByUUID(volume.getUuid()) == null) {
                volumeService.save(volume);
            }
            if (eventObject.getString("commandEventType").equals("VOLUME.CREATE")) {
                // Resource count update for volume.
                if (!convertEntityService.getDepartmentById(volume.getDepartmentId()).getType()
                        .equals(AccountType.USER)) {
                    updateResourceCountService.QuotaUpdateByResourceObject(volume, CS_Volume, volume.getDomainId(),
                            CS_Domain, Update);
                } else {
                    if (volume.getProjectId() != null) {
                        updateResourceCountService.QuotaUpdateByResourceObject(volume, CS_Volume, volume.getProjectId(),
                                CS_Project, Update);
                    }
                    if (volume.getDepartmentId() != null) {
                        updateResourceCountService.QuotaUpdateByResourceObject(volume, CS_Volume,
                                volume.getDepartmentId(), CS_Department, Update);
                    }
                    if (volume.getDomainId() != null) {
                        updateResourceCountService.QuotaUpdateByResourceObject(volume, CS_Volume, volume.getDomainId(),
                                CS_Domain, Update);
                    }
                }
            }
        }
        if (eventObject.getString("commandEventType").equals("VOLUME.ATTACH")
                || eventObject.getString("commandEventType").equals("VOLUME.DETACH")) {
            Volume csVolume = Volume.convert(jobResult.getJSONObject("volume"));
            Volume volume = volumeService.findByUUID(csVolume.getUuid());
            if (csVolume.getUuid().equals(volume.getUuid())) {
                volume.setIsSyncFlag(false);
                volume.setZoneId(convertEntityService.getZoneId(csVolume.getTransZoneId()));
                volume.setDomainId(convertEntityService.getDomainId(csVolume.getTransDomainId()));
                volume.setStorageOfferingId(
                        convertEntityService.getStorageOfferId(csVolume.getTransStorageOfferingId()));
                volume.setVmInstanceId(convertEntityService.getVmInstanceId(csVolume.getTransvmInstanceId()));
                if (csVolume.getTransProjectId() != null) {
                    volume.setProjectId(convertEntityService.getProjectId(csVolume.getTransProjectId()));
                    volume.setDepartmentId(projectService.find(volume.getProjectId()).getDepartmentId());
                } else {
                    Domain domain = domainService.find(volume.getDomainId());
                    volume.setDepartmentId(convertEntityService
                            .getDepartmentByUsernameAndDomains(csVolume.getTransDepartmentId(), domain));
                }
                volume.setName(csVolume.getName());
                volume.setVolumeType(csVolume.getVolumeType());
                volume.setIsActive(true);
                if (volume.getDiskSize() != null) {
                    volume.setDiskSize(csVolume.getDiskSize());
                } else {
                    volume.setDiskSize(csVolume.getStorageOffering().getDiskSize());
                }
                volume.setDiskSizeFlag(true);
                volume.setChecksum(csVolume.getChecksum());
                volume.setStatus(csVolume.getStatus());
                volume.setDiskMaxIops(csVolume.getDiskMaxIops());
                volume.setDiskMinIops(csVolume.getDiskMinIops());
                volume.setCreatedDateTime(csVolume.getCreatedDateTime());
                volume.setUpdatedDateTime(csVolume.getUpdatedDateTime());
                volumeService.update(volume);

            }
        }
    }

    /**
     * Sync with Cloud Server Volume.
     *
     * @param jobResult
     *            job result
     * @param eventObject
     *            volume event object
     * @throws ApplicationException
     *             unhandled application errors.
     * @throws Exception
     *             cloudstack unhandled errors.
     */
    public void asyncNic(JSONObject jobResult, JSONObject eventObject) throws ApplicationException, Exception {

        if (eventObject.getString("commandEventType").equals("NIC.CREATE")
                || eventObject.getString("commandEventType").equals("NIC.UPDATE")
                || eventObject.getString("commandEventType").equals("NIC.DELETE")) {

            JSONArray nicListJSON = jobResult.getJSONObject("virtualmachine").getJSONArray("nic");
            List<Nic> nicList = new ArrayList<Nic>();
            for (int i = 0, size = nicListJSON.length(); i < size; i++) {
                Nic nic = Nic.convert(nicListJSON.getJSONObject(i));
                nic.setVmInstanceId(convertEntityService
                        .getVmInstanceId(JsonUtil.getStringValue(jobResult.getJSONObject("virtualmachine"), "id")));
                nic.setNetworkId(convertEntityService.getNetworkId(nic.getTransNetworkId()));
                nicList.add(nic);
            }
            HashMap<String, Nic> csNicMap = (HashMap<String, Nic>) Nic.convert(nicList);
            List<Nic> appnicList = nicService.findByInstance(convertEntityService
                    .getVmInstanceId(JsonUtil.getStringValue(jobResult.getJSONObject("virtualmachine"), "id")));

            for (Nic nic : appnicList) {
                nic.setSyncFlag(false);
                LOGGER.debug("Total rows updated : " + (appnicList.size()));
                if (csNicMap.containsKey(nic.getUuid())) {
                    Nic csNic = csNicMap.get(nic.getUuid());
                    nic.setUuid(csNic.getUuid());
                    nic.setIsDefault(csNic.getIsDefault());
                    nicService.update(nic);
                    csNicMap.remove(nic.getUuid());
                } else {
                    nicService.softDelete(nic);
                }
            }
            for (String key : csNicMap.keySet()) {
                LOGGER.debug("Syncservice Nic uuid:");
                nicService.save(csNicMap.get(key));
            }
        }
        if (eventObject.getString("commandEventType").equals("NIC.SECONDARY.IP.ASSIGN")) {
            if (eventObject.getString("commandEventType").equals("NIC.SECONDARY.IP.ASSIGN")) {
                VmIpaddress csVmIpaddress = VmIpaddress.convert(jobResult.getJSONObject("nicsecondaryip"));
                csVmIpaddress.setSyncFlag(false);
                csVmIpaddress.setUuid(csVmIpaddress.getUuid());
                csVmIpaddress.setGuestIpAddress(csVmIpaddress.getGuestIpAddress());
                csVmIpaddress.setNicId(convertEntityService.getNic(csVmIpaddress.getTransNicId()).getId());
                csVmIpaddress.setVmInstanceId(convertEntityService.getNic(csVmIpaddress.getTransNicId()).getVmInstanceId());
            }

        }
        if (eventObject.getString("commandEventType").equals("NIC.SECONDARY.IP.UNASSIGN")) {
            if (eventObject.getString("commandEventType").equals("NIC.SECONDARY.IP.UNASSIGN")) {
                LOGGER.debug("Nic secondary IP Implemented");
                if (eventObject.getString("commandEventType").equals("NIC.SECONDARY.IP.UNASSIGN")) {
                    JSONObject json = new JSONObject(eventObject.getString("cmdInfo"));
                    VmIpaddress vmIpAddress = vmIpService.findByUUID((json.getString("id")));
                    if (vmIpAddress != null) {
                        vmIpAddress.setSyncFlag(false);
                        vmIpService.softDelete(vmIpAddress);
                        Nic nic = new Nic();
                        nicService.releaseSecondaryIP(nic, vmIpAddress.getId());
                        nic.setSyncFlag(false);
                        nicService.update(nic);
                    }
                }

            }
        }
    }

    /**
     * Sync with Cloud Server Network port forwarding.
     *
     * @param jobResult
     *            job result
     * @param eventObject
     *            volume event object
     * @throws ApplicationException
     *             unhandled application errors.
     * @throws Exception
     *             cloudstack unhandled errors.
     */
    public void asyncNet(JSONObject jobResult, JSONObject eventObject) throws ApplicationException, Exception {

        if (eventObject.getString("commandEventType").equals("NET.RULEADD")) {
            PortForwarding portForwarding = PortForwarding.convert(jobResult.getJSONObject("portforwardingrule"));
            portForwarding.setVmInstanceId(convertEntityService.getVmInstanceId(portForwarding.getTransvmInstanceId()));
            portForwarding.setNetworkId(convertEntityService.getNetworkId(portForwarding.getTransNetworkId()));
            portForwarding.setIpAddressId(convertEntityService.getIpAddressId(portForwarding.getTransIpAddressId()));
            if (portForwardingService.findByUUID(portForwarding.getUuid()) == null) {
                portForwardingService.save(portForwarding);
                firewallRules(jobResult, FirewallRules.Purpose.PORTFORWARDING);
            }
        }

        if (eventObject.getString("commandEventType").equals("NET.RULEDELETE")) {
            JSONObject json = new JSONObject(eventObject.getString("cmdInfo"));
            PortForwarding portForwarding = portForwardingService.findByUUID(json.getString("id"));
            if (portForwarding != null) {
                portForwarding.setSyncFlag(false);
                portForwardingService.softDelete(portForwarding);
            }

            // Delete the port forwarding firewall rule
            FirewallRules firewallRules = egressRuleService.findByUUID(json.getString("id"));
            if (firewallRules != null) {
                firewallRules.setSyncFlag(false);
                egressRuleService.deleteFirewallIngressRule(firewallRules);
            }
        }

        if (eventObject.getString("commandEventType").equals("NET.IPASSIGN")
                || eventObject.getString("commandEventType").equals("NET.IPRELEASE")) {
            asyncIpAddress(jobResult, eventObject);
        }

    }

    /**
     * Sync with Cloud Server snapshot.
     *
     * @param jobResult from ACS.
     * @param eventObject snapshot event object.
     * @throws ApplicationException unhandled application errors.
     * @throws Exception ACS unhandled errors.
     */
    @SuppressWarnings("unused")
    public void asyncSnapshot(JSONObject jobResult, JSONObject eventObject) throws ApplicationException, Exception {

         if (eventObject.getString("commandEventType").equals("SNAPSHOT.CREATE") || eventObject.getString("commandEventType").equals("SNAPSHOT.REVERT") || eventObject.getString("commandEventType").equals(EventTypes.EVENT_UNKNOWN)) {
             Snapshot snapShot = Snapshot.convert(jobResult.getJSONObject("snapshot"));
             snapShot.setZoneId(convertEntityService.getZoneId(snapShot.getTransZoneId()));
             snapShot.setDomainId(convertEntityService.getDomainId(snapShot.getTransDomainId()));
             snapShot.setVolumeId(convertEntityService.getVolumeId(snapShot.getTransVolumeId()));
             snapShot.setDepartmentId(
                     convertEntityService.getDepartmentByUsernameAndDomains(snapShot.getTransDepartmentId(),
                             convertEntityService.getDomain(snapShot.getTransDomainId())));
             snapShot.setSyncFlag(false);
             if(snapShotService.findByUUID(snapShot.getUuid()) == null) {
                 snapShotService.save(snapShot);
             }

         }
          if (eventObject.getString("commandEventType").equals("SNAPSHOT.DELETE")) {
             JSONObject json = new JSONObject(eventObject.getString("cmdInfo"));
             Snapshot snapshot = snapShotService.findByUUID(json.getString("id"));
             if (snapshot != null) {
                 snapshot.setSyncFlag(false);
                 snapShotService.softDelete(snapshot);
             }
          }
    }

    /**
     * Sync with Cloud Server VM snapshot.
     *
     * @param jobResult job result
     * @param eventObject VM snapshot event object
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    public void asyncVMSnapshot(JSONObject jobResult, JSONObject eventObject) throws ApplicationException, Exception {

         if (eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE).equals(EventTypes.EVENT_VM_SNAPSHOT_CREATE)) {
             VmSnapshot vmSnapshot = VmSnapshot.convert(jobResult.getJSONObject(CloudStackConstants.CS_VM_SNAPSHOT));
             vmSnapshot.setVmId(convertEntityService.getVmInstanceId(vmSnapshot.getTransvmInstanceId()));
             vmSnapshot.setDomainId(convertEntityService.getVm(vmSnapshot.getTransvmInstanceId()).getDomainId());
             vmSnapshot.setOwnerId(convertEntityService.getVm(vmSnapshot.getTransvmInstanceId()).getInstanceOwnerId());
             vmSnapshot.setZoneId(convertEntityService.getVm(vmSnapshot.getTransvmInstanceId()).getZoneId());
             List<VmSnapshot> vmSnapshotList = vmSnapshotService.findByVmInstance(vmSnapshot.getVmId(), false);
             for (VmSnapshot vmSnap : vmSnapshotList) {
                 if (vmSnap.getIsCurrent()) {
                     vmSnap.setIsCurrent(false);
                     vmSnap.setSyncFlag(false);
                     vmSnapshotService.save(vmSnap);
                 }
             }

             vmSnapshot.setSyncFlag(false);
             if (vmSnapshotService.findByUUID(vmSnapshot.getUuid()) == null) {
                 vmSnapshotService.save(vmSnapshot);
             }
         }

         if (eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE).equals(EventTypes.EVENT_VM_SNAPSHOT_REVERT)) {
             JSONObject json = new JSONObject(eventObject.getString(CloudStackConstants.CS_CMD_INFO));
             VmSnapshot vmsnapshot = vmSnapshotService.findByUUID(json.getString(CloudStackConstants.CS_VM_SNAPSHOT_ID));
             List<VmSnapshot> vmSnapshotList = vmSnapshotService.findByVmInstance(vmsnapshot.getVmId(), false);
             for (VmSnapshot vmSnap : vmSnapshotList) {
                 if (vmSnap.getIsCurrent()) {
                     vmSnap.setIsCurrent(false);
                     vmSnap.setSyncFlag(false);
                     vmSnapshotService.save(vmSnap);
                 }
             }
             vmsnapshot.setStatus(ck.panda.domain.entity.VmSnapshot.Status.valueOf(EventTypes.EVENT_READY));
             vmsnapshot.setIsCurrent(true);
             vmsnapshot.setSyncFlag(false);
             vmSnapshotService.save(vmsnapshot);
         }

         if (eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE).equals(EventTypes.EVENT_VM_SNAPSHOT_DELETE)) {
             JSONObject json = new JSONObject(eventObject.getString(CloudStackConstants.CS_CMD_INFO));
             VmSnapshot vmsnapshot = vmSnapshotService.findByUUID(json.getString(CloudStackConstants.CS_VM_SNAPSHOT_ID));
             vmsnapshot.setIsRemoved(true);
             vmsnapshot.setStatus(ck.panda.domain.entity.VmSnapshot.Status.Expunging);
             vmsnapshot.setSyncFlag(false);
             vmSnapshotService.save(vmsnapshot);
         }
    }

    /**
     * Sync VPN details from the Cloud Server.
     *
     * @param jobResult job result
     * @param eventObject VM snapshot event object
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    public void asyncVpn(JSONObject jobResult, JSONObject eventObject) throws ApplicationException, Exception {

         if (eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE).equals(EventTypes.EVENT_REMOTE_ACCESS_CREATE)) {
             JSONObject jobresultReponse = jobResult.getJSONObject(CloudStackConstants.CS_REMOTE_ACCESS_VPN);
             IpAddress ipAddress = ipService.findbyUUID(jobresultReponse.getString(CloudStackConstants.CS_PUBLIC_IP_ID));
             ipAddress.setVpnUuid(jobresultReponse.getString(CloudStackConstants.CS_ID));
             ipAddress.setVpnPresharedKey(convertEncryptedKey(jobresultReponse.getString(CloudStackConstants.CS_PRESHARED_KEY)));
             ipAddress.setVpnState(VpnState.valueOf(jobresultReponse.getString(CloudStackConstants.CS_STATE).toUpperCase()));
             ipAddress.setVpnForDisplay(jobresultReponse.getBoolean(CloudStackConstants.CS_FOR_DISPLAY));
             ipAddress.setSyncFlag(false);
             ipService.save(ipAddress);
         }

         if (eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE).equals(EventTypes.EVENT_REMOTE_ACCESS_DESTROY)) {
             JSONObject json = new JSONObject(eventObject.getString(CloudStackConstants.CS_CMD_INFO));
             IpAddress ipAddress = ipService.findbyUUID(json.getString(CloudStackConstants.CS_PUBLIC_IP_ID));
             ipAddress.setVpnState(VpnState.DISABLED);
             ipAddress.setSyncFlag(false);
             ipService.save(ipAddress);
         }

         if (eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE).equals(EventTypes.EVENT_VPN_USER_ADD)) {
             JSONObject jobresultReponse = jobResult.getJSONObject(CloudStackConstants.CS_VPN_USER);
             VpnUser vpnUser = new VpnUser();
             vpnUser.setUuid(jobresultReponse.getString(CloudStackConstants.CS_ID));
             vpnUser.setUserName(jobresultReponse.getString(CloudStackConstants.CS_USER_NAME));
             vpnUser.setDomainId(convertEntityService.getDomainId(jobresultReponse.getString(CloudStackConstants.CS_DOMAIN_ID)));
             vpnUser.setDepartmentId(convertEntityService.getDepartmentByUsername(jobresultReponse.getString(CloudStackConstants.CS_ACCOUNT), vpnUser.getDomainId()));
             if (jobresultReponse.has(CloudStackConstants.CS_PROJECT_ID)) {
                 vpnUser.setProjectId(convertEntityService.getProjectId(jobresultReponse.getString(CloudStackConstants.CS_PROJECT_ID)));
             }
             if (vpnUserService.findbyUUID(vpnUser.getUuid()) == null) {
                 vpnUser.setSyncFlag(false);
                 vpnUserService.save(vpnUser);
             }
         }

         if (eventObject.getString(CloudStackConstants.CS_COMMAND_EVENT_TYPE).equals(EventTypes.EVENT_VPN_USER_REMOVE)) {
             JSONObject json = new JSONObject(eventObject.getString(CloudStackConstants.CS_CMD_INFO));
             VpnUser vpnUser = vpnUserService.findbyDomainWithAccountAndUser(json.getString(CloudStackConstants.CS_USER_NAME),
                 json.getString(CloudStackConstants.CS_ACCOUNT),
                 json.getString(CloudStackConstants.CS_DOMAIN_ID));
             if (vpnUser != null) {
                 vpnUser.setSyncFlag(false);
                 vpnUserService.softDelete(vpnUser);
             }
         }
    }

    /**
     * Convert key value as encrypted format.
     *
     * @param value secret value.
     * @return encrypted value
     * @throws Exception unhandled errors.
     */
    private String convertEncryptedKey(String value) throws Exception {
        // Set password from CS for an instance with AES encryption.
        String encryptedValue = "";
        if (value != null) {
            String strEncoded = Base64.getEncoder().encodeToString(secretKey.getBytes(GenericConstants.CHARACTER_ENCODING));
            byte[] decodedKey = Base64.getDecoder().decode(strEncoded);
            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, GenericConstants.ENCRYPT_ALGORITHM);
            encryptedValue = new String(EncryptionUtil.encrypt(value, originalKey));
        }
        return encryptedValue;
    }

    /**
     * Sync with Cloud Server Network Load Balancer.
     *
     * @param jobResult
     *            job result
     * @param eventObject
     *            Load Balancer event object
     * @throws ApplicationException
     *             unhandled application errors.
     * @throws Exception
     *             cloudstack unhandled errors.
     */
    @SuppressWarnings("unused")
    public void asyncLb(JSONObject jobResult, JSONObject eventObject) throws ApplicationException, Exception {

        if (eventObject.getString("commandEventType").equals("LB.CREATE")) {
            LoadBalancerRule loadBalancerRule = LoadBalancerRule.convert(jobResult.getJSONObject("loadbalancer"));
            loadBalancerRule.setNetworkId(convertEntityService.getNetworkId(loadBalancerRule.getTransNetworkId()));
            loadBalancerRule
                    .setIpAddressId(convertEntityService.getIpAddressId(loadBalancerRule.getTransIpAddressId()));
            loadBalancerRule.setZoneId(convertEntityService.getZoneId(loadBalancerRule.getTransZoneId()));
            loadBalancerRule.setDomainId(convertEntityService.getDomainId(loadBalancerRule.getTransDomainId()));
            if (loadBalancerService.findByUUID(loadBalancerRule.getUuid()) == null) {
                loadBalancerService.save(loadBalancerRule);
                firewallRules(jobResult, FirewallRules.Purpose.LOADBALANCING);
            }
        }

        if (eventObject.getString("commandEventType").equals("LB.STICKINESSPOLICY.CREATE")) {
            JSONObject stickyResult = jobResult.getJSONObject(CloudStackConstants.CS_STICKY_POLICIES);
            JSONArray stickyPolicy = stickyResult.getJSONArray(CloudStackConstants.CS_STICKY_POLICY);
            for (int j = 0, sizes = stickyPolicy.length(); j < sizes; j++) {
                JSONObject json = (JSONObject) stickyPolicy.get(j);
                LoadBalancerRule lbRule = loadBalancerService.findByUUID(stickyResult.getString(CloudStackConstants.CS_LB_RULE_ID));

                LbStickinessPolicy loadBalanceRule = null;
                if (lbRule.getLbPolicyId() != null) {
                    loadBalanceRule = lbPolicyService.find(lbRule.getLbPolicyId());
                }  else if(lbPolicyService.findByUUID(json.getString(CloudStackConstants.CS_ID)) != null) {
                    loadBalanceRule = lbPolicyService.findByUUID(json.getString(CloudStackConstants.CS_ID));
                }
                else {
                    loadBalanceRule = new LbStickinessPolicy();
                }
                loadBalanceRule.setUuid(json.getString(CloudStackConstants.CS_ID));
                loadBalanceRule.setStickinessMethod(StickinessMethod.valueOf(json.getString(CloudStackConstants.CS_METHOD_NAME)));
                loadBalanceRule.setStickinessName(json.getString(CloudStackConstants.CS_NAME));
                loadBalanceRule.setSyncFlag(false);
                if (json.has(CloudStackConstants.CS_PARAMS)) {
                    JSONObject paramsResponse = json.getJSONObject(CloudStackConstants.CS_PARAMS);
                    loadBalanceRule.setStickyTableSize(JsonUtil.getStringValue(paramsResponse, CloudStackConstants.CS_TABLE_SIZE));
                    loadBalanceRule.setStickyLength(JsonUtil.getStringValue(paramsResponse, CloudStackConstants.CS_LENGTH));
                    loadBalanceRule.setStickyExpires(JsonUtil.getStringValue(paramsResponse, CloudStackConstants.CS_EXPIRE));
                    loadBalanceRule.setStickyMode(JsonUtil.getStringValue(paramsResponse, CloudStackConstants.CS_MODE));
                    loadBalanceRule.setStickyPrefix(JsonUtil.getBooleanValue(paramsResponse, CloudStackConstants.CS_PREFIX));
                    loadBalanceRule.setStickyRequestLearn(JsonUtil.getBooleanValue(paramsResponse, CloudStackConstants.CS_REQUEST_LEARN));
                    loadBalanceRule.setStickyIndirect(JsonUtil.getBooleanValue(paramsResponse, CloudStackConstants.CS_INDIRECT));
                    loadBalanceRule.setStickyNoCache(JsonUtil.getBooleanValue(paramsResponse, CloudStackConstants.CS_NO_CACHE));
                    loadBalanceRule.setStickyPostOnly(JsonUtil.getBooleanValue(paramsResponse, CloudStackConstants.CS_POST_ONLY));
                    loadBalanceRule.setStickyHoldTime(JsonUtil.getStringValue(paramsResponse, CloudStackConstants.CS_HOLD_TIME));
                    loadBalanceRule.setStickyCompany(JsonUtil.getStringValue(paramsResponse, CloudStackConstants.CS_DOMAIN));
                    loadBalanceRule.setCookieName(JsonUtil.getStringValue(paramsResponse, CloudStackConstants.CS_COOKIE));
                }
                if (lbPolicyService.findByUUID(loadBalanceRule.getUuid()) == null) {
                        LbStickinessPolicy lbPolicy = lbPolicyService.save(loadBalanceRule);
                         lbRule.setLbPolicyId(lbPolicy.getId());
                         lbRule.setLbPolicy(lbPolicy);
                         lbRule.setSyncFlag(false);
                         loadBalancerService.save(lbRule);
                }
                else {
                    loadBalanceRule.setSyncFlag(false);
                    LbStickinessPolicy lbPolicy = lbPolicyService.update(loadBalanceRule);
                     //Assign lb policy to rule
                    lbRule.setLbPolicyId(lbPolicy.getId());
                    lbRule.setLbPolicy(lbPolicy);
                    lbRule.setSyncFlag(false);
                    loadBalancerService.save(lbRule);
                }

            }
        }
        if (eventObject.getString("commandEventType").equals("LB.UPDATE")) {
            LoadBalancerRule csLoadBalancer = LoadBalancerRule.convert(jobResult.getJSONObject("loadbalancer"));
            LoadBalancerRule loadBalancer = loadBalancerService.findByUUID(csLoadBalancer.getUuid());
            loadBalancer.setSyncFlag(false);
            if (csLoadBalancer.getUuid().equals(loadBalancer.getUuid())) {
                LoadBalancerRule csLb = csLoadBalancer;
                loadBalancer.setName(csLb.getName());
                loadBalancer.setAlgorithm(csLb.getAlgorithm());
                loadBalancerService.update(loadBalancer);
            }
        }

        if (eventObject.getString("commandEventType").equals("LB.ASSIGN.TO.RULE")
                || eventObject.getString("commandEventType").equals("LB.REMOVE.FROM.RULE")) {
            JSONObject json = new JSONObject(eventObject.getString("cmdInfo"));

            LoadBalancerRule loadBalancer = loadBalancerService.findByUUID(json.getString("id"));
            if (loadBalancer != null) {
                List<VmIpaddress> vmList = new ArrayList<VmIpaddress>();
                for (int i = 0; i < i + 1; i++) {
                    if (json.has("vmidipmap[" + i + "].vmip")) {
                        VmInstance vmId = convertEntityService.getVm(json.getString("vmidipmap[" + i + "].vmid"));
                        VmIpaddress vmIp = vmIpService.findByIPAddress(json.getString("vmidipmap[" + i + "].vmip"),
                                vmId.getId());
                        vmIp.setGuestIpAddress(json.getString("vmidipmap[" + i + "].vmip"));
                        vmIp.setVmInstanceId(vmId.getId());
                        vmList.add(vmIp);
                    }
                }
            loadBalancer.setVmIpAddress(vmList);
            loadBalancer.setSyncFlag(false);
            loadBalancer.setState(State.ACTIVE);
            loadBalancerService.save(loadBalancer);
                HashMap<String, String> loadBalancerInstanceMap = new HashMap<String, String>();
                loadBalancerInstanceMap.put("lbvmips", "true");
                loadBalancerInstanceMap.put("listall", "true");
                configUtil.setServer(1L);
                String response = cloudStackLoadBalancerService.listLoadBalancerRuleInstances(json.getString("id"),
                        "json", loadBalancerInstanceMap);
                JSONArray vmListJSON = null;
                JSONObject responseObject = new JSONObject(response)
                        .getJSONObject("listloadbalancerruleinstancesresponse");
                if (responseObject.has("lbrulevmidip")) {
                    vmListJSON = responseObject.getJSONArray("lbrulevmidip");
                    List<VmInstance> newVmInstance = new ArrayList<VmInstance>();
                    for (int i = 0; i < vmListJSON.length(); i++) {
                        VmInstance vmInstance = virtualMachineService.findByUUID(
                                vmListJSON.getJSONObject(i).getJSONObject("loadbalancerruleinstance").getString("id"));
                        newVmInstance.add(vmInstance);
                    }
                    loadBalancer.setVmInstanceList(newVmInstance);
                } else {
                    loadBalancer.setVmInstanceList(null);
                }
                loadBalancer.setSyncFlag(false);
                loadBalancerService.update(loadBalancer);
            }
        }

        if (eventObject.getString("commandEventType").equals("LB.DELETE")) {
            JSONObject json = new JSONObject(eventObject.getString("cmdInfo"));
            LoadBalancerRule loadBalancer = loadBalancerService.findByUUID(json.getString("id"));
            if (loadBalancer != null) {
                loadBalancer.setSyncFlag(false);
                loadBalancer.setVmInstanceList(null);
                loadBalancerService.softDelete(loadBalancer);
            }

            // Delete the load balancer firewall rule
            FirewallRules firewallRules = egressRuleService.findByUUID(json.getString("id"));
            if (firewallRules != null) {
                firewallRules.setSyncFlag(false);
                egressRuleService.deleteFirewallIngressRule(firewallRules);
            }
        }
    }

    /**
     * Sync with Cloud Server Network Firewall Rules.
     *
     * @param jobResult
     *            job result
     * @param purpose
     *            of the firewall
     * @throws ApplicationException
     *             unhandled application errors.
     * @throws Exception
     *             cloudstack unhandled errors.
     */
    public void firewallRules(JSONObject jobResult, FirewallRules.Purpose purpose)
            throws ApplicationException, Exception {
        FirewallRules csFirewallRule = null;
        if (purpose == Purpose.LOADBALANCING) {
            csFirewallRule = FirewallRules.convert(jobResult.getJSONObject("loadbalancer"), null, purpose);
        } else {
            csFirewallRule = FirewallRules.convert(jobResult.getJSONObject("portforwardingrule"), null, purpose);
        }
        FirewallRules loadBalancer = egressRuleService.findByUUID(csFirewallRule.getUuid());
        if (loadBalancer == null) {
            csFirewallRule.setNetworkId(convertEntityService.getNetworkByUuid(csFirewallRule.getTransNetworkId()));
            csFirewallRule.setDepartmentId(convertEntityService
                    .getNetworkById(convertEntityService.getNetworkByUuid(csFirewallRule.getTransNetworkId()))
                    .getDepartmentId());
            csFirewallRule.setProjectId(convertEntityService
                    .getNetworkById(convertEntityService.getNetworkByUuid(csFirewallRule.getTransNetworkId()))
                    .getProjectId());
            csFirewallRule.setDomainId(convertEntityService
                    .getNetworkById(convertEntityService.getNetworkByUuid(csFirewallRule.getTransNetworkId()))
                    .getDomainId());
            egressRuleService.save(csFirewallRule);
        }
    }

    /**
     * Sync with CloudStack server Network offering.
     *
     * @param uuid
     *            network offering response event
     * @throws ApplicationException
     *             unhandled application errors.
     * @throws Exception
     *             cloudstack unhandled errors
     */
    @Override
    public void asyncNetworkOffering(ResponseEvent eventObject) throws ApplicationException, Exception {

        if (eventObject.getEvent().equals("NETWORK.OFFERING.EDIT")) {
            HashMap<String, String> networkOfferingMap = new HashMap<String, String>();
            networkOfferingMap.put("id", eventObject.getEntityuuid());
            configUtil.setServer(1L);
            String response = csNetworkOfferingService.listNetworkOfferings("json", networkOfferingMap);
            JSONArray networkOfferingListJSON = null;
            JSONObject responseObject = new JSONObject(response).getJSONObject("listnetworkofferingsresponse");
            if (responseObject.has("networkoffering")) {
                networkOfferingListJSON = responseObject.getJSONArray("networkoffering");
                NetworkOffering csNetworkOffering = NetworkOffering.convert(networkOfferingListJSON.getJSONObject(0));
                NetworkOffering networkOffering = networkOfferingService.findByUUID(eventObject.getEntityuuid());
                networkOffering.setName(csNetworkOffering.getName());
                networkOffering.setDisplayText(csNetworkOffering.getDisplayText());
                networkOffering.setAvailability(csNetworkOffering.getAvailability());
                networkOfferingService.update(networkOffering);
            }
        }
        if (eventObject.getEvent().equals("NETWORK.OFFERING.DELETE")) {
            NetworkOffering networkOffering = networkOfferingService.findByUUID(eventObject.getEntityuuid());
            networkOfferingService.delete(networkOffering);
        }
    }

    /**
     * Sync with Cloud Server Volume.
     *
     * @throws ApplicationException
     *             unhandled application errors.
     * @throws Exception
     *             cloudstack unhandled errors.
     */
    @Override
    public void asyncVolume(ResponseEvent eventObject) throws ApplicationException, Exception {
        if (eventObject.getEvent().contains("VOLUME.DELETE")) {
            Volume volume = volumeService.findByUUID(eventObject.getEntityuuid());
            volume.setIsSyncFlag(false);
            volume.setIsActive(false);
            Errors errors = new Errors(messageSource);
            volumeService.softDelete(volume);
            //Resource count delete for volume.
            if (!convertEntityService.getDepartmentById(volume.getDepartmentId()).getType().equals(AccountType.USER)) {
                updateResourceCountService.QuotaUpdateByResourceObject(volume, CS_Volume, volume.getDomainId(),
                        CS_Domain, Delete);
            } else {
                if (volume.getProjectId() != null) {
                    updateResourceCountService.QuotaUpdateByResourceObject(volume, CS_Volume, volume.getProjectId(),
                            CS_Project, Delete);
                }
                if (volume.getDepartmentId() != null) {
                    updateResourceCountService.QuotaUpdateByResourceObject(volume, CS_Volume,
                            volume.getDepartmentId(), CS_Department, Delete);
                }
                if (volume.getDomainId() != null) {
                    updateResourceCountService.QuotaUpdateByResourceObject(volume, CS_Volume, volume.getDomainId(),
                            CS_Domain, Delete);
                }
            }
        }
    }

    @Override
    public void syncVMUpdate(String uuid) throws Exception {
        VmInstance persistVm = virtualMachineService.findByUUID(uuid);
        configUtil.setServer(1L);
        HashMap<String, String> vmMap = new HashMap<String, String>();
        vmMap.put(CloudStackConstants.CS_ID, persistVm.getUuid());
        String response = cloudStackInstanceService.listVirtualMachines(CloudStackConstants.JSON, vmMap);
        JSONArray vmListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject(CloudStackConstants.CS_LIST_VM_RESPONSE);
        if (responseObject.has(CloudStackConstants.CS_VM)) {
            vmListJSON = responseObject.getJSONArray(CloudStackConstants.CS_VM);
            // 2. Iterate the json list, convert the single json
            // entity to vm.
            for (int i = 0, size = vmListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to vm
                // entity.
                VmInstance CsVmInstance = VmInstance.convert(vmListJSON.getJSONObject(i));
                persistVm.setDisplayName(CsVmInstance.getTransDisplayName());
                // 3. Update vm for user vm creation.
                virtualMachineService.update(persistVm);
            }
        }
    }

}
