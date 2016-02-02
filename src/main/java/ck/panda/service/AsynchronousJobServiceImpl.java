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
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import ck.panda.constants.EventTypes;
import ck.panda.domain.entity.CloudStackConfiguration;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.FirewallRules;
import ck.panda.domain.entity.FirewallRules.Purpose;
import ck.panda.domain.entity.IpAddress;
import ck.panda.domain.entity.LoadBalancerRule;
import ck.panda.domain.entity.Network;
import ck.panda.domain.entity.NetworkOffering;
import ck.panda.domain.entity.Nic;
import ck.panda.domain.entity.PortForwarding;
import ck.panda.domain.entity.Template;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.VmIpaddress;
import ck.panda.domain.entity.Volume;
import ck.panda.domain.entity.Volume.Status;
import ck.panda.rabbitmq.util.ResponseEvent;
import ck.panda.util.CloudStackInstanceService;
import ck.panda.util.CloudStackLoadBalancerService;
import ck.panda.util.CloudStackNetworkOfferingService;
import ck.panda.util.CloudStackNicService;
import ck.panda.util.CloudStackResourceCapacity;
import ck.panda.util.CloudStackServer;
import ck.panda.util.CloudStackVolumeService;
import ck.panda.util.EncryptionUtil;
import ck.panda.util.JsonUtil;
import ck.panda.util.error.exception.ApplicationException;

/**
 * Synchronization of all the asynchronous data from the cloudStack.
 *
 */
