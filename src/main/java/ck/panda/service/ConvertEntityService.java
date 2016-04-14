package ck.panda.service;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ck.panda.constants.CloudStackConstants;
import ck.panda.domain.entity.ComputeOffering;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.IpAddress;
import ck.panda.domain.entity.LoadBalancerRule;
import ck.panda.domain.entity.Network;
import ck.panda.domain.entity.NetworkOffering;
import ck.panda.domain.entity.Nic;
import ck.panda.domain.entity.OsCategory;
import ck.panda.domain.entity.Project;
import ck.panda.domain.entity.ResourceLimitDepartment;
import ck.panda.domain.entity.ResourceLimitDomain;
import ck.panda.domain.entity.StorageOffering;
import ck.panda.domain.entity.Template;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.VmIpaddress;
import ck.panda.domain.entity.Volume;
import ck.panda.domain.entity.Zone;
import ck.panda.domain.entity.ResourceLimitDomain.ResourceType;
import ck.panda.domain.entity.ResourceLimitProject;
import ck.panda.domain.entity.Snapshot;
import ck.panda.domain.entity.SnapshotPolicy;
import ck.panda.util.CloudStackInstanceService;
import ck.panda.util.CloudStackResourceCapacity;
import ck.panda.util.CloudStackServer;
import ck.panda.util.audit.DateTimeService;
import ck.panda.domain.entity.SSHKey;

/**
 * Convert Util used to get entity object from CS server's resource uuid.
 */
@Service
public class ConvertEntityService {

    /** Constant for resource type details. */
    public static final String CS_INSTANCE = "0", CS_IP = "1", CS_VOLUME = "2", CS_SNAPSHOT = "3", CS_TEMPLATE = "4",
            CS_PROJECT = "5", CS_NETWORK = "6", CS_VPC = "7", CS_CPU = "8", CS_MEMORY = "9", CS_PRIMARY_STORAGE = "10",
            CS_SECONDARY_STORAGE = "11";

    /** Constant for resource type. */
    public static final String CS_RESOUCE_TYPE = "resourcetype";

    /** Constant for resource count. */
    public static final String CS_RESOUCE_COUNT = "resourcecount";

    /** Constant for update resource count. */
    public static final String CS_UPDATE_RESOURCE_RESPONSE = "updateresourcecountresponse";

    /** Domain Service for listing domains. */
    @Autowired
    private DomainService domainService;

    /** RegionSerivce for listing Regions. */
    @Autowired
    private ZoneService zoneService;

    /** IsoSerivce for listing Iso. */
    @Autowired
    private IsoService isoService;

    /** Websocket service reference. */
    @Autowired
    private WebsocketService websocketService;

    /** OSCategoryService for listing operating sytem in cloudstack server. */
    @Autowired
    private OsCategoryService osCategoryService;

    /** OSCategoryService for listing operating sytem in cloudstack server. */
    @Autowired
    private OsTypeService osTypeService;

    /** Storage Offering Service for listing storage offering. */
    @Autowired
    private StorageOfferingService storageService;

    /** Date and time service reference. */
    @Autowired
    private DateTimeService dateTimeService;

    /** Websocket service for tracking.*/
       @Autowired
       private WebsocketService webSocket;

    /**
     * NetworkOfferingService for listing network offers in cloudstack server.
     */
    @Autowired
    private NetworkOfferingService networkOfferingService;

    /**
     * NetworkOfferingService for listing network offers in cloudstack server.
     */
    @Autowired
    private NetworkService networkService;

    /**
     * NetworkOfferingService for listing network offers in cloudstack server.
     */
    @Autowired
    private ComputeOfferingService computeService;

    /** User Service for listing users. */
    @Autowired
    private DepartmentService departmentService;

    /** Virtual machine Service for listing vms. */
    @Autowired
    private VirtualMachineService virtualMachineService;

    /** User Service for listing users. */
    @Autowired
    private UserService userService;

    /** Template Service for listing templates. */
    @Autowired
    private TemplateService templateService;

    /** Pod service for listing pods. */
    @Autowired
    private PodService podService;

    /** Cluster service for listing clusters. */
    @Autowired
    private ClusterService clusterService;

    /** volume servcie for listing volumes. */
    @Autowired
    private VolumeService volumeService;

    /** Host service for listing hosts. */
    @Autowired
    private HostService hostService;

    /** Project service for listing projects. */
    @Autowired
    private ProjectService projectService;

    /** Nic service for listing nic. */
    @Autowired
    private NicService nicService;

    /** Service reference to IpAddress. */
    @Autowired
    private IpaddressService ipAddressService;

    /** Service reference to VmIpAddress. */
    @Autowired
    private VmIpaddressService vmIpAddressService;

    /** Service reference to LoadBalancer. */
    @Autowired
    private LoadBalancerService lbService;

    /** CloudStack connector reference for instance. */
    @Autowired
    private CloudStackInstanceService cloudStackInstanceService;

