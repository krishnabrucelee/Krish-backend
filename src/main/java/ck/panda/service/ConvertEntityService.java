package ck.panda.service;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.ComputeOffering;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.Network;
import ck.panda.domain.entity.NetworkOffering;
import ck.panda.domain.entity.OsCategory;
import ck.panda.domain.entity.Project;
import ck.panda.domain.entity.StorageOffering;
import ck.panda.domain.entity.Template;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.Zone;

/**
 * Convert Util used to get entity object from CS server's resource uuid.
 */
@Service
public class ConvertEntityService {
    /** Domain Service for listing domains. */
    @Autowired
    private DomainService domainService;

    /** RegionSerivce for listing Regions. */
    @Autowired
    private ZoneService zoneService;

    /** IsoSerivce for listing Iso. */
    @Autowired
    private IsoService isoService;

    /** OSCategoryService for listing operating sytem in cloudstack server. */
    @Autowired
    private OsCategoryService osCategoryService;

    /** OSCategoryService for listing operating sytem in cloudstack server. */
    @Autowired
    private OsTypeService osTypeService;

    /** Storage Offering Service for listing storage offering. */
    @Autowired
    private StorageOfferingService storageService;

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

    /** Secret key value is append. */
    @Value(value = "${aes.salt.secretKey}")
    private String secretKey;

    /**
     * Get domain id.
     *
     * @param uuid uuid of domain.
     * @return domain id.
     * @throws Exception unhandled exception.
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
     * @param id of domain.
     * @return domain.
     * @throws Exception unhandled exception.
     */
    public Domain getDomainById(Long id) throws Exception {
        return domainService.find(id);
    }

    /**
     * Get zone id.
     *
     * @param uuid uuid of zone.
     * @return zone id.
     * @throws Exception unhandled exception.
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
     * @param id of zone.
     * @return zone.
     * @throws Exception unhandled exception.
     */
    public Zone getZoneById(Long id) throws Exception {
        return zoneService.find(id);
    }

    /**
     * Get template id.
     *
     * @param uuid uuid of template.
     * @return template id.
     * @throws Exception unhandled exception.
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
     * @param id of template.
     * @return template.
     * @throws Exception unhandled exception.
     */
    public Template getTemplateById(Long id) throws Exception {
        return templateService.find(id);
    }

    /**
     * Get compute offer id.
     *
     * @param uuid uuid of service offering.
     * @return computer offer id.
     * @throws Exception unhandled exception.
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
     * @param id of service offering.
     * @return computer offer .
     * @throws Exception unhandled exception.
     */
    public ComputeOffering getComputeOfferById(Long id) throws Exception {
        return computeService.find(id);
    }

    /**
     * Get the network id.
     *
     * @param uuid uuid of nic network.
     * @return netwotk id.
     * @throws Exception unhandled exception.
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
     * @param uuid uuid of nic network.
     * @return netwotk id.
     * @throws Exception unhandled exception.
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
     * @param uuid uuid of nic network.
     * @return netwotk id.
     * @throws Exception unhandled exception.
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
     * @param id of NetworkOffering
     * @return NetworkOffering.
     * @throws Exception unhandled exception.
     */
    public NetworkOffering getNetworkOfferingById(Long id) throws Exception {
        return networkOfferingService.findById(id);
    }

    /**
     * Get the ostype id.
     *
     * @param uuid uuid of nic network.
     * @return netwotk id.
     * @throws Exception unhandled exception.
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
     * @param uuid uuid of domain
     * @return domain.
     * @throws Exception unhandled exception.
     */
    public Domain getDomain(String uuid) throws Exception {
        return domainService.findbyUUID(uuid);
    }

    /**
     * Get Network object.
     *
     * @param id of network
     * @return network.
     * @throws Exception unhandled exception.
     */
    public Network getNetworkById(Long id) throws Exception {
        return networkService.findById(id);
    }

    /**
     * Get Vm Instance object.
     *
     * @param id of vm instance
     * @return network.
     * @throws Exception unhandled exception.
     */
    public VmInstance getVmInstanceById(Long id) throws Exception {
        return virtualMachineService.findById(id);
    }

    /**
     * Get owner id.
     *
     * @param name of account/user.
     * @param domain domain.
     * @return user id.
     * @throws Exception unhandled exception.
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
     * @param id of account/user.
     * @return user.
     * @throws Exception unhandled exception.
     */
    public User getOwnerById(Long id) throws Exception {
        return userService.find(id);
    }

    /**
     * Get department object.
     *
     * @param uuid uuid of department.
     * @return department.
     * @throws Exception unhandled exception.
     */
    public Department getDepartment(String uuid) throws Exception {
        return departmentService.findByUuidAndIsActive(uuid, true);
    }

    /**
     * Get department object.
     *
     * @param id id of department.
     * @return department.
     * @throws Exception unhandled exception.
     */
    public Department getDepartmentById(Long id) throws Exception {
        return departmentService.find(id);
    }

