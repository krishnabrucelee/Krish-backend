package ck.panda.service;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Account;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.OsCategory;
import ck.panda.domain.entity.Project;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.VmInstance;

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
     * AccountService for listing network offers in cloudstack server.
     */
    @Autowired
    private AccountService accountService;


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
     * Get account object.
     *
     * @param uuid uuid of account.
     * @return account.
     * @throws Exception unhandled exception.
     */
    public Account getAccount(String uuid) throws Exception {
        return accountService.findByUuidAndIsActive(uuid, true);
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
     * Get domain id.
     *
     * @param domain object for account.
     * @param name of the domain.
     * @return domain id.
     * @throws Exception unhandled exception.
     */
    public Account getAccountByUsernameAndDomain(String name,Domain domain) throws Exception {
        if (accountService.findByNameAndDomainAndIsActive(name, domain, true) != null) {
            return accountService.findByNameAndDomainAndIsActive(name, domain, true);
        } else {
            return null;
        }
    }

    /**
     * Get department id.
     *
     * @param domain object for account.
     * @param name of the domain.
     * @return domain id.
     * @throws Exception unhandled exception.
     */
    public Long getDepartmentByUsernameAndDomain(String name, Domain domain) throws Exception {
        if (accountService.findByNameAndDomainAndIsActiveAndUserType(name, domain, true) != null) {
            return departmentService.findByUuidAndIsActive(
                    accountService.findByNameAndDomainAndIsActiveAndUserType(name, domain, true).getUuid(), true)
                    .getId();
        } else {
            return null;
        }
    }

    /**
     * Get Account id.
     *
     * @param name of the account.
     * @param domain uuid of domain.
     * @return account id.
     * @throws Exception unhandled exception.
     */
    public Long getAccountIdByUsernameAndDomain(String name, Domain domain) throws Exception {
        if (accountService.findByNameAndDomainAndIsActive(name, domain, true) != null) {
            return accountService.findByNameAndDomainAndIsActive(name, domain, true).getId();
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
        List<User> user = userService.findByAccountId(getAccountIdByUsernameAndDomain(name,domain));
        if (user.size() != 0) {
           return user.get(0).getId();
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
    public Long getUserByName(String owner, Domain domain)  throws Exception {
        if (userService.findByNameAndDomain(owner, domain) != null) {
            return userService.findByNameAndDomain(owner, domain).getId();
        }
        return null;
    }

}