    /** Vmsnapshot service for reference .*/
    @Autowired
    private VmSnapshotService vmSnapshotService ;

    /** CloudStack connector. */
    @Autowired
    private CloudStackServer server;

    /** CloudStack configuration . */
    @Autowired
    private CloudStackConfigurationService cloudConfigService;

    /** Service reference to Port Forwarding. */
    @Autowired
    private PortForwardingService portForwardingService;

    /** Resource Limit Domain Service. */
    @Autowired
    private ResourceLimitDomainService resourceLimitDomainService;

    /** Resource Limit department Service. */
    @Autowired
    private ResourceLimitDepartmentService resourceLimitDepartmentService;

    /** Resource Limit Project Service. */
    @Autowired
    private ResourceLimitProjectService resourceLimitProjectService;

    /** CloudStack Resource Capacity Service. */
    @Autowired
    private CloudStackResourceCapacity cloudStackResourceCapacity;

    /** Snapshot service for reference . */
    @Autowired
    private SnapshotService snapshotService;

    /** snapshot Policy Service service for reference . */
    @Autowired
    private SnapshotPolicyService snapshotPolicyService;

    /** Sync Service reference. */
    @Autowired
    private AsynchronousJobService asyncService;

    /** Update Resource Count Service reference. */
    @Autowired
    private UpdateResourceCountService updateResourceCountService;

    /** SSHKey Service for listing ssh key. */
    @Autowired
    private SSHKeyService sshKeyService;

    /** Secret key value is append. */
    @Value(value = "${aes.salt.secretKey}")
    private String secretKey;

    /**
     * Get domain id.
     *
     * @param uuid
     *            uuid of domain.
     * @return domain id.
     * @throws Exception
     *             unhandled exception.
     */
    public Long getDomainId(String uuid) throws Exception {
        if (domainService.findbyUUID(uuid) != null) {
            return domainService.findbyUUID(uuid).getId();
        } else {
            return null;
        }
    }

    /**
     * Get domain by id.
     *
     * @param id
     *            of domain.
     * @return domain.
     * @throws Exception
     *             unhandled exception.
     */
    public Domain getDomainById(Long id) throws Exception {
        return domainService.find(id);
    }

    /**
     * Get zone id.
     *
     * @param uuid
     *            uuid of zone.
     * @return zone id.
     * @throws Exception
     *             unhandled exception.
     */
    public Long getZoneId(String uuid) throws Exception {
        if (zoneService.findByUUID(uuid) != null) {
            return zoneService.findByUUID(uuid).getId();
        } else {
            return null;
        }

    }

    /**
     * Get zone by id.
     *
     * @param id
     *            of zone.
     * @return zone.
     * @throws Exception
     *             unhandled exception.
     */
    public Zone getZoneById(Long id) throws Exception {
        return zoneService.find(id);
    }

    /**
     * Get template id.
     *
     * @param uuid
     *            uuid of template.
     * @return template id.
     * @throws Exception
     *             unhandled exception.
     */
    public Long getTemplateId(String uuid) throws Exception {
        if (templateService.findByUUID(uuid) != null) {
            return templateService.findByUUID(uuid).getId();
        } else {
            return null;
        }
    }

    /**
     * Get template by id.
     *
     * @param id
     *            of template.
     * @return template.
     * @throws Exception
     *             unhandled exception.
     */
    public Template getTemplateById(Long id) throws Exception {
        return templateService.find(id);
    }

    /**
     * Get ssh key by id.
     *
     * @param id of ssh key.
     * @return ssh key.
     * @throws Exception unhandled exception.
     */
    public SSHKey getSSHKeyById(Long id) throws Exception {
        return sshKeyService.find(id);
    }

    /**
     * Get ssh key by name and departmentId.
     *
     * @param name of ssh key
     * @param departmentId of ssh key.
     * @return ssh key.
     * @throws Exception unhandled exception.
     */
    public SSHKey getSSHKeyByNameAndDepartment(String name, Long departmentId) throws Exception {
        return sshKeyService.findAllByDepartmentAndKeypairAndIsActive(departmentId, name, true);
    }

    /**
     * Get compute offer id.
     *
     * @param uuid
     *            uuid of service offering.
     * @return computer offer id.
     * @throws Exception
     *             unhandled exception.
     */
    public Long getComputeOfferId(String uuid) throws Exception {
        if (computeService.findByUUID(uuid) != null) {
            return computeService.findByUUID(uuid).getId();
        } else {
            return null;
        }
    }

    /**
     * Get compute offer by id.
     *
     * @param id
     *            of service offering.
     * @return computer offer .
     * @throws Exception
     *             unhandled exception.
     */
    public ComputeOffering getComputeOfferById(Long id) throws Exception {
        return computeService.find(id);
    }

    /**
     * Get the network id.
     *
     * @param uuid
     *            uuid of nic network.
     * @return netwotk id.
     * @throws Exception
     *             unhandled exception.
     */
    public Long getNetworkId(String uuid) throws Exception {
        if (networkService.findByUUID(uuid) != null) {
            return networkService.findByUUID(uuid).getId();
        } else {
            return null;
        }
    }