    /**
     * Get department id.
     *
     * @param uuid uuid of department.
     * @return department id.
     * @throws Exception unhandled exception.
     */
    public Long getDepartmentId(String uuid) throws Exception {
        if (departmentService.findbyUUID(uuid) != null) {
            return departmentService.findbyUUID(uuid).getId();
        } else {
            return null;
        }
    }

    /**
     * Get account object.
     *
     * @param uuid uuid of account.
     * @return account.
     * @throws Exception unhandled exception.
     */
    public Project getProject(String uuid) throws Exception {
        return projectService.findByUuidAndIsActive(uuid, true);
    }

    /**
     * Get Project object by id.
     *
     * @param id of project.
     * @return project.
     * @throws Exception unhandled exception.
     */
    public Project getProjectById(Long id) throws Exception {
        return projectService.find(id);
    }

    /**
     * Get project id.
     *
     * @param uuid uuid of account.
     * @return account.
     * @throws Exception unhandled exception.
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
     * @param uuid of pod.
     * @return pod id.
     * @throws Exception unhandled exception.
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
     * @param uuid of pod.
     * @return pod id.
     * @throws Exception unhandled exception.
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
     * @param uuid of instance.
     * @return instance id.
     * @throws Exception unhandled exception.
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
     * @param uuid of host.
     * @return host id.
     * @throws Exception unhandled exception.
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
     * @param uuid uuid of department.
     * @return storage.
     * @throws Exception unhandled exception.
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
     * @param id of storage offer.
     * @return storage.
     * @throws Exception unhandled exception.
     */
    public StorageOffering getStorageOfferById(Long id) throws Exception {
        return storageService.find(id);
    }

    /**
     * Get Vm id.
     *
     * @param uuid of vm.
     * @return vm id.
     * @throws Exception unhandled exception.
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
     * @param uuid of vm.
     * @return vm.
     * @throws Exception unhandled exception.
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
     * @param hostId host id.
     * @return pod id.
     * @throws Exception unhandled exception.
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
     * @param uuid uuid of domain.
     * @return domain id.
     * @throws Exception unhandled exception.
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
     * @param name of the department.
     * @return domain id.
     * @throws Exception unhandled exception.
     */
    public Long getDepartmentByUsername(String name) throws Exception {
        if (departmentService.findByUsername(name, true) != null) {
            return departmentService.findByUsername(name, true).getId();
        } else {
            return null;
        }
    }

    /**
     * Get department id.
     *
     * @param name of the department.
     * @param domain of the department.
     * @return domain id.
     * @throws Exception unhandled exception.
     */
    public Long getDepartmentByUsernameAndDomains(String name, Domain domain) throws Exception {
        if (departmentService.findByUsernameAndDomain(name, domain, true) != null) {
            return departmentService.findByUsernameAndDomain(name, domain, true).getId();
        } else {
            return null;
        }
    }

    /**
     * Get User id.
     *
     * @param domain object.
     * @param name of the user.
     * @return user id.
     * @throws Exception unhandled exception.
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
     * @param state of the user.
     * @return true/false.
     * @throws Exception unhandled exception.
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
     * @param status of the user.
     * @return status enum string.
     * @throws Exception unhandled exception.
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
     * @throws UnsupportedEncodingException unhandled errors.
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
     * @param uuid of osCategory.
     * @return osCategory id.
     * @throws Exception unhandled exception.
     */
    public OsCategory getOsCategory(String uuid) throws Exception {
        return osCategoryService.findbyUUID(uuid);
    }

    /**
     * Get the Iso.
     *
     * @param uuid of Iso.
     * @return Iso id.
     * @throws Exception unhandled exception.
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
     * @param uuid of Iso.
     * @return Iso id.
     * @throws Exception unhandled exception.
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
     * @param owner of username.
     * @param domain domain object..
     * @return osCategory id.
     * @throws Exception unhandled exception.
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
     * @param storageOfferingId storageOffering Id.
     * @return storageOffering.
     * @throws Exception unhandled exception.
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
     * @param zoneId Zone Id.
     * @return Zone.
     * @throws Exception unhandled exception.
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
     * @param projectId Project Id.
     * @return Project.
     * @throws Exception unhandled exception.
     */
    public String getProjectUuidById(Long projectId) throws Exception {
        if (projectService.find(projectId) != null) {
            return projectService.find(projectId).getUuid();
        }
        return null;
    }

    /**
     * Get Department By Id.
     *
     * @param departmentId Department Id.
     * @return Department.
     * @throws Exception unhandled exception.
     */
    public String getDepartmentUsernameById(Long departmentId) throws Exception {
        if (departmentService.find(departmentId) != null) {
            return departmentService.find(departmentId).getUserName();
        } else {
            return null;
        }
    }

}