@PropertySource(value = "classpath:permission.properties")
@Service
public class AsynchronousJobServiceImpl implements AsynchronousJobService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncServiceImpl.class);

    /** CloudStack connector. */
    @Autowired
    private CloudStackServer server;

    /** CloudStack configuration . */
    @Autowired
    private CloudStackConfigurationService cloudConfigService;

    /** Virtual machine Service for listing vms. */
    @Autowired
    private VirtualMachineService virtualMachineService;

    /** NetworkOfferingService for listing network offers in cloudstack server. */
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

    /** NetworkOfferingService for listing network offers in cloudstack server. */
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

    /** Cloud stack firewall service. */
    @Autowired
    private CloudStackLoadBalancerService cloudStackLoadBalancerService;

    /** CloudStack Resource Capacity Service. */
    @Autowired
    private CloudStackResourceCapacity cloudStackResourceCapacity;

    /** Secret key value is append. */
    @Value(value = "${aes.salt.secretKey}")
    private String secretKey;

    /** Used for setting optional values for resource count. */
    HashMap<String, String> domainCountMap = new HashMap<String, String>();

    /**
     * Sync with CloudStack server list via Asynchronous Job.
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
        String eventObjectResult = cloudStackInstanceService.queryAsyncJobResult(eventObject.getString("jobId"),
                "json");
        JSONObject jobResult = new JSONObject(eventObjectResult).getJSONObject("queryasyncjobResultresponse")
                .getJSONObject("jobResult");

        String commandText = null;
        if (eventObject.getString("commandEventType").contains(".")) {
            commandText = eventObject.getString("commandEventType").substring(0,
                    eventObject.getString("commandEventType").indexOf('.', 0)) + ".";
        } else {
            commandText = eventObject.getString("commandEventType");
        }
        switch (commandText) {
        case EventTypes.EVENT_VM:
            LOGGER.debug("VM Sync", eventObject.getString("jobId") + "===" + eventObject.getString("commandEventType"));
            syncVirtualMachine(jobResult, eventObject);
            break;
        case EventTypes.EVENT_NETWORK:
            if (!eventObject.getString("commandEventType").contains("OFFERING")) {
                LOGGER.debug("Network sync",
                        eventObject.getString("jobId") + "===" + eventObject.getString("commandEventType"));
                asyncNetwork(jobResult, eventObject);
            }
            break;
        case EventTypes.EVENT_FIREWALL:
            if (eventObject.getString("commandEventType").contains("FIREWALL")) {
                LOGGER.debug("Firewall sync",
                        eventObject.getString("jobId") + "===" + eventObject.getString("commandEventType"));
                asyncFirewall(jobResult, eventObject);
            }
            break;
        case EventTypes.EVENT_NAT:
            if (eventObject.getString("commandEventType").contains("STATICNAT")) {
                LOGGER.debug("Nat sync",
                        eventObject.getString("jobId") + "===" + eventObject.getString("commandEventType"));
                asyncNat(jobResult, eventObject);
            }
            break;
        case EventTypes.EVENT_TEMPLATE:
            LOGGER.debug("templates sync",
                    eventObject.getString("jobId") + "===" + eventObject.getString("commandEventType"));
            asyncTemplates(eventObject);
            break;
        case EventTypes.EVENT_VOLUME:
            LOGGER.debug("Volume sync", eventObject.getString("jobId") + "===" + eventObject.getString("commandEventType"));
            asyncVolume(jobResult, eventObject);
            break;
        case EventTypes.EVENT_NIC:
            LOGGER.debug("NIC sync", eventObject.getString("jobId") + "===" + eventObject.getString("commandEventType"));
            asyncNic(jobResult, eventObject);
            break;
        case EventTypes.EVENT_PORTFORWARDING:
            LOGGER.debug("NET sync", eventObject.getString("jobId") + "===" + eventObject.getString("commandEventType"));
            asyncNet(jobResult, eventObject);
            break;
        case EventTypes.EVENT_LOADBALANCER:
            LOGGER.debug("LB sync", eventObject.getString("jobId") + "===" + eventObject.getString("commandEventType"));
            asyncLb(jobResult, eventObject);
            break;
        default:
            LOGGER.debug("No sync required",
                    eventObject.getString("jobId") + "===" + eventObject.getString("commandEventType"));
        }
    }

    /**
     * Sync with CloudStack server virtual machine.
     *
     * @param jobResult job result
     * @param eventObject network event object
     * @throws Exception cloudstack unhandled errors
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
                    csVm.setTemplateId(convertEntityService.getTemplateId(csVm.getTransTemplateId()));
                    csVm.setComputeOfferingId(convertEntityService.getComputeOfferId(csVm.getTransComputeOfferingId()));
                    if (csVm.getHostId() != null) {
                        csVm.setPodId(convertEntityService
                                .getPodIdByHost(convertEntityService.getHostId(csVm.getTransHostId())));
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
                    }
                    if (csVm.getInstanceOwnerId() != null) {
                        instance.setInstanceOwnerId(csVm.getInstanceOwnerId());
                    }
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
                    // Resource count for domain
                    String csResponse = cloudStackResourceCapacity.updateResourceCount(
                            convertEntityService.getDomainById(instance.getDomainId()).getUuid(), domainCountMap,
                            "json");
                    convertEntityService.resourceCount(csResponse);
                }
            } else {
                vmInstance.setDomainId(convertEntityService.getDomainId(vmInstance.getTransDomainId()));
                vmInstance.setZoneId(convertEntityService.getZoneId(vmInstance.getTransZoneId()));
                vmInstance.setNetworkId(convertEntityService.getNetworkId(vmInstance.getTransNetworkId()));
                vmInstance.setProjectId(convertEntityService.getProjectId(vmInstance.getTransProjectId()));
                vmInstance.setHostId(convertEntityService.getHostId(vmInstance.getTransHostId()));
                vmInstance.setInstanceOwnerId(convertEntityService.getUserByName(vmInstance.getTransDisplayName(),
                        convertEntityService.getDomain(vmInstance.getTransDomainId())));
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

                vmIn = virtualMachineService.update(vmInstance);
            }
            if (eventObject.getString("commandEventType").equals(EventTypes.EVENT_VM_CREATE)) {
                this.assignNicTovM(vmIn);
                this.assignVolumeTovM(vmIn);
                if (volumeService.findByInstanceAndVolumeType(vmIn.getId()) != null) {
                    vmIn.setVolumeSize(volumeService.findByInstanceAndVolumeType(vmIn.getId()).getDiskSize());
                    vmIn = virtualMachineService.update(vmIn);
                }

            }
        }
    }

    /**
     * Get the default NIC when creating the template.
     *
     * @param vmInstance instance details
     * @throws ApplicationException unhandled application errors
     * @throws Exception cloudstack unhandled errors
     */
    public void assignNicTovM(VmInstance vmInstance) throws ApplicationException, Exception {
        HashMap<String,String> nicMap = new HashMap<String, String>();
        nicMap.put("virtualmachineid", vmInstance.getUuid());
        String listNic = cloudStackNicService.listNics(nicMap, "json");
        JSONArray nicListJSON = new JSONObject(listNic).getJSONObject("listnicsresponse").getJSONArray("nic");
        for (int i = 0; i < nicListJSON.length(); i++) {
            Nic nic = nicService.findbyUUID(nicListJSON.getJSONObject(i).getString("id"));
            if (nic != null) {
                nic.setSyncFlag(false);
                nic.setUuid(nicListJSON.getJSONObject(i).getString("id"));
                nic.setVmInstanceId(virtualMachineService.findByUUID(nicListJSON.getJSONObject(i).getString("virtualmachineid")).getId());
                nic.setNetworkId(networkService.findByUUID(nicListJSON.getJSONObject(i).getString("networkid")).getId());
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
                nic.setVmInstanceId(virtualMachineService.findByUUID(nicListJSON.getJSONObject(i).getString("virtualmachineid")).getId());
                nic.setNetworkId(networkService.findByUUID(nicListJSON.getJSONObject(i).getString("networkid")).getId());
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
     * @param vmInstance instance details
     * @throws ApplicationException unhandled application errors
     * @throws Exception cloudstack unhandled errors
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
           String listVolume = csVolumeService.listVolumes("json", volumeMap);
           JSONArray volumeListJSON = new JSONObject(listVolume).getJSONObject("listvolumesresponse").getJSONArray("volume");
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
                   volume.setDepartmentId(convertEntityService.getDepartmentByUsernameAndDomains(volume.getTransDepartmentId(), domain));
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
     * @param jobResult job result
     * @param eventObject network event object
     * @throws ApplicationException unhandled application errors
     * @throws Exception cloudstack unhandled errors
     */
    public void asyncNetwork(JSONObject jobResult, JSONObject eventObject) throws ApplicationException, Exception {

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

                // Resource count for domain
                String csResponse = cloudStackResourceCapacity.updateResourceCount(
                        convertEntityService.getDomainById(network.getDomainId()).getUuid(), domainCountMap, "json");
                convertEntityService.resourceCount(csResponse);
            }
        }
        if (eventObject.getString("commandEventType").equals("NETWORK.DELETE")) {
            JSONObject json = new JSONObject(eventObject.getString("cmdInfo"));
            Network network = networkService.findByUUID(json.getString("id"));
            network.setSyncFlag(false);
            networkService.softDelete(network);

            // Resource count for domain
            String csResponse = cloudStackResourceCapacity.updateResourceCount(
                    convertEntityService.getDomainById(network.getDomainId()).getUuid(), domainCountMap, "json");
            convertEntityService.resourceCount(csResponse);
        }
    }

    /**
     * Sync with CloudStack server Firewall from Asynchronous Job.
     *
     * @param jobResult job result
     * @param eventObject network event object
     * @throws ApplicationException unhandled application errors
     * @throws Exception cloudstack unhandled errors
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
     * Sync with CloudStack server Ip address for sourcenat from Asynchronous Job.
     *
     * @param jobResult job result
     * @param eventObject network event object
     * @throws ApplicationException unhandled application errors
     * @throws Exception cloudstack unhandled errors
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
     * @param jobResult job result
     * @param eventObject network event object
     * @throws ApplicationException unhandled application errors
     * @throws Exception cloudstack unhandled errors
     */
    public void asyncIpAddress(JSONObject jobResult, JSONObject eventObject) throws ApplicationException, Exception {
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

                    // Resource count for domain
                    String csResponse = cloudStackResourceCapacity.updateResourceCount(
                            convertEntityService.getDomainById(persistIp.getDomainId()).getUuid(), domainCountMap,
                            "json");
                    convertEntityService.resourceCount(csResponse);
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

                // Resource count for domain
                String csResponse = cloudStackResourceCapacity.updateResourceCount(
                        convertEntityService.getDomainById(ipAddress.getDomainId()).getUuid(), domainCountMap, "json");
                convertEntityService.resourceCount(csResponse);
            }
        }

    }

    /**
     * Sync with CloudStack server Network from Asynchronous Job.
     *
     * @param eventObject template event object
     * @throws ApplicationException unhandled application errors
     * @throws Exception cloudstack unhandled errors
     */
    public void asyncTemplates(JSONObject eventObject) throws ApplicationException, Exception {

        if (eventObject.getString("commandEventType").equals("TEMPLATE.DELETE")) {
            JSONObject json = new JSONObject(eventObject.getString("cmdInfo"));
            Template template = templateService.findByUUID(json.getString("id"));
            template.setSyncFlag(false);
            templateService.softDelete(template);
        }
    }

    /**
     * Sync with Cloud Server Volume.
     *
     * @param jobResult job result
     * @param eventObject volume event object
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    public void asyncVolume(JSONObject jobResult, JSONObject eventObject) throws ApplicationException, Exception {

        if (eventObject.getString("commandEventType").equals("VOLUME.CREATE")
                || eventObject.getString("commandEventType").equals("VOLUME.UPLOAD")) {
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
            } else {
                volume.setDiskSizeFlag(true);
            }
            if (jobResult.getJSONObject("volume").getString("state").equalsIgnoreCase("ALLOCATED")) {
                volume.setStatus(Status.ALLOCATED);
            }
            if (volumeService.findByUUID(volume.getUuid()) == null) {
                volumeService.save(volume);

                // Resource count update for domain
                String csResponse = cloudStackResourceCapacity.updateResourceCount(
                        convertEntityService.getDomainById(volume.getDomainId()).getUuid(), domainCountMap, "json");
                convertEntityService.resourceCount(csResponse);
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
     * @param jobResult job result
     * @param eventObject volume event object
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    public void asyncNic(JSONObject jobResult, JSONObject eventObject) throws ApplicationException, Exception {

        if (eventObject.getString("commandEventType").equals("NIC.CREATE") || eventObject.getString("commandEventType").equals("NIC.UPDATE")
             || eventObject.getString("commandEventType").equals("NIC.DELETE")) {

            JSONArray nicListJSON = jobResult.getJSONObject("virtualmachine").getJSONArray("nic");
            List<Nic> nicList = new ArrayList<Nic>();
            for (int i = 0, size = nicListJSON.length(); i < size; i++) {
                Nic nic = Nic.convert(nicListJSON.getJSONObject(i));
                nic.setVmInstanceId(convertEntityService.getVmInstanceId(JsonUtil.getStringValue(jobResult.getJSONObject("virtualmachine"), "id")));
                nic.setNetworkId(convertEntityService.getNetworkId(nic.getTransNetworkId()));
                nicList.add(nic);
            }
            HashMap<String, Nic> csNicMap = (HashMap<String, Nic>) Nic.convert(nicList);
            List<Nic> appnicList = nicService.findByInstance(convertEntityService.getVmInstanceId(JsonUtil.getStringValue(jobResult.getJSONObject("virtualmachine"), "id")));

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
                 VmIpaddress vmIpaddress = vmIpService.findByUUID(csVmIpaddress.getUuid());
                 vmIpaddress.setSyncFlag(false);
                 vmIpaddress.setUuid(csVmIpaddress.getUuid());
                 vmIpaddress.setGuestIpAddress(csVmIpaddress.getGuestIpAddress());
                 Nic nic = nicService.findbyUUID(csVmIpaddress.getTransNicId());
                 if (vmIpService.findByUUID(csVmIpaddress.getUuid()) == null) {
                     vmIpService.save(csVmIpaddress);
                 }
                 nic.setSyncFlag(false);
                 nicService.update(nic);
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
     * @param jobResult job result
     * @param eventObject volume event object
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
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

            //Delete the port forwarding firewall rule
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
     * Sync with Cloud Server Network Load Balancer.
     *
     * @param jobResult job result
     * @param eventObject Load Balancer event object
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    @SuppressWarnings("unused")
    public void asyncLb(JSONObject jobResult, JSONObject eventObject) throws ApplicationException, Exception {

        if (eventObject.getString("commandEventType").equals("LB.CREATE")) {
            LoadBalancerRule loadBalancerRule = LoadBalancerRule.convert(jobResult.getJSONObject("loadbalancer"));
            loadBalancerRule.setNetworkId(convertEntityService.getNetworkId(loadBalancerRule.getTransNetworkId()));
            loadBalancerRule.setIpAddressId(convertEntityService.getIpAddressId(loadBalancerRule.getTransIpAddressId()));
            loadBalancerRule.setZoneId(convertEntityService.getZoneId(loadBalancerRule.getTransZoneId()));
            loadBalancerRule.setDomainId(convertEntityService.getDomainId(loadBalancerRule.getTransDomainId()));
            if (loadBalancerService.findByUUID(loadBalancerRule.getUuid()) == null) {
                loadBalancerService.save(loadBalancerRule);
                firewallRules(jobResult, FirewallRules.Purpose.LOADBALANCING);
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
                HashMap<String, String> loadBalancerInstanceMap = new HashMap<String, String>();
                loadBalancerInstanceMap.put("lbvmips", "true");
                loadBalancerInstanceMap.put("listall", "true");
                String response = cloudStackLoadBalancerService.listLoadBalancerRuleInstances(json.getString("id"), "json", loadBalancerInstanceMap);
                JSONArray vmListJSON = null;
                JSONObject responseObject = new JSONObject(response).getJSONObject("listloadbalancerruleinstancesresponse");
                if (responseObject.has("lbrulevmidip")) {
                    vmListJSON = responseObject.getJSONArray("lbrulevmidip");
                    List<VmInstance> newVmInstance = new ArrayList<VmInstance>();
                    for (int i = 0; i < vmListJSON.length(); i++) {
                        VmInstance vmInstance = virtualMachineService.findByUUID(vmListJSON.getJSONObject(i).
                                getJSONObject("loadbalancerruleinstance").getString("id"));
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

            //Delete the load balancer firewall rule
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
     * @param jobResult job result
     * @param purpose of the firewall
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    public void firewallRules(JSONObject jobResult, FirewallRules.Purpose purpose) throws ApplicationException, Exception {
        FirewallRules csFirewallRule = null;
        if (purpose == Purpose.LOADBALANCING) {
            csFirewallRule = FirewallRules.convert(jobResult.getJSONObject("loadbalancer"),
                null, purpose);
        } else {
            csFirewallRule = FirewallRules.convert(jobResult.getJSONObject("portforwardingrule"),
                    null, purpose);
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
     * @param uuid network offering response event
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors
     */
    @Override
    public void asyncNetworkOffering(ResponseEvent eventObject) throws ApplicationException, Exception {

        if (eventObject.getEvent().equals("NETWORK.OFFERING.EDIT")) {
            HashMap<String, String> networkOfferingMap = new HashMap<String, String>();
            networkOfferingMap.put("id", eventObject.getEntityuuid());
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
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    @Override
    public void asyncVolume(ResponseEvent eventObject) throws ApplicationException, Exception {
        if (eventObject.getEvent().contains("VOLUME.DELETE")) {
            Volume volume = volumeService.findByUUID(eventObject.getEntityuuid());
            volume.setIsSyncFlag(false);
            volumeService.softDelete(volume);

            // Resource count update for domain
            String csResponse = cloudStackResourceCapacity.updateResourceCount(
                    convertEntityService.getDomainById(volume.getDomainId()).getUuid(), domainCountMap, "json");
            convertEntityService.resourceCount(csResponse);
        }
    }
}