    /**
     * Get the network id.
     *
     * @param uuid
     *            uuid of nic network.
     * @return netwotk id.
     * @throws Exception
     *             unhandled exception.
     */
    public Long getNetworkByUuid(String uuid) throws Exception {
        if (networkService.findByUUID(uuid) != null) {
            return networkService.findByUUID(uuid).getId();
        } else {
            return null;
        }
    }

    /**
     * Get the networkoffering id.
     *
     * @param uuid
     *            uuid of nic network.
     * @return netwotk id.
     * @throws Exception
     *             unhandled exception.
     */
    public Long getNetworkOfferingId(String uuid) throws Exception {
        if (networkOfferingService.findByUUID(uuid) != null) {
            return networkOfferingService.findByUUID(uuid).getId();
        }
        return null;
    }

    /**
     * Get NetworkOffering object.
     *
     * @param id
     *            of NetworkOffering
     * @return NetworkOffering.
     * @throws Exception
     *             unhandled exception.
     */
    public NetworkOffering getNetworkOfferingById(Long id) throws Exception {
        return networkOfferingService.findById(id);
    }

    /**
     * Get the ostype id.
     *
     * @param uuid
     *            uuid of nic network.
     * @return netwotk id.
     * @throws Exception
     *             unhandled exception.
     */
    public Long getOsTypeId(String uuid) throws Exception {
        if (osTypeService.findByUUID(uuid) != null) {
            return osTypeService.findByUUID(uuid).getId();
        }
        return null;
    }

    /**
     * Get domain object.
     *
     * @param uuid
     *            uuid of domain
     * @return domain.
     * @throws Exception
     *             unhandled exception.
     */
    public Domain getDomain(String uuid) throws Exception {
        return domainService.findbyUUID(uuid);
    }

    /**
     * Get Network object.
     *
     * @param id
     *            of network
     * @return network.
     * @throws Exception
     *             unhandled exception.
     */
    public Network getNetworkById(Long id) throws Exception {
        return networkService.findById(id);
    }

    /**
     * Get Network object.
     *
     * @param id
     *            of network
     * @return network.
     * @throws Exception
     *             unhandled exception.
     */
    public Nic getNicById(Long id) throws Exception {
        return nicService.findById(id);
    }

    /**
     * Get snapshot policy object.
     *
     * @param id of the snapshot policy.
     * @return snapshot policy.
     * @throws Exception if error occurs.
     */
    public SnapshotPolicy getSnapshotPolicyById(Long id) throws Exception {
        return snapshotPolicyService.find(id);
    }

    /**
     * Get domain object.
     *
     * @param uuid
     *            uuid of domain
     * @return domain.
     * @throws Exception
     *             unhandled exception.
     */
    public Nic getNic(String uuid) throws Exception {
        return nicService.findbyUUID(uuid);
    }

    /**
     * Get Vm Instance object.
     *
     * @param id
     *            of vm instance
     * @return network.
     * @throws Exception
     *             unhandled exception.
     */
    public VmInstance getVmInstanceById(Long id) throws Exception {
        return virtualMachineService.findById(id);
    }

    /**
     * Get snapshot object.
     *
     * @param id of the snapshot
     * @return snapshot
     * @throws Exception if error occurs.
     */
    public Snapshot getSnapshotById(Long id) throws Exception {
        return snapshotService.findById(id);
    }

    public Volume getVolumeById(Long id) throws Exception {
        return volumeService.find(id);
    }

    /**
     * Get owner id.
     *
     * @param name
     *            of account/user.
     * @param domain
     *            domain.
     * @return user id.
     * @throws Exception
     *             unhandled exception.
     */
    public Long getOwnerId(String name, Domain domain) throws Exception {
        if (userService.findByUserNameAndDomain(name, domain) != null) {
            return userService.findByUserNameAndDomain(name, domain).getId();
        } else {
            return null;
        }
    }

    /**
     * Get owner by id.
     *
     * @param id
     *            of account/user.
     * @return user.
     * @throws Exception
     *             unhandled exception.
     */
    public User getOwnerById(Long id) throws Exception {
        return userService.find(id);
    }

    /**
     * Get owner by UUID.
     *
     * @param uuid
     *            uuid of account/user.
     * @return user id.
     * @throws Exception
     *             unhandled exception.
     */
    public Long getOwnerByUuid(String uuid) throws Exception {
        if (userService.findByUuIdAndIsActive(uuid, true) != null) {
            return userService.findByUuIdAndIsActive(uuid, true).getId();
        } else {
            return null;
        }
    }

    /**
     * Get deleted owner by UUID.
     *
     * @param uuid uuid of account/user.
     * @return user id.
     * @throws Exception unhandled exception.
     */
    public Long getDeletedOwnerByUuid(String uuid) throws Exception {
        if (userService.findByUuId(uuid) != null) {
            return userService.findByUuId(uuid).getId();
        } else {
            return null;
        }
    }

