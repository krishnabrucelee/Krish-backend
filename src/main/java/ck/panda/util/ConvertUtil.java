package ck.panda.util;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.OsCategory;
import ck.panda.domain.entity.VmInstance;
import ck.panda.service.ComputeOfferingService;
import ck.panda.service.DepartmentService;
import ck.panda.service.DomainService;
import ck.panda.service.EncryptionUtil;
import ck.panda.service.HostService;
import ck.panda.service.HypervisorService;
import ck.panda.service.IsoService;
import ck.panda.service.NetworkOfferingService;
import ck.panda.service.NetworkService;
import ck.panda.service.OsCategoryService;
import ck.panda.service.OsTypeService;
import ck.panda.service.PodService;
import ck.panda.service.RegionService;
import ck.panda.service.StorageOfferingService;
import ck.panda.service.TemplateService;
import ck.panda.service.UserService;
import ck.panda.service.VirtualMachineService;
import ck.panda.service.VolumeService;
import ck.panda.service.ZoneService;

/**
 * Convert Util used to get entity object from CS server's resource uuid.
 */
@Component
public class ConvertUtil {
    /** Domain Service for listing domains. */
    @Autowired
    private DomainService domainService;

    /** RegionSerivce for listing Regions. */
    @Autowired
    private ZoneService zoneService;

    /** RegionSerivce for listing Regions. */
    @Autowired
    private RegionService regionService;

    /** RegionSerivce for listing Regions. */
    @Autowired
    private HypervisorService hypervisorService;

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

    /** volume servcie for listing volumes. */
    @Autowired
    private VolumeService volumeService;

    /** Host service for listing hosts. */
    @Autowired
    private HostService hostService;

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
        return networkOfferingService.findByUUID(uuid).getId();
    }

    /**
     * Get the ostype id.
     *
     * @param uuid uuid of nic network.
     * @return netwotk id.
     * @throws Exception unhandled exception.
     */
    public Long getOsTypeId(String uuid) throws Exception {
        return osTypeService.findByUUID(uuid).getId();
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
     * Get pod id.
     *
     * @param uuid of pod.
     * @return pod id.
     * @throws Exception unhandled exception.
     */
    public Long getPodId(String uuid) throws Exception {
        return podService.findByUUID(uuid).getId();
    }

    /**
     * Get volume id.
     *
     * @param uuid of pod.
     * @return pod id.
     * @throws Exception unhandled exception.
     */
    public Long getVolumeId(String uuid) throws Exception {
        return volumeService.findByUUID(uuid).getId();
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
     * @param uuid uuid of domain.
     * @return domain id.
     * @throws Exception unhandled exception.
     */
    public Department getDepartmentByUsername(String name) throws Exception {
        if (departmentService.findByUsername(name, true) != null) {
            return departmentService.findByUsername(name, true);
        } else {
            return null;
        }
    }

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

}
