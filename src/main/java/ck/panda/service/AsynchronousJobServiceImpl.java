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
import ck.panda.domain.entity.FirewallRules.State;
import ck.panda.domain.entity.IpAddress;
import ck.panda.domain.entity.Network;
import ck.panda.domain.entity.NetworkOffering;
import ck.panda.domain.entity.Nic;
import ck.panda.domain.entity.PortForwarding;
import ck.panda.domain.entity.Template;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.Volume;
import ck.panda.rabbitmq.util.ResponseEvent;
import ck.panda.util.CloudStackInstanceService;
import ck.panda.util.CloudStackNetworkOfferingService;
import ck.panda.util.CloudStackNicService;
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

    /** Secret key value is append. */
    @Value(value = "${aes.salt.secretKey}")
    private String secretKey;

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
        JSONObject jobresult = new JSONObject(eventObjectResult).getJSONObject("queryasyncjobresultresponse")
                .getJSONObject("jobresult");

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
                syncvirtualmachine(jobresult, eventObject);
            break;
        case EventTypes.EVENT_NETWORK:
            if (!eventObject.getString("commandEventType").contains("OFFERING")) {
                LOGGER.debug("Network sync",
                        eventObject.getString("jobId") + "===" + eventObject.getString("commandEventType"));
                asyncNetwork(jobresult, eventObject);
            }
            break;
        case EventTypes.EVENT_FIREWALL:
            if (eventObject.getString("commandEventType").contains("FIREWALL")) {
                LOGGER.debug("Firewall sync",
                        eventObject.getString("jobId") + "===" + eventObject.getString("commandEventType"));
                asyncFirewall(jobresult, eventObject);
            }
            break;
        case EventTypes.EVENT_NAT:
            if (eventObject.getString("commandEventType").contains("STATICNAT")) {
                LOGGER.debug("Nat sync",
                        eventObject.getString("jobId") + "===" + eventObject.getString("commandEventType"));
                asyncNat(jobresult, eventObject);
            }
            break;
        case EventTypes.EVENT_TEMPLATE:
            LOGGER.debug("templates sync",
                    eventObject.getString("jobId") + "===" + eventObject.getString("commandEventType"));
            asyncTemplates(eventObject);
            break;
        case EventTypes.EVENT_VOLUME:
            LOGGER.debug("Volume sync", eventObject.getString("jobId") + "===" + eventObject.getString("commandEventType"));
            asyncVolume(jobresult, eventObject);
            break;
        case EventTypes.EVENT_NIC:
            LOGGER.debug("NIC sync", eventObject.getString("jobId") + "===" + eventObject.getString("commandEventType"));
            asyncNic(jobresult, eventObject);
            break;
        case EventTypes.EVENT_PORTFORWARDING:
            LOGGER.debug("NET sync", eventObject.getString("jobId") + "===" + eventObject.getString("commandEventType"));
            asyncNet(jobresult, eventObject);
            break;
        default:
            LOGGER.debug("No sync required",
                    eventObject.getString("jobId") + "===" + eventObject.getString("commandEventType"));
        }
    }

    /**
     * Sync with CloudStack server virtual machine.
     *
     * @param jobresult job result
     * @param eventObject network event object
     * @throws Exception cloudstack unhandled errors
     */
    public void syncvirtualmachine(JSONObject jobresult, JSONObject eventObject) throws Exception {

        if (jobresult.has("virtualmachine")) {
            VmInstance vmInstance = VmInstance.convert(jobresult.getJSONObject("virtualmachine"));
            VmInstance instance = virtualMachineService.findByUUID(vmInstance.getUuid());
            VmInstance vmIn = null;
            if (instance != null) {
                instance.setSyncFlag(false);
                // 3.1 Find the corresponding CS server vm object by finding it in a
                // hash using uuid
                if (vmInstance.getUuid().equals(instance.getUuid())) {
                    VmInstance csVm = vmInstance;
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
        HashMap<String,String> optional = new HashMap<String, String>();
        optional.put("virtualmachineid", vmInstance.getUuid());
        String listNic = cloudStackNicService.listNics(optional, "json");
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
           volumeMap.put("domainid", convertEntityService.getDomainById(vmInstance.getDomainId()).getUuid());

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
     * @param jobresult job result
     * @param eventObject network event object
     * @throws ApplicationException unhandled application errors
     * @throws Exception cloudstack unhandled errors
     */
    public void asyncNetwork(JSONObject jobresult, JSONObject eventObject) throws ApplicationException, Exception {

        if (eventObject.getString("commandEventType").equals("NETWORK.UPDATE")) {
            Network csNetwork = Network.convert(jobresult.getJSONObject("network"));
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
            networkService.softDelete(network);
        }
    }

    /**
     * Sync with CloudStack server Firewall from Asynchronous Job.
     *
     * @param jobresult job result
     * @param eventObject network event object
     * @throws ApplicationException unhandled application errors
     * @throws Exception cloudstack unhandled errors
     */
    public void asyncFirewall(JSONObject jobresult, JSONObject eventObject) throws ApplicationException, Exception {
        if (eventObject.getString("commandEventType").equals("FIREWALL.EGRESS.OPEN")) {
            FirewallRules csFirewallRule = FirewallRules.convert(jobresult.getJSONObject("firewallrule"), FirewallRules.TrafficType.EGRESS);
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
            FirewallRules csFirewallRule = FirewallRules.convert(jobresult.getJSONObject("firewallrule"), FirewallRules.TrafficType.INGRESS);
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
     * @param jobresult job result
     * @param eventObject network event object
     * @throws ApplicationException unhandled application errors
     * @throws Exception cloudstack unhandled errors
     */
    public void asyncNat(JSONObject jobresult, JSONObject eventObject) throws ApplicationException, Exception {
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
     * @param jobresult job result
     * @param eventObject network event object
     * @throws ApplicationException unhandled application errors
     * @throws Exception cloudstack unhandled errors
     */
    public void asyncIpAddress(JSONObject jobresult, JSONObject eventObject) throws ApplicationException, Exception {
        if (eventObject.getString("commandEventType").equals("NET.IPASSIGN")) {
            IpAddress ipaddress = IpAddress.convert(jobresult.getJSONObject("ipaddress"));
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
     * @param jobresult job result
     * @param eventObject volume event object
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    public void asyncVolume(JSONObject jobresult, JSONObject eventObject) throws ApplicationException, Exception {

        if (eventObject.getString("commandEventType").equals("VOLUME.CREATE")
                || eventObject.getString("commandEventType").equals("VOLUME.UPLOAD")) {
            Volume volume = Volume.convert(jobresult.getJSONObject("volume"));
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
            if (volumeService.findByUUID(volume.getUuid()) == null) {
                volumeService.save(volume);
            }
        }
        if (eventObject.getString("commandEventType").equals("VOLUME.ATTACH")
                || eventObject.getString("commandEventType").equals("VOLUME.DETACH")) {
            Volume csVolume = Volume.convert(jobresult.getJSONObject("volume"));
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
     * @param jobresult job result
     * @param eventObject volume event object
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    public void asyncNic(JSONObject jobresult, JSONObject eventObject) throws ApplicationException, Exception {

        if (eventObject.getString("commandEventType").equals("NIC.CREATE") || eventObject.getString("commandEventType").equals("NIC.UPDATE")
             || eventObject.getString("commandEventType").equals("NIC.DELETE")) {

            JSONArray nicListJSON = jobresult.getJSONObject("virtualmachine").getJSONArray("nic");
            List<Nic> nicList = new ArrayList<Nic>();
            for (int i = 0, size = nicListJSON.length(); i < size; i++) {
                Nic nic = Nic.convert(nicListJSON.getJSONObject(i));
                nic.setVmInstanceId(convertEntityService.getVmInstanceId(JsonUtil.getStringValue(jobresult.getJSONObject("virtualmachine"), "id")));
                nic.setNetworkId(convertEntityService.getNetworkId(nic.getTransNetworkId()));
                nicList.add(nic);
            }
            HashMap<String, Nic> csNicMap = (HashMap<String, Nic>) Nic.convert(nicList);
            List<Nic> appnicList = nicService.findByInstance(convertEntityService.getVmInstanceId(JsonUtil.getStringValue(jobresult.getJSONObject("virtualmachine"), "id")));

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
            LOGGER.debug("Not Implemented");
        }
        if (eventObject.getString("commandEventType").equals("NIC.SECONDARY.IP.UNASSIGN")) {
            LOGGER.debug("Not Implemented");
        }
    }

    /**
     * Sync with Cloud Server Network port forwarding.
     *
     * @param jobresult job result
     * @param eventObject volume event object
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    public void asyncNet(JSONObject jobresult, JSONObject eventObject) throws ApplicationException, Exception {

        if (eventObject.getString("commandEventType").equals("NET.RULEADD")) {
            PortForwarding portForwarding = PortForwarding.convert(jobresult.getJSONObject("portforwardingrule"));
            portForwarding.setVmInstanceId(convertEntityService.getVmInstanceId(portForwarding.getTransvmInstanceId()));
            portForwarding.setNetworkId(convertEntityService.getNetworkId(portForwarding.getTransNetworkId()));
            portForwarding.setIpAddressId(convertEntityService.getIpAddressId(portForwarding.getTransIpAddressId()));
            if (portForwardingService.findByUUID(portForwarding.getUuid()) == null) {
                portForwardingService.save(portForwarding);
            }
        }

        if (eventObject.getString("commandEventType").equals("NET.RULEDELETE")) {
            JSONObject json = new JSONObject(eventObject.getString("cmdInfo"));
            PortForwarding portForwarding = portForwardingService.findByUUID(json.getString("id"));
            portForwarding.setSyncFlag(false);
            portForwardingService.softDelete(portForwarding);

        }

        if (eventObject.getString("commandEventType").equals("NET.IPASSIGN") ||
                eventObject.getString("commandEventType").equals("NET.IPRELEASE")) {
            asyncIpAddress(jobresult, eventObject);
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
        }
    }

}