    /**
     * Get department object.
     *
     * @param uuid
     *            uuid of department.
     * @return department.
     * @throws Exception
     *             unhandled exception.
     */
    public Department getDepartment(String uuid) throws Exception {
        return departmentService.findByUuidAndIsActive(uuid, true);
    }

    /**
     * Get department object.
     *
     * @param id
     *            id of department.
     * @return department.
     * @throws Exception
     *             unhandled exception.
     */
    public Department getDepartmentById(Long id) throws Exception {
        return departmentService.find(id);
    }

    /**
     * Get department id.
     *
     * @param uuid
     *            uuid of department.
     * @return department id.
     * @throws Exception
     *             unhandled exception.
     */
    public Long getDepartmentId(String uuid) throws Exception {
        if (departmentService.findbyUUID(uuid) != null) {
            return departmentService.findbyUUID(uuid).getId();
        } else {
            return null;
        }
    }

    /**
     * Get vm Ip address object.
     *
     * @param id
     *            of ip address.
     * @return vm ipaddress.
     * @throws Exception
     *             unhandled exception.
     */
    public VmIpaddress getVmIpaddressById(Long id) throws Exception {
        return vmIpAddressService.findById(id);
    }

    /**
     * Get vm Ip address object.
     *
     * @param id
     *            of ip address.
     * @return vm ipaddress.
     * @throws Exception
     *             unhandled exception.
     */
  /*  public VmIpaddress getVmIpaddressByIpaddress(String ipaddress) throws Exception {
        return vmIpAddressService.findById(id);
    }*/

    /**
     * Get account object.
     *
     * @param uuid
     *            uuid of account.
     * @return account.
     * @throws Exception
     *             unhandled exception.
     */
    public Project getProject(String uuid) throws Exception {
        return projectService.findByUuid(uuid);
    }

    /**
     * Get Project object by id.
     *
     * @param id
     *            of project.
     * @return project.
     * @throws Exception
     *             unhandled exception.
     */
    public Project getProjectById(Long id) throws Exception {
        return projectService.find(id);
    }

    /**
     * Get project id.
     *
     * @param uuid
     *            uuid of account.
     * @return account.
     * @throws Exception
     *             unhandled exception.
     */
    public Long getProjectId(String uuid) throws Exception {
        if (projectService.findByUuid(uuid) != null) {
            return projectService.findByUuid(uuid).getId();
        }
        return null;
    }

    /**
     * Get pod id.
     *
     * @param uuid
     *            of pod.
     * @return pod id.
     * @throws Exception
     *             unhandled exception.
     */
    public Long getPodId(String uuid) throws Exception {
        if (podService.findByUUID(uuid) != null) {
            return podService.findByUUID(uuid).getId();
        }
        return null;
    }

    /**
     * Get volume id.
     *
     * @param uuid
     *            of pod.
     * @return pod id.
     * @throws Exception
     *             unhandled exception.
     */
    public Long getVolumeId(String uuid) throws Exception {
        if (volumeService.findByUUID(uuid) != null) {
            return volumeService.findByUUID(uuid).getId();
        }
        return null;
    }

    /**
     * Get instance id.
     *
     * @param uuid
     *            of instance.
     * @return instance id.
     * @throws Exception
     *             unhandled exception.
     */
    public Long getVmInstanceId(String uuid) throws Exception {
        if (virtualMachineService.findByUUID(uuid) != null) {
            return virtualMachineService.findByUUID(uuid).getId();
        } else {
            return null;
        }
    }

    /**
     * Get Host id.
     *
     * @param uuid
     *            of host.
     * @return host id.
     * @throws Exception
     *             unhandled exception.
     */
    public Long getHostId(String uuid) throws Exception {
        if (hostService.findByUUID(uuid) != null) {
            return hostService.findByUUID(uuid).getId();
        } else {
            return null;
        }
    }

    /**
     * Get Storage object.
     *
     * @param uuid
     *            uuid of department.
     * @return storage.
     * @throws Exception
     *             unhandled exception.
     */
    public Long getStorageOfferId(String uuid) throws Exception {
        if (storageService.findUuid(uuid) != null) {
            return storageService.findUuid(uuid).getId();
        }
        return null;
    }

    /**
     * Get Storage object by id.
     *
     * @param id
     *            of storage offer.
     * @return storage.
     * @throws Exception
     *             unhandled exception.
     */
    public StorageOffering getStorageOfferById(Long id) throws Exception {
        return storageService.find(id);
    }

    /**
     * Get Vm id.
     *
     * @param uuid
     *            of vm.
     * @return vm id.
     * @throws Exception
     *             unhandled exception.
     */
    public Long getVmId(String uuid) throws Exception {
        if (virtualMachineService.findByUUID(uuid) != null) {
            return virtualMachineService.findByUUID(uuid).getId();
        } else {
            return null;
        }
    }

