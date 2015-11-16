package ck.panda.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Domain;
import ck.panda.service.ComputeOfferingService;
import ck.panda.service.DepartmentService;
import ck.panda.service.DomainService;
import ck.panda.service.HypervisorService;
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

    /** OSCategoryService for listing operating sytem in cloudstack server. */
    @Autowired
    private OsCategoryService osCategoryService;

    /** OSCategoryService for listing operating sytem in cloudstack server. */
    @Autowired
    private OsTypeService osTypeService;

    /** Storage Offering Service for listing storage offering. */
    @Autowired
    private StorageOfferingService storageService;

    /** NetworkOfferingService for listing network offers in cloudstack server. */
    @Autowired
    private NetworkOfferingService networkOfferingService;

    /** NetworkOfferingService for listing network offers in cloudstack server. */
    @Autowired
    private NetworkService networkService;

    /** NetworkOfferingService for listing network offers in cloudstack server. */
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

    /**
     * Get domain id.
     *
     * @param uuid uuid of domain.
     * @return domain id.
     * @throws Exception unhandled exception.
     */
    public Long getDomainId(String uuid) throws Exception {
        return domainService.findbyUUID(uuid).getId();
    }

    /**
     * Get zone id.
     *
     * @param uuid uuid of zone.
     * @return zone id.
     * @throws Exception unhandled exception.
     */
    public Long getZoneId(String uuid) throws Exception {
        return zoneService.findByUUID(uuid).getId();

    }

    /**
     * Get template id.
     *
     * @param uuid uuid of template.
     * @return template id.
     * @throws Exception unhandled exception.
     */
    public Long getTemplateId(String uuid) throws Exception {
        return templateService.findByUUID(uuid).getId();
    }

    /**
     * Get compute offer id.
     *
     * @param uuid uuid of service offering.
     * @return computer offer id.
     * @throws Exception unhandled exception.
     */
    public Long getComputeOfferId(String uuid) throws Exception {
        return computeService.findByUUID(uuid).getId();
    }

    /**
     * Get the network id.
     *
     * @param uuid uuid of nic network.
     * @return netwotk id.
     * @throws Exception unhandled exception.
     */
    public Long getNetworkId(String uuid) throws Exception {
        return networkService.findByUUID(uuid).getId();
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
        return userService.findByUserNameAndDomain(name, domain).getId();
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
     * Get volume id.
     *
     * @param uuid of pod.
     * @return pod id.
     * @throws Exception unhandled exception.
     */
    public Long getVmInstanceId(String uuid) throws Exception {
        return virtualMachineService.findByUUID(uuid).getId();
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

}