    /**
     * Get Vm.
     *
     * @param uuid
     *            of vm.
     * @return vm.
     * @throws Exception
     *             unhandled exception.
     */
    public VmInstance getVm(String uuid) throws Exception {
        if (virtualMachineService.findByUUID(uuid) != null) {
            return virtualMachineService.findByUUID(uuid);
        } else {
            return null;
        }
    }

    /**
     * Get pod id.
     *
     * @param hostId
     *            host id.
     * @return pod id.
     * @throws Exception
     *             unhandled exception.
     */
    public Long getPodIdByHost(Long hostId) throws Exception {
        if (hostService.find(hostId) != null) {
            return hostService.find(hostId).getPodId();
        } else {
            return null;
        }
    }

    /**
     * Get domain id.
     *
     * @param uuid
     *            uuid of domain.
     * @return domain id.
     * @throws Exception
     *             unhandled exception.
     */
    public Long getDepartmentUuidId(String uuid) throws Exception {
        if (departmentService.findByUuidAndIsActive(uuid, true) != null) {
            return departmentService.findByUuidAndIsActive(uuid, true).getId();
        } else {
            return null;
        }
    }

    /**
     * Get domain id.
     *
     * @param name
     *            of the department.
     * @return domain id.
     * @throws Exception
     *             unhandled exception.
     */
    public Long getDepartmentByUsername(String name, Long domainId) throws Exception {
        if (departmentService.findByUsernameDomainAndIsActive(name, domainId, true) != null) {
            return departmentService.findByUsernameDomainAndIsActive(name, domainId, true).getId();
        } else {
            return null;
        }
    }

    /**
     * Get department id.
     *
     * @param name
     *            of the department.
     * @param domain
     *            of the department.
     * @return domain id.
     * @throws Exception
     *             unhandled exception.
     */
    public Long getDepartmentByUsernameAndDomains(String name, Domain domain) throws Exception {
        Department department = departmentService.findByUsernameDomainAndIsActive(name, domain.getId(), true);
        if (department != null) {
            return department.getId();
        } else {
            return null;
        }
    }

    /**
     * Get User id.
     *
     * @param domain
     *            object.
     * @param name
     *            of the user.
     * @return user id.
     * @throws Exception
     *             unhandled exception.
     */
    public Long getUserIdByAccount(String name, Domain domain) throws Exception {
        User user = userService.findByUserNameAndDomain(name, domain);
        if (user != null) {
            return user.getId();
        }
        return null;
    }

    /**
     * Get state of resource.
     *
     * @param state
     *            of the user.
     * @return true/false.
     * @throws Exception
     *             unhandled exception.
     */
    public Boolean getState(String state) throws Exception {
        if (state.equalsIgnoreCase("Active")) {
            return true;
        }
        return false;
    }

    /**
     * Get status of resource.
     *
     * @param status
     *            of the user.
     * @return status enum string.
     * @throws Exception
     *             unhandled exception.
     */
    public Enum getStatus(String status) throws Exception {
        if (status.equalsIgnoreCase("Active")) {
            return Project.Status.ENABLED;
        }
        return Project.Status.DELETED;
    }

    /**
     * Get secret key for generating token.
     *
     * @return original key.
     * @throws UnsupportedEncodingException
     *             unhandled errors.
     */
    public SecretKey getSecretKey() throws UnsupportedEncodingException {
        String strEncoded = Base64.getEncoder().encodeToString(secretKey.getBytes("utf-8"));
        byte[] decodedKey = Base64.getDecoder().decode(strEncoded);
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        return originalKey;
    }

    /**
     * Get the osCategory.
     *
     * @param uuid
     *            of osCategory.
     * @return osCategory id.
     * @throws Exception
     *             unhandled exception.
     */
    public OsCategory getOsCategory(String uuid) throws Exception {
        return osCategoryService.findbyUUID(uuid);
    }

    /**
     * Get the Iso.
     *
     * @param uuid
     *            of Iso.
     * @return Iso id.
     * @throws Exception
     *             unhandled exception.
     */
    public Long getIso(String uuid) throws Exception {
        if (isoService.findbyUUID(uuid) != null) {
            return isoService.findbyUUID(uuid).getId();
        } else {
            return null;
        }
    }

    /**
     * Get the Iso.
     *
     * @param uuid
     *            of Iso.
     * @return Iso id.
     * @throws Exception
     *             unhandled exception.
     */
    public Long getClusterId(String uuid) throws Exception {
        if (clusterService.findByUUID(uuid) != null) {
            return clusterService.findByUUID(uuid).getId();
        } else {
            return null;
        }
    }

    /**
     * Get the username.
     *
     * @param owner
     *            of username.
     * @param domain
     *            domain object..
     * @return osCategory id.
     * @throws Exception
     *             unhandled exception.
     */
    public Long getUserByName(String owner, Domain domain) throws Exception {
        if (userService.findByNameAndDomain(owner, domain) != null) {
            return userService.findByNameAndDomain(owner, domain).getId();
        }
        return null;
    }

    /**
     * Get Storage Offering By Id.
     *
     * @param storageOfferingId
     *            storageOffering Id.
     * @return storageOffering.
     * @throws Exception
     *             unhandled exception.
     */
    public String getStorageOfferingById(Long storageOfferingId) throws Exception {
        if (storageService.find(storageOfferingId) != null) {
            return storageService.find(storageOfferingId).getUuid();
        }
        return null;
    }

    /**
     * Get Zone By Id.
     *
     * @param zoneId
     *            Zone Id.
     * @return Zone.
     * @throws Exception
     *             unhandled exception.
     */
    public String getZoneUuidById(Long zoneId) throws Exception {
        if (zoneService.find(zoneId) != null) {
            return zoneService.find(zoneId).getUuid();
        } else {
            return null;
        }
    }

    /**
     * Get Project By Id.
     *
     * @param projectId
     *            Project Id.
     * @return Project.
     * @throws Exception
     *             unhandled exception.
     */
    public String getProjectUuidById(Long projectId) throws Exception {
        if (projectService.find(projectId) != null) {
            return projectService.find(projectId).getUuid();
        }
        return null;
    }

    /**
     * Get Domain UUID By domain id.
     *
     * @param domainId domain Id.
     * @return domain.
     * @throws Exception unhandled exception.
     */
    public String getDomainUuidById(Long domainId) throws Exception {
        if (domainService.find(domainId) != null) {
            return domainService.find(domainId).getUuid();
        }
        return null;
    }

    /**
     * Get Department By Id.
     *
     * @param departmentId
     *            Department Id.
     * @return Department.
     * @throws Exception
     *             unhandled exception.
     */
    public String getDepartmentUsernameById(Long departmentId) throws Exception {
        if (departmentService.find(departmentId) != null) {
            return departmentService.find(departmentId).getUserName();
        } else {
            return null;
        }
    }

    /**
     * Get the IP address id.
     *
     * @param uuid
     *            uuid of IP address.
     * @return IP address id.
     * @throws Exception
     *             unhandled exception.
     */
    public Long getIpAddressId(String uuid) throws Exception {
        if (ipAddressService.findbyUUID(uuid) != null) {
            return ipAddressService.findbyUUID(uuid).getId();
        } else {
            return null;
        }
    }

    /**
     * Get IP address by id.
     *
     * @param id
     *            of IP address.
     * @return IP address.
     * @throws Exception
     *             unhandled exception.
     */
    public IpAddress getIpAddress(Long id) throws Exception {
        return ipAddressService.find(id);
    }

    /**
     * Get load Balancer by id
     *
     * @param id of the load balancer.
     * @return load balancer rule
     * @throws Exception if error occurs.
     */
    public LoadBalancerRule getLoadBalancer(Long id) throws Exception {
        return lbService.find(id);
    }

    /**
     * Get virtual machine service object.
     *
     * @return virtual machine service object
     */
    public VirtualMachineService getInstanceService() {
        return this.virtualMachineService;
    }

    /**
     * Get volume service object.
     *
     * @return volume service object
     */
    public VolumeService getVolumeService() {
        return this.volumeService;
    }


    /**
     * Get vmsnapshot service object.
     *
     * @return vmsnapshot service object
     */
    public VmSnapshotService getVmSnapshotService() {
        return this.vmSnapshotService;
    }


    /**
     * Get NIC service object.
     *
     * @return NIC service object
     */
    public NicService getNicService() {
        return this.nicService;
    }

    /**
     * Get network service object.
     *
     * @return network service object
     */
    public NetworkService getNetworkService() {
        return this.networkService;
    }

    /**
     * Get Port Forwarding service object.
     *
     * @return PortForwarding service object
     */
    public PortForwardingService getPortForwardingService() {
        return this.portForwardingService;
    }

    /**
     * Get web socket service object.
     *
     * @return Websocket service object
     */
    public WebsocketService getWebsocket() {
        return this.websocketService;
    }

    /**
     * Get CloudStack Resource Capacity service object.
     *
     * @return CloudStack Resource Capacity service object
     */
    public CloudStackResourceCapacity getCloudStackResourceCapacityService() {
        return this.cloudStackResourceCapacity;
    }

    /**
     * Get Date and Time servie object.
     *
     * @return Date and time service object
     */
    public DateTimeService getTimeService() {
        return this.dateTimeService;
    }

    /**
     * Get Websocket server object.
     *
     * @return  Websocket server object.
     */
    public WebsocketService getWebsocketService() {
        return this.webSocket;
    }

    /**
     * Get Asynchronous Job service object.
     *
     * @return AsynchronousJob service object
     */
    public AsynchronousJobService getasyncService() {
        return this.asyncService;
    }

    /**
     * Get Cloud Stack server object.
     *
     * @return  Cloud Stack server object.
     */
    public CloudStackServer getCSConnecter() {
        return this.server;
    }

    /**
     * Get CloudStack configuration object.
     *
     * @return CloudStack configuration object.
     */
    public CloudStackConfigurationService getCSConfig() {
        return this.cloudConfigService;
    }

    /**
     * Get CloudStack instance service object.
     *
     * @return CloudStack instance service object
     */
    public CloudStackInstanceService getCSInstanceService() {
        return this.cloudStackInstanceService;
    }

    /**
     * Get Update Resource Count service object.
     *
     * @return Update Resource Count service object
     */
    public UpdateResourceCountService getUpdateResourceCountService() {
        // TODO Auto-generated method stub
        return this.updateResourceCountService;
    }

    /**
     * Update the resource count for current resource type.
     *
     * @param csResponse cloud stack response resource type for domain resource.
     * @throws Exception resource count error
     */
    public void resourceCount(String csResponse) throws Exception {
        JSONArray resourceCountArrayJSON = null;
        // get cloud stack resource count response
        JSONObject csCountJson = new JSONObject(csResponse).getJSONObject(CS_UPDATE_RESOURCE_RESPONSE);
        // If json response has resource count object
        if (csCountJson.has(CS_RESOUCE_COUNT)) {
            resourceCountArrayJSON = csCountJson.getJSONArray(CS_RESOUCE_COUNT);
            // Iterate resource count response from resource type
            for (int i = 0, size = resourceCountArrayJSON.length(); i < size; i++) {
                // get resource count, type, domain and set in a variable for
                // future use
                String resourceCount = resourceCountArrayJSON.getJSONObject(i).getString(CS_RESOUCE_COUNT);
                String resourceType = resourceCountArrayJSON.getJSONObject(i).getString(CS_RESOUCE_TYPE);
                String domainId = resourceCountArrayJSON.getJSONObject(i)
                        .getString(CloudStackConstants.CS_DOMAIN_ID);
                // check resource type other than 5(resource type of project)
                // and allow to update
                if (!resourceType.equals(CS_PROJECT)) {
                    // Map and get the resource count for current resource type
                    // value
                    HashMap<String, String> resourceMap = getResourceTypeValue();
                    // checking null validation for resource map
                    if (resourceMap != null && !resourceCountArrayJSON.getJSONObject(i).has("account") &&
                            !resourceCountArrayJSON.getJSONObject(i).has("project")) {
                        // update resource count in resource limit domain table
                        ResourceLimitDomain resourceDomainCount = resourceLimitDomainService
                                .findByDomainAndResourceCount(getDomainId(domainId),
                                        ResourceType.valueOf(resourceMap.get(resourceType)), true);
                        // check the max value if not -1 and upadate the
                        // available value
                        if (resourceDomainCount.getMax() != -1) {
                            // Check resource type primary = 10 and secondary
                            // storage = 11 and convert resource
                            // count values GiB to MB.
                            if (resourceType.equals(CS_PRIMARY_STORAGE) || resourceType.equals(CS_SECONDARY_STORAGE)) {
                                // Convert and set Available resource count of
                                // primary and secondary GiB to MB.
                                resourceDomainCount.setAvailable(resourceDomainCount.getMax()
                                        - (Long.valueOf(resourceCount) / (1024 * 1024 * 1024)));
                                // Convert and set Used resource count of
                                // primary and secondary GiB to MB.
                                resourceDomainCount.setUsedLimit((Long.valueOf(resourceCount) / (1024 * 1024 * 1024)));
                            } else {
                                resourceDomainCount
                                        .setAvailable(resourceDomainCount.getMax() - Long.valueOf(resourceCount));
                                resourceDomainCount.setUsedLimit(Long.valueOf(resourceCount));
                            }
                        } else {
                            resourceDomainCount.setAvailable(resourceDomainCount.getMax());
                            resourceDomainCount.setUsedLimit(Long.valueOf(resourceCount));
                        }
                        // Set used limit value
                        resourceDomainCount.setIsSyncFlag(false);
                        resourceLimitDomainService.update(resourceDomainCount);
                    }
                    if (resourceMap != null && resourceCountArrayJSON.getJSONObject(i).has("account")
                            && !resourceCountArrayJSON.getJSONObject(i).has("project")) {
                        String account = resourceCountArrayJSON.getJSONObject(i)
                                .getString(CloudStackConstants.CS_ACCOUNT);
                        // update resource count in resource limit domain table
                        ResourceLimitDepartment resourceDepartmentCount = resourceLimitDepartmentService
                                .findByDepartmentAndResourceType(getDepartmentByUsername(account, getDomainId(domainId)),
                                        ResourceLimitDepartment.ResourceType.valueOf(resourceMap.get(resourceType)), true);
                        // check the max value if not -1 and upadate the
                        // available value
                        if (resourceDepartmentCount != null) {
                            if (resourceDepartmentCount.getMax() != -1) {
                                // Check resource type primary = 10 and
                                // secondary
                                // storage = 11 and convert resource
                                // count values GiB to MB.
                                if (resourceType.equals(CS_PRIMARY_STORAGE)
                                        || resourceType.equals(CS_SECONDARY_STORAGE)) {
                                    // Convert and set Available resource count
                                    // of
                                    // primary and secondary GiB to MB.
                                    resourceDepartmentCount.setAvailable(resourceDepartmentCount.getMax()
                                            - (Long.valueOf(resourceCount) / (1024 * 1024 * 1024)));
                                    // Convert and set Used resource count of
                                    // primary and secondary GiB to MB.
                                    resourceDepartmentCount
                                            .setUsedLimit((Long.valueOf(resourceCount) / (1024 * 1024 * 1024)));
                                } else {
                                    resourceDepartmentCount.setAvailable(
                                            resourceDepartmentCount.getMax() - Long.valueOf(resourceCount));
                                    resourceDepartmentCount.setUsedLimit(Long.valueOf(resourceCount));
                                }
                            } else {
                                resourceDepartmentCount.setAvailable(resourceDepartmentCount.getMax());
                                resourceDepartmentCount.setUsedLimit(Long.valueOf(resourceCount));
                            }
                            // Set used limit value
                            resourceDepartmentCount.setIsSyncFlag(false);
                            resourceLimitDepartmentService.update(resourceDepartmentCount);
                        }
                    }
                    if (resourceMap != null && resourceCountArrayJSON.getJSONObject(i).has("project")) {
                        String projectId = resourceCountArrayJSON.getJSONObject(i)
                                .getString(CloudStackConstants.CS_PROJECT_ID);
                        // update resource count in resource limit domain table
                        ResourceLimitProject resourceProjectCount = resourceLimitProjectService.
                                findResourceByProjectAndResourceType(getProjectId(projectId), ResourceLimitProject.ResourceType.valueOf(resourceMap.get(resourceType)), true);

                        // check the max value if not -1 and upadate the
                        // available value
                        if (resourceProjectCount.getMax() != -1) {
                            // Check resource type primary = 10 and secondary
                            // storage = 11 and convert resource
                            // count values GiB to MB.
                            if (resourceType.equals(CS_PRIMARY_STORAGE) || resourceType.equals(CS_SECONDARY_STORAGE)) {
                                // Convert and set Available resource count of
                                // primary and secondary GiB to MB.
                                resourceProjectCount.setAvailable(resourceProjectCount.getMax()
                                        - (Long.valueOf(resourceCount) / (1024 * 1024 * 1024)));
                                // Convert and set Used resource count of
                                // primary and secondary GiB to MB.
                                resourceProjectCount.setUsedLimit((Long.valueOf(resourceCount) / (1024 * 1024 * 1024)));
                            } else {
                                resourceProjectCount
                                        .setAvailable(resourceProjectCount.getMax() - Long.valueOf(resourceCount));
                                resourceProjectCount.setUsedLimit(Long.valueOf(resourceCount));
                            }
                        } else {
                            resourceProjectCount.setAvailable(resourceProjectCount.getMax());
                            resourceProjectCount.setUsedLimit(Long.valueOf(resourceCount));
                        }
                        // Set used limit value
                        resourceProjectCount.setIsSyncFlag(false);
                        resourceLimitProjectService.update(resourceProjectCount);
                    }
                }
            }
        }
    }

    /**
     * Map and get the resource count for current resource type value.
     *
     * @return resourceMap resource count mapped values for resource type.
     */
    public HashMap<String, String> getResourceTypeValue() {
        HashMap<String, String> resourceMap = new HashMap<>();
        // Map and get the resource count for current resource type value
        resourceMap.put(CS_INSTANCE, String.valueOf(ResourceType.Instance));
        resourceMap.put(CS_IP, String.valueOf(ResourceType.IP));
        resourceMap.put(CS_VOLUME, String.valueOf(ResourceType.Volume));
        resourceMap.put(CS_SNAPSHOT, String.valueOf(ResourceType.Snapshot));
        resourceMap.put(CS_TEMPLATE, String.valueOf(ResourceType.Template));
        resourceMap.put(CS_NETWORK, String.valueOf(ResourceType.Network));
        resourceMap.put(CS_VPC, String.valueOf(ResourceType.VPC));
        resourceMap.put(CS_CPU, String.valueOf(ResourceType.CPU));
        resourceMap.put(CS_MEMORY, String.valueOf(ResourceType.Memory));
        resourceMap.put(CS_PRIMARY_STORAGE, String.valueOf(ResourceType.PrimaryStorage));
        resourceMap.put(CS_SECONDARY_STORAGE, String.valueOf(ResourceType.SecondaryStorage));
        return resourceMap;
    }

    /**
     * Get User details by account name and domain.
     *
     * @param domain
     *            object.
     * @param name
     *            of the user.
     * @return user id.
     * @throws Exception
     *             unhandled exception.
     */
    public User getUserIdByAccountAndDomain(String name, Domain domain) throws Exception {
        User user = userService.findByUserNameAndDomain(name, domain);
        if (user != null) {
            return user;
        }
        return null;
    }

}
