package ck.panda.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import ck.panda.constants.PermissionUtil;
import ck.panda.domain.entity.CloudStackConfiguration;
import ck.panda.domain.entity.Cluster;
import ck.panda.domain.entity.ComputeOffering;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Department.AccountType;
import ck.panda.domain.entity.FirewallRules.Protocol;
import ck.panda.domain.entity.FirewallRules.State;
import ck.panda.domain.entity.FirewallRules.TrafficType;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.FirewallRules;
import ck.panda.domain.entity.Host;
import ck.panda.domain.entity.Hypervisor;
import ck.panda.domain.entity.IpAddress;
import ck.panda.domain.entity.Iso;
import ck.panda.domain.entity.LbStickinessPolicy;
import ck.panda.domain.entity.LoadBalancerRule;
import ck.panda.domain.entity.Network;
import ck.panda.domain.entity.NetworkOffering;
import ck.panda.domain.entity.Nic;
import ck.panda.domain.entity.OsCategory;
import ck.panda.domain.entity.OsType;
import ck.panda.domain.entity.Permission;
import ck.panda.domain.entity.Pod;
import ck.panda.domain.entity.PortForwarding;
import ck.panda.domain.entity.Project;
import ck.panda.domain.entity.Region;
import ck.panda.domain.entity.ResourceLimitDomain;
import ck.panda.domain.entity.ResourceLimitProject;
import ck.panda.domain.entity.Role;
import ck.panda.domain.entity.SSHKey;
import ck.panda.domain.entity.Snapshot;
import ck.panda.domain.entity.SnapshotPolicy;
import ck.panda.domain.entity.StorageOffering;
import ck.panda.domain.entity.Template;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.User.Status;
import ck.panda.domain.entity.User.UserType;
import ck.panda.domain.repository.jpa.VirtualMachineRepository;
import ck.panda.rabbitmq.util.ResponseEvent;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.VmSnapshot;
import ck.panda.domain.entity.Volume;
import ck.panda.domain.entity.Volume.VolumeType;
import ck.panda.domain.entity.VpnUser;
import ck.panda.domain.entity.Zone;
import ck.panda.util.CloudStackInstanceService;
import ck.panda.util.CloudStackResourceCapacity;
import ck.panda.util.CloudStackServer;
import ck.panda.util.EncryptionUtil;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;

/**
 * We have to sync up with cloudstack server for the following data
 *
 * 1. Zone 2. Domain 3. Region 4. Hypervisor 5. OS Catogory 6. OS Type 7. Network Offering 8. Template 9. User 10.
 * Network 11. Instance
 *
 * Get the corresponding data from cloud stack server, if the application does not have the data add it. If the
 * application has the data update it, if the application has data which the cloud stack server does not have, then
 * delete it.
 *
 */
@PropertySource(value = "classpath:permission.properties")
@Service
@SuppressWarnings("PMD.CyclomaticComplexity")
public class SyncServiceImpl implements SyncService {

    /** Domain Service for listing domains. */
    @Autowired
    private DomainService domainService;

    /** Ip address Service for listing ipaddress. */
    @Autowired
    private IpaddressService ipService;

    /** Egress Service for listing egressrules. */
    @Autowired
    private EgressRuleService egressRuleService;

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncServiceImpl.class);

    /** RegionSerivce for listing Regions. */
    @Autowired
    private ZoneService zoneService;

    /** Virtual machine Service for listing vms. */
    @Autowired
    private VirtualMachineService virtualMachineService;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** Virtual Machine repository reference. */
    @Autowired
    private VirtualMachineRepository virtualmachinerepository;

    /** RegionSerivce for listing Regions. */
    @Autowired
    private RegionService regionService;

    /** ProjectService for listing Regions. */
    @Autowired
    private ProjectService projectService;

    /** RegionSerivce for listing Regions. */
    @Autowired
    private HypervisorService hypervisorService;

    /** OSCategoryService for listing operating sytem in cloudstack server. */
    @Autowired
    private OsCategoryService osCategoryService;

    /** OSCategoryService for listing operating sytem in cloudstack server. */
    @Autowired
    private OsTypeService osTypeService;

    /** Service reference to Load Balancer. */
    @Autowired
    private LbStickinessPolicyService lbPolicyService;

    /** Storage offering service for listing storage offers. */
    @Autowired
    private StorageOfferingService storageService;

    /**
     * NetworkOfferingService for listing network offers in cloudstack server.
     */
    @Autowired
    private NetworkOfferingService networkOfferingService;

    /** CloudStack connector reference for instance. */
    @Autowired
    private CloudStackInstanceService cloudStackInstanceService;

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

    /** Department service for listing users. */
    @Autowired
    private DepartmentService departmentService;

    /** Nic service for listing nic. */
    @Autowired
    private NicService nicService;

    /** For listing users in cloudstack server. */
    @Autowired
    private UserService userService;

    /** For listing hosts in cloudstack server. */
    @Autowired
    private HostService hostService;

    /** For listing snapshots in cloudstack server. */
    @Autowired
    private SnapshotService snapshotService;


    /** For listing snapshots policies in cloudstack server. */
    @Autowired
    private SnapshotPolicyService snapshotPolicyService;

    /** For listing snapshots in cloudstack server. */
    @Autowired
    private VmSnapshotService vmsnapshotService;

    /** Template Service for listing templates. */
    @Autowired
    private TemplateService templateService;

    /** Volume Service for listing volumes. */
    @Autowired
    private VolumeService volumeService;

    /** Pod service for listing pods. */
    @Autowired
    private PodService podService;

    /** Cluster service for listing clusters. */
    @Autowired
    private ClusterService clusterService;

    /** For listing iso image in cloudstack server. */
    @Autowired
    private IsoService isoService;

    /** For listing Public Ip address from  cloudstack server. */
    @Autowired
    private IpaddressService ipAddressService;

    /** Egress service for listing egress rules. */
    @Autowired
    private EgressRuleService egressService;

    /** Resource Limit Service for listing resource limits. */
    @Autowired
    private ResourceLimitDomainService resourceDomainService;

    /** SSH Key Service for listing ssh keys. */
    @Autowired
    private SSHKeyService sshKeyService;

    /** CloudStack connector. */
    @Autowired
    private CloudStackServer server;

    /** CloudStack configuration . */
    @Autowired
    private CloudStackConfigurationService cloudConfigService;

    /** Secret key value is append. */
    @Value(value = "${aes.salt.secretKey}")
    private String secretKey;

    /** Autowired permission service. */
    @Autowired
    private PermissionService permissionService;

    @Autowired
    private VmIpaddressService vmIpService;

    /** Autowired roleService. */
    @Autowired
    private RoleService roleService;

    /** Listing Port Forwarding details from cloudstack server. */
    @Autowired
    private PortForwardingService portForwardingService;

    /** Listing resource limit project service. */
    @Autowired
    private ResourceLimitProjectService resourceProjectService;

    /** Listing Load Balancer details from cloudstack server. */
    @Autowired
    private LoadBalancerService loadBalancerService;

    /** CloudStack Resource Capacity Service. */
    @Autowired
    private CloudStackResourceCapacity cloudStackResourceCapacity;

    /** Permission instance properties. */
    @Value(value = "${permission.instance}")
    private String instance;

    /** Permission storage properties. */
    @Value(value = "${permission.storage}")
    private String storage;

    /** Permission network properties. */
    @Value(value = "${permission.network}")
    private String network;

    /** Permission sshkey properties. */
    @Value(value = "${permission.sshkey}")
    private String sshkey;

    /** Permission quatoLimit properties. */
    @Value(value = "${permission.quatolimit}")
    private String quatoLimit;

    /** Permission vpc properties. */
    @Value(value = "${permission.vpc}")
    private String vpc;

    /** Permission template properties. */
    @Value(value = "${permission.template}")
    private String template;

    /** Permission additionalServive properties. */
    @Value(value = "${permission.additionalservive}")
    private String additionalServive;

    /** Permission project properties. */
    @Value(value = "${permission.project}")
    private String project;

    /** Permission application properties. */
    @Value(value = "${permission.application}")
    private String application;

    /** Permission department properties. */
    @Value(value = "${permission.department}")
    private String department;

    /** Permission roles properties. */
    @Value(value = "${permission.roles}")
    private String roles;

    /** Permission user properties. */
    @Value(value = "${permission.user}")
    private String user;

    /** Permission ip address properties. */
    @Value(value = "${permission.network}")
    private String ipAdddress;

    /** Permission report properties. */
    @Value(value = "${permission.report}")
    private String report;

    /** Full permission for root and domain admin. */
    public static final String ADMIN_PERMISSION = "FULL_PERMISSION";

    /** Message source attribute. */
    @Autowired
    private MessageSource messageSource;

    /** For listing VPN user list from cloudstack server. */
    @Autowired
    private VpnUserService vpnUserService;

    @Override
    public void init(CloudStackServer server) throws Exception {
        CloudStackConfiguration cloudConfig = cloudConfigService.find(1L);
        this.server = server;
        this.server.setServer(cloudConfig.getApiURL(), cloudConfig.getSecretKey(), cloudConfig.getApiKey());
    }

    /**
     * Sync call for synchronization list of Region, domain, SSH key, region. template, hypervisor
     *
     * @throws Exception unhandled errors.
     */
    @Override
    public void sync() throws Exception {

        try {
            // 1. Sync Region entity
            this.syncRegion();
        } catch (Exception e) {
            Errors errors = new Errors(messageSource);
            errors.addGlobalError(
                    "Either of URL or API key or Secret key is wrong. please provide correct values and proceed ");
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            }
        }
        try {
            // 2. Sync Zone entity
            this.syncZone();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Zone", e);
        }
        try {
            // 3. Sync Domain entity
            this.syncDomain();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Domaim", e);
        }
        try {
            // 4. Sync Pod entity
            this.syncPod();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Pod", e);
        }
        try {
            // 5. Sync Cluster entity
            this.syncCluster();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch cluster", e);
        }
        try {
            // 6. Sync Host entity
            this.syncHost();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Host", e);
        }
        try {
            // 7. Sync Hypervisor entity
            this.syncHypervisor();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Hypervisor", e);
        }
        try {
            // 8. Sync Department entity
            this.syncDepartment();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Department", e);
        }
        try {
            // 9. Sync User entity
            this.syncUser();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch User", e);
        }
        try {
            // 10. Sync Project entity
            this.syncProject();
        } catch (Exception e) {
            LOGGER.error("ERROR AT sync Project", e);
        }
        try {
            // 11. Sync OSCategory entity
            this.syncOsCategory();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch OS Category", e);
        }
        try {
            // 12. Sync OSType entity
            this.syncOsTypes();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch OS Types", e);
        }
        try {
            // 13. Sync Network offering entity
            this.syncNetworkOffering();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch NetworkOffering", e);
        }
        try {
            // 14. Sync Compute Offering entity
            this.syncComputeOffering();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Compute Offering", e);
        }
        try {
            // 15. Sync Storage offering entity
            this.syncStorageOffering();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Storage Offering", e);
        }
        try {
            // 16. Sync Iso entity
            this.syncIso();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Iso", e);
        }
        try {
            // 17. Sync Network entity
            this.syncNetwork();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Network ", e);
        }
        try {
            // 18. Sync Templates entity
            this.syncTemplates();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Templates", e);
        }
       /* try {
            // 19. Sync ResourceLimit entity
            this.syncResourceLimit();
        } catch (Exception e) {
            LOGGER.error("ERROR AT sync ResourceLimit Domain", e);
        }*/
        try {
            // 20. Sync Instance entity
            this.syncInstances();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Instance", e);
        }
        try {
            // 21. Sync Volume entity
            this.syncVolume();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Volume", e);
        }
        try {
            // 22. Sync VmSnapshot entity
            this.syncVmSnapshots();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch vm snapshots", e);
        }
        try {
            // 23. Sync Snapshot entity
            this.syncSnapshot();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Snapshot", e);
        }
        try {
            // 24. Sync Nic entity
            this.syncNic();
            LOGGER.debug("nic");
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Nic", e);
        }
        try {
            // 25. Sync IP address entity
            this.syncIpAddress();
            LOGGER.debug("ipAddress");
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Ip Address", e);
        }
        try {
            // 26. Sync Egress firewall rules entity
            this.syncEgressFirewallRules();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch EgressRule", e);
        }
        try {
            // 27. Sync Ingress firewall rules entity
            this.syncIngressFirewallRules();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch EgressRule", e);
        }
        try {
            // 28. Sync Port Forwarding entity
            this.syncPortForwarding();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch PortForwarding", e);
        }
        try {
            // 29. Sync SnapshotPolicy entity
            this.syncSnapshotPolicy();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch SnapshotPolicy", e);
        }
        try {
            // 29. Sync Load Balancer entity
            this.syncLoadBalancer();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch LoadBalancer", e);
        }

       try {
            // 29. Sync Load Balancer entity
            this.syncLoadBalancerStickyPolicy();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch LoadBalancer", e);
        }
        try {
            // 30. Sync SSHKey entity
            this.syncSSHKey();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch SSH Key", e);
        }
        try {
            // 31. Sync for update role in user entity
            this.syncUpdateUserRole();
        } catch (Exception e) {
            LOGGER.error("ERROR AT Sync for update role in user entity", e);
        }
        try {
            // 32. Sync VPN user entity
            this.syncVpnUser();
            LOGGER.debug("ipAddress");
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Ip Address", e);
        }

    }

    /**
     * Sync with CloudStack server Domain.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors
     */
    @Override
    public void syncDomain() throws ApplicationException, Exception {

        // 1. Get all the domain objects from CS server as hash
        List<Domain> csDomainList = domainService.findAllFromCSServer();
        HashMap<String, Domain> csDomainMap = (HashMap<String, Domain>) Domain.convert(csDomainList);

        // 2. Get all the domain objects from application
        List<Domain> appDomainList = domainService.findAllDomain();

        // 3. Iterate application domain list
        LOGGER.debug("Total rows updated : " + (appDomainList.size()));
        for (Domain domain : appDomainList) {
            domain.setSyncFlag(false);
            // 3.1 Find the corresponding CS server domain object by finding it
            // in a hash using uuid
            if (csDomainMap.containsKey(domain.getUuid())) {
                Domain csDomain = csDomainMap.get(domain.getUuid());

                domain.setCompanyNameAbbreviation(csDomain.getCompanyNameAbbreviation());

                // 3.2 If found, update the domain object in app db
                domainService.update(domain);

                // 3.3 Remove once updated, so that we can have the list of cs
                // domain which is not added in the app
                csDomainMap.remove(domain.getUuid());
            } else {
                domainService.softDelete(domain);
            }
        }
        // 4. Get the remaining list of cs server hash domain object, then
        // iterate and
        // add it to app db
        for (String key : csDomainMap.keySet()) {
            LOGGER.debug("Syncservice domain uuid:");
            domainService.save(csDomainMap.get(key));
            LOGGER.debug("Total rows added", (csDomainMap.size()));
        }
    }

    /**
     * Sync with Cloud Server Zone.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    @Override
    public void syncZone() throws ApplicationException, Exception {

        // 1. Get all the zone objects from CS server as hash
        List<Zone> csZoneList = zoneService.findAllFromCSServer();
        HashMap<String, Zone> csZoneMap = (HashMap<String, Zone>) Zone.convert(csZoneList);

        // 2. Get all the zone objects from application
        List<Zone> appZoneList = zoneService.findAll();

        // 3. Iterate application zone list
        for (Zone zone : appZoneList) {
            LOGGER.debug("Total rows updated : " + (appZoneList.size()));
            // 3.1 Find the corresponding CS server zone object by finding it in
            // a hash using uuid
            if (csZoneMap.containsKey(zone.getUuid())) {
                Zone csZone = csZoneMap.get(zone.getUuid());

                zone.setName(csZone.getName());

                // 3.2 If found, update the zone object in app db
                zoneService.update(zone);

                // 3.3 Remove once updated, so that we can have the list of cs
                // zone which is not added in the app
                csZoneMap.remove(zone.getUuid());
            } else {
                if (!zone.getIsActive()) {
                    zoneService.softDelete(zone);
                }
            }
        }
        // 4. Get the remaining list of cs server hash zone object, then iterate
        // and
        // add it to app db
        for (String key : csZoneMap.keySet()) {
            LOGGER.debug("Syncservice zone uuid:");
            zoneService.save(csZoneMap.get(key));
        }
        LOGGER.debug("Total rows added : " + (csZoneMap.size()));
    }

    /**
     * Sync with Cloud Server Region.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    @Override
    public void syncRegion() throws ApplicationException, Exception {

        // 1. Get all the region objects from CS server as hash
        List<Region> csRegionList = regionService.findAllFromCSServer();
        HashMap<String, Region> csRegionMap = (HashMap<String, Region>) Region.convert(csRegionList);

        // 2. Get all the region objects from application
        List<Region> appRegionList = regionService.findAll();

        // 3. Iterate application region list
        for (Region region : appRegionList) {
            LOGGER.debug("Total rows updated : " + (appRegionList.size()));
            // 3.1 Find the corresponding CS server region object by finding it
            // in a hash using uuid
            if (csRegionMap.containsKey(region.getName())) {
                Region csRegion = csRegionMap.get(region.getName());

                region.setName(csRegion.getName());
                region.setEndPoint(csRegion.getEndPoint());

                // 3.2 If found, update the region object in app db
                regionService.update(region);

                // 3.3 Remove once updated, so that we can have the list of cs
                // region which is not added in the app
                csRegionMap.remove(region.getName());
            } else {
                regionService.delete(region);
            }
        }
        // 4. Get the remaining list of cs server hash region object, then
        // iterate and
        // add it to app db
        for (String key : csRegionMap.keySet()) {
            LOGGER.debug("Syncservice region name:");
            regionService.save(csRegionMap.get(key));
        }
        LOGGER.debug("Total rows added : " + (csRegionMap.size()));

    }

    /**
     * Sync with Cloud Server Hypervisor.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    @Override
    public void syncHypervisor() throws ApplicationException, Exception {

        // 1. Get all the hypervisor objects from CS server as hash
        List<Hypervisor> csHypervisorList = hypervisorService.findAllFromCSServer();
        HashMap<String, Hypervisor> csHypervisorMap = (HashMap<String, Hypervisor>) Hypervisor
                .convert(csHypervisorList);

        // 2. Get all the hypervisor objects from application
        List<Hypervisor> appHypervisorList = hypervisorService.findAll();

        // 3. Iterate application hypervisor list
        for (Hypervisor hypervisor : appHypervisorList) {
            LOGGER.debug("Total rows updated : " + (appHypervisorList.size()));
            // 3.1 Find the corresponding CS server hypervisor object by finding
            // it in a hash using uuid
            if (csHypervisorMap.containsKey(hypervisor.getName())) {
                Hypervisor csHypervisor = csHypervisorMap.get(hypervisor.getName());

                hypervisor.setName(csHypervisor.getName());
                // 3.2 If found, update the hypervisor object in app db
                hypervisorService.update(hypervisor);

                // 3.3 Remove once updated, so that we can have the list of cs
                // hypervisor which is not added in the app
                csHypervisorMap.remove(hypervisor.getName());
            } else {
                hypervisorService.delete(hypervisor);
            }
        }
        // 4. Get the remaining list of cs server hash hypervisor object, then
        // iterate and
        // add it to app db
        for (String key : csHypervisorMap.keySet()) {
            LOGGER.debug("Syncservice hypervisor uuid :");
            hypervisorService.save(csHypervisorMap.get(key));
        }
        LOGGER.debug("Total rows added : " + (csHypervisorMap.size()));

    }

    /**
     * Sync with Cloud Server osCategory.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    @Override
    public void syncOsCategory() throws ApplicationException, Exception {

        // 1. Get all the oscategory objects from CS server as hash
        List<OsCategory> csOsCategoryList = osCategoryService.findAllFromCSServer();
        HashMap<String, OsCategory> csOsCategoryMap = (HashMap<String, OsCategory>) OsCategory
                .convert(csOsCategoryList);

        // 2. Get all the oscategory objects from application
        List<OsCategory> appOsCategoryList = osCategoryService.findAll();

        // 3. Iterate application oscategory list
        for (OsCategory osCategory : appOsCategoryList) {
            LOGGER.debug("Total rows updated : " + (appOsCategoryList.size()));
            // 3.1 Find the corresponding CS server oscategory object by finding
            // it in a hash using uuid
            if (csOsCategoryMap.containsKey(osCategory.getUuid())) {
                OsCategory csOsCategory = csOsCategoryMap.get(osCategory.getUuid());

                osCategory.setName(csOsCategory.getName());
                // 3.2 If found, update the oscategory object in app db
                osCategoryService.update(osCategory);

                // 3.3 Remove once updated, so that we can have the list of cs
                // oscategory which is not added in the app
                csOsCategoryMap.remove(osCategory.getUuid());
            } else {
                osCategoryService.delete(osCategory);

            }
        }
        // 4. Get the remaining list of cs server hash oscategory object, then
        // iterate and
        // add it to app db
        for (String key : csOsCategoryMap.keySet()) {
            LOGGER.debug("Syncservice os category uuid:");
            osCategoryService.save(csOsCategoryMap.get(key));
        }
        LOGGER.debug("Total rows added : " + (csOsCategoryMap.size()));

    }

    /**
     * Sync with CloudStack server osType.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors
     */
    @Override
    public void syncOsTypes() throws ApplicationException, Exception {

        // 1. Get all the osType objects from CS server as hash
        List<OsType> csOsTypesList = osTypeService.findAllFromCSServer();
        HashMap<String, OsType> csOsTypeMap = (HashMap<String, OsType>) OsType.convert(csOsTypesList);

        // 2. Get all the osType objects from application
        List<OsType> appOsTypeList = osTypeService.findAll();

        // 3. Iterate application osType list
        for (OsType osType : appOsTypeList) {
            LOGGER.debug("Total rows updated : " + (appOsTypeList.size()));
            // 3.1 Find the corresponding CS server osType object by finding it
            // in a hash using uuid
            if (csOsTypeMap.containsKey(osType.getUuid())) {
                OsType csOsType = csOsTypeMap.get(osType.getUuid());
                csOsType.setDescription(csOsType.getDescription());
                csOsType.setOsCategoryId(csOsType.getOsCategoryId());

                // 3.2 If found, update the osType object in app db
                osTypeService.update(osType);

                // 3.3 Remove once updated, so that we can have the list of cs
                // osType which is not added in the app
                csOsTypeMap.remove(osType.getUuid());
            } else {
                osTypeService.delete(osType);
            }
        }
        // 4. Get the remaining list of cs server hash osType object, then
        // iterate and
        // add it to app db
        for (String key : csOsTypeMap.keySet()) {
            LOGGER.debug("Syncservice osType uuid :");
            osTypeService.save(csOsTypeMap.get(key));
        }
        LOGGER.debug("Total rows added : " + (csOsTypeMap.size()));

    }

    /**
     * Sync with CloudStack server osType.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors
     */
    @Override
    public void syncStorageOffering() throws ApplicationException, Exception {

        // 1. Get all the StorageOffering objects from CS server as hash
        List<StorageOffering> csStorageOfferingsList = storageService.findAllFromCSServer();
        HashMap<String, StorageOffering> csStorageOfferingMap = (HashMap<String, StorageOffering>) StorageOffering
                .convert(csStorageOfferingsList);

        // 2. Get all the osType objects from application
        List<StorageOffering> appstorageServiceList = storageService.findAll();

        // 3. Iterate application osType list
        for (StorageOffering storageOffering : appstorageServiceList) {
            LOGGER.debug("Total rows updated : " + (appstorageServiceList.size()));
            storageOffering.setIsSyncFlag(false);
            // 3.1 Find the corresponding CS server osType object by finding it
            // in a hash using uuid
            if (csStorageOfferingMap.containsKey(storageOffering.getUuid())) {
                StorageOffering csStorageOffering = csStorageOfferingMap.get(storageOffering.getUuid());
                if (csStorageOffering.getTransDomainId() != null){
                    storageOffering.setDomainId(convertEntityService.getDomain(csStorageOffering.getTransDomainId()).getId());
                }

                // 3.2 If found, update the osType object in app db
                storageService.update(storageOffering);

                // 3.3 Remove once updated, so that we can have the list of cs
                // osType which is not added in the app
                csStorageOfferingMap.remove(storageOffering.getUuid());
            } else {
                storageService.softDelete(storageOffering);
            }
        }
        // 4. Get the remaining list of cs server hash osType object, then
        // iterate and
        // add it to app db
        for (String key : csStorageOfferingMap.keySet()) {
            LOGGER.debug("Syncservice storage offering uuid:");
            storageService.save(csStorageOfferingMap.get(key));
        }
        LOGGER.debug("Total rows added : " + (csStorageOfferingMap.size()));

    }

    /**
     * Sync with Cloud Server Account.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    @Override
    public void syncUser() throws ApplicationException, Exception {

        // 1. Get all the user objects from CS server as hash
        List<User> csUserService = userService.findAllFromCSServerByDomain();
        HashMap<String, User> csUserMap = (HashMap<String, User>) User.convert(csUserService);

        // 2. Get all the user objects from application
        List<User> appUserList = userService.findAll();

        // 3. Iterate application user list
        for (User user : appUserList) {
            LOGGER.debug("Total rows updated : " + (appUserList.size()));
            user.setSyncFlag(false);
            // 3.1 Find the corresponding CS server user object by finding it in
            // a hash using uuid
            if (csUserMap.containsKey(user.getUuid())) {
                User csUser = csUserMap.get(user.getUuid());

                user.setFirstName(csUser.getFirstName());
                user.setLastName(csUser.getLastName());
                user.setEmail(csUser.getEmail());
                user.setUserName(csUser.getUserName());
                user.setIsActive(true);
                user.setStatus(Status.ACTIVE);
                // 3.2 If found, update the user object in app db
                userService.update(user);

                // 3.3 Remove once updated, so that we can have the list of cs
                // user which is not added in the app
                csUserMap.remove(user.getUuid());
            } else {
                userService.softDelete(user);
                // 3.2 If not found, delete it from app db
                // TODO clarify the business requirement, since it has impact in
                // the application if it is used
                // TODO clarify is this a soft or hard delete
            }

        }
        // 4. Get the remaining list of cs server hash user object, then iterate
        // and
        // add it to app db
        for (String key : csUserMap.keySet()) {
            LOGGER.debug("Syncservice user uuid:");
            userService.save(csUserMap.get(key));
        }
        LOGGER.debug("Total rows added : " + (csUserMap.size()));

    }

    /**
     * Sync with CloudStack server Network offering.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors
     */
    @Override
    public void syncNetworkOffering() throws ApplicationException, Exception {

        // 1. Get all the networkOffering objects from CS server as hash
        List<NetworkOffering> csNetworkOfferingList = networkOfferingService.findAllFromCSServer();
        HashMap<String, NetworkOffering> csNetworkOfferingMap = (HashMap<String, NetworkOffering>) NetworkOffering
                .convert(csNetworkOfferingList);

        // 2. Get all the networkOffering objects from application
        List<NetworkOffering> appNetworkOfferingList = networkOfferingService.findAll();

        // 3. Iterate application networkOffering list
        for (NetworkOffering networkOffering : appNetworkOfferingList) {
            LOGGER.debug("Total rows updated : " + (appNetworkOfferingList.size()));
            // 3.1 Find the corresponding CS server networkOfferingService
            // object by finding it in a hash using uuid
            if (csNetworkOfferingMap.containsKey(networkOffering.getUuid())) {
                NetworkOffering csNetworkOffering = csNetworkOfferingMap.get(networkOffering.getUuid());

                networkOffering.setName(csNetworkOffering.getName());

                // 3.2 If found, update the networkOffering object in app db
                networkOfferingService.update(networkOffering);

                // 3.3 Remove once updated, so that we can have the list of cs
                // networkOffering which is not added in the app
                csNetworkOfferingMap.remove(networkOffering.getUuid());
            } else {
                networkOfferingService.delete(networkOffering);
                // 3.2 If not found, delete it from app db
                // TODO clarify the business requirement, since it has impact in
                // the application if it is used
                // TODO clarify is this a soft or hard delete
            }
        }
        // 4. Get the remaining list of cs server hash NetworkOffering object,
        // then iterate and
        // add it to app db
        for (String key : csNetworkOfferingMap.keySet()) {
            LOGGER.debug("Syncservice networking offering uuid:");
            networkOfferingService.save(csNetworkOfferingMap.get(key));
        }
    }

    /**
     * Sync with CloudStack server Domain.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors
     */
    @Override
    public void syncNetwork() throws ApplicationException, Exception {

        // 1. Get all the network objects from CS server as hash
        List<Network> csNetworkList = networkService.findAllFromCSServerByDomain();
        HashMap<String, Network> csNetworkMap = (HashMap<String, Network>) Network.convert(csNetworkList);

        // 2. Get all the network objects from application
        List<Network> appNetworkList = networkService.findAll();

        // 3. Iterate application network list
        for (Network network : appNetworkList) {
            LOGGER.debug("Total rows updated : " + (appNetworkList.size()));
            network.setSyncFlag(false);
            // 3.1 Find the corresponding CS server network object by finding it
            // in a hash using uuid
            if (csNetworkMap.containsKey(network.getUuid())) {
                Network csNetwork = csNetworkMap.get(network.getUuid());

                network.setName(csNetwork.getName());
                network.setDomainId(csNetwork.getDomainId());
                network.setZoneId(csNetwork.getZoneId());
                network.setDisplayText(csNetwork.getDisplayText());
                network.setStatus(csNetwork.getStatus());
                network.setNetworkDomain(csNetwork.getNetworkDomain());
                network.setNetMask(csNetwork.getNetMask());
                network.setNetworkOfferingId(csNetwork.getNetworkOfferingId());
                network.setIsActive(true);

                // 3.2 If found, update the network object in app db
                networkService.update(network);

                // 3.3 Remove once updated, so that we can have the list of cs
                // network which is not added in the app
                csNetworkMap.remove(network.getUuid());
            } else {
                networkService.softDelete(network);
            }
        }
        // 4. Get the remaining list of cs server hash network object, then
        // iterate and
        // add it to app db
        for (String key : csNetworkMap.keySet()) {
            LOGGER.debug("Syncservice network uuid:");
            networkService.save(csNetworkMap.get(key));
        }
        LOGGER.debug("Total rows added : " + (csNetworkMap.size()));

    }

    /**
     * Sync with CloudStack server Domain.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors
     */
    @Override
    public void syncComputeOffering() throws ApplicationException, Exception {

        // 1. Get all the compute offering objects from CS server as hash
        List<ComputeOffering> csComputeOfferingList = computeService.findAllFromCSServer();
        HashMap<String, ComputeOffering> csComputeOfferingMap = (HashMap<String, ComputeOffering>) ComputeOffering
                .convert(csComputeOfferingList);

        // 2. Get all the compute offering objects from application
        List<ComputeOffering> appComputeList = computeService.findAll();

        // 3. Iterate application compute offering list
        for (ComputeOffering computeOffering : appComputeList) {
            LOGGER.debug("Total rows updated : " + (appComputeList.size()));
            computeOffering.setIsSyncFlag(false);
            // 3.1 Find the corresponding CS server compute offering object by
            // finding it in a hash using uuid
            if (csComputeOfferingMap.containsKey(computeOffering.getUuid())) {
                ComputeOffering csComputeService = csComputeOfferingMap.get(computeOffering.getUuid());

                computeOffering.setName(csComputeService.getName());
                computeOffering.setDisplayText(csComputeService.getDisplayText());

                // 3.2 If found, update the compute offering object in app db
                computeService.update(computeOffering);

                // 3.3 Remove once updated, so that we can have the list of cs
                // compute offering which is not added in the app
                csComputeOfferingMap.remove(computeOffering.getUuid());
            } else {
                computeService.softDelete(computeOffering);
            }
        }
        // 4. Get the remaining list of cs server hash domain object, then
        // iterate and
        // add it to app db
        for (String key : csComputeOfferingMap.keySet()) {
            LOGGER.debug("Syncservice compute offering uuid:");
            computeService.save(csComputeOfferingMap.get(key));
        }
        LOGGER.debug("Total rows added : " + (csComputeOfferingMap.size()));
    }

    /**
     * Sync with CloudStack server template.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors
     */
    @Override
    public void syncTemplates() throws ApplicationException, Exception {

        // 1. Get all the template objects from CS server as hash
        List<Template> csTemplatesList = templateService.findAllFromCSServer();
        HashMap<String, Template> csTemplateMap = (HashMap<String, Template>) Template.convert(csTemplatesList);

        // 2. Get all the template objects from application
        List<Template> appTemplateList = templateService.findAll();

        // 3. Iterate application template list
        for (Template template : appTemplateList) {
            LOGGER.debug("Total rows updated : " + (appTemplateList.size()));
            // 3.1 Find the corresponding CS server template object by finding
            // it in a hash using uuid
            if (csTemplateMap.containsKey(template.getUuid())) {
                Template csTemplate = csTemplateMap.get(template.getUuid());

                template.setSyncFlag(false);
                template.setUuid(csTemplate.getUuid());
                template.setName(csTemplate.getName());
                template.setDescription(csTemplate.getDescription());
                template.setShare(csTemplate.getShare());
                template.setPasswordEnabled(csTemplate.getPasswordEnabled());
                template.setFormat(csTemplate.getFormat());
                template.setFeatured(csTemplate.getFeatured());
                template.setOsTypeId(csTemplate.getOsTypeId());
                template.setZoneId(csTemplate.getZoneId());
                template.setStatus(csTemplate.getStatus());
                template.setType(csTemplate.getType());
                template.setHypervisorId(csTemplate.getHypervisorId());
                template.setExtractable(csTemplate.getExtractable());
                template.setDynamicallyScalable(csTemplate.getDynamicallyScalable());

                // 3.2 If found, update the template object in app db
                templateService.update(template);

                // 3.3 Remove once updated, so that we can have the list of cs
                // template which is not added in the app
                csTemplateMap.remove(template.getUuid());
            } else {
                template.setSyncFlag(false);
                templateService.softDelete(template);
            }
        }
        // 4. Get the remaining list of cs server hash template object, then
        // iterate and
        // add it to app db
        for (String key : csTemplateMap.keySet()) {
            templateService.save(csTemplateMap.get(key));
        }
        LOGGER.debug("Total rows added : " + (csTemplateMap.size()));

    }

    /**
     * Sync with Cloud Server Account.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    @Override
    public void syncDepartment() throws ApplicationException, Exception {
        // 1. Get all the department objects from CS server as hash
        List<Department> csAccountService = departmentService.findAllFromCSServer();
        HashMap<String, Department> csUserMap = (HashMap<String, Department>) Department.convert(csAccountService);
        // 2. Get all the department objects from application
        List<Department> appDepartmentList = departmentService.findAllBySync();
        // 3. Iterate application department list
        for (Department department : appDepartmentList) {
            department.setSyncFlag(false);
            // 3.1 Find the corresponding CS server user object by finding it in a hash using uuid
            if (csUserMap.containsKey(department.getUuid())) {
                Department csUser = csUserMap.get(department.getUuid());
                department.setUserName(csUser.getUserName());
                // 3.2 If found, update the department object in app db
                departmentService.update(department);
                // 3.3 Remove once updated, so that we can have the list of cs department which is not
                // added in the app
                csUserMap.remove(department.getUuid());
            } else {
                departmentService.softDelete(department);
            }
        }
        // 4. Get the remaining list of cs server hash department object, then iterate and add it to app db
        for (String key : csUserMap.keySet()) {
            departmentService.save(csUserMap.get(key));
        }
        // Create default roles and permissions
        List<Permission> existPermissionList = new ArrayList<Permission>();
        List<Department> departmentList = new ArrayList<Department>();
        existPermissionList = permissionService.findAll();
        List<AccountType> types = new ArrayList<AccountType>();
        types.add(AccountType.ROOT_ADMIN);
        types.add(AccountType.DOMAIN_ADMIN);
        departmentList = departmentService.findByAccountTypesAndActive(types, true);
        createRole(departmentList, existPermissionList);
    }

    /**
     * Sync with Cloud Server Instance.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    @Override
    public void syncInstances() throws Exception {
        // 1. Get all the vm objects from CS server as hash
        List<VmInstance> csInstanceService = virtualMachineService.findAllFromCSServer();
        HashMap<String, VmInstance> vmMap = (HashMap<String, VmInstance>) VmInstance.convert(csInstanceService);
        // 2. Get all the vm objects from application
        List<VmInstance> appVmList = (List<VmInstance>) virtualmachinerepository.findAll();
        // 3. Iterate application user list
        for (VmInstance instance : appVmList) {
            instance.setSyncFlag(false);
            // 3.1 Find the corresponding CS server vm object by finding it in a
            // hash using uuid
            if (vmMap.containsKey(instance.getUuid())) {
                VmInstance csVm = vmMap.get(instance.getUuid());
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
                    instance.setDepartmentId(convertEntityService.getProjectById(csVm.getProjectId()).getDepartmentId());
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
                virtualMachineService.update(instance);

                // 3.3 Remove once updated, so that we can have the list of cs
                // vm which is not added in the
                // app
                vmMap.remove(instance.getUuid());
            } else {
                // 3.2 If not found, delete it from app db
                virtualMachineService.delete(instance);
            }
        }
        // 4. Get the remaining list of cs server hash vm object, then iterate
        // and
        // add it to app db
        for (String key : vmMap.keySet()) {
            virtualMachineService.save(vmMap.get(key));
        }
    }

    /**
     * Sync with Cloud Server Host.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    private void syncHost() throws ApplicationException, Exception {

        // 1. Get all the host objects from CS server as hash
        List<Host> csHostService = hostService.findAllFromCSServer();
        HashMap<String, Host> csHostMap = (HashMap<String, Host>) Host.convert(csHostService);

        // 2. Get all the host objects from application
        List<Host> appHostList = hostService.findAll();

        // 3. Iterate application host list
        for (Host host : appHostList) {
            // 3.1 Find the corresponding CS server host object by finding it in
            // a hash using uuid
            if (csHostMap.containsKey(host.getUuid())) {
                Host csHost = csHostMap.get(host.getUuid());
                host.setName(csHost.getName());
                host.setHostIpaddress(csHost.getHostIpaddress());
                host.setStatus(csHost.getStatus());
                // 3.2 If found, update the user object in app db
                hostService.update(host);

                // 3.3 Remove once updated, so that we can have the list of cs
                // host which is not added in the app
                csHostMap.remove(host.getUuid());
            } else {
                hostService.softDelete(host);
            }

        }
        // 4. Get the remaining list of cs server hash user object, then iterate
        // and
        // add it to app db
        for (String key : csHostMap.keySet()) {
            hostService.save(csHostMap.get(key));
        }
    }

    /**
     * Sync with Cloud Server Volume.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    public void syncVolume() throws ApplicationException, Exception {

        // 1. Get all the volume objects from CS server as hash
        List<Volume> volumeList = volumeService.findAllFromCSServer();
        HashMap<String, Volume> csVolumeMap = (HashMap<String, Volume>) Volume.convert(volumeList);

        // 2. Get all the volume objects from application
        List<Volume> appVolumeServiceList = volumeService.findAll();

        // 3. Iterate application volume list
        for (Volume volume : appVolumeServiceList) {
            volume.setIsSyncFlag(false);
            // 3.1 Find the corresponding CS server volume object by finding it
            // in a hash using uuid
            if (csVolumeMap.containsKey(volume.getUuid())) {
                Volume csVolume = csVolumeMap.get(volume.getUuid());
                volume.setName(csVolume.getName());
                volume.setStorageOfferingId(csVolume.getStorageOfferingId());
                volume.setZoneId(csVolume.getZoneId());
                volume.setDomainId(csVolume.getDomainId());
                volume.setDepartmentId(csVolume.getDepartmentId());
                volume.setVmInstanceId(csVolume.getVmInstanceId());
                volume.setVolumeType(csVolume.getVolumeType());
                volume.setIsActive(true);
                if (volume.getDiskSize() != null) {
                    volume.setDiskSize(csVolume.getDiskSize());
                } else {
                    volume.setDiskSize(storageService.find(csVolume.getStorageOfferingId()).getDiskSize());
                }
                volume.setDiskSizeFlag(true);
                if (volume.getProjectId() != null) {
                    volume.setProjectId(csVolume.getProjectId());
                }
                volume.setChecksum(csVolume.getChecksum());
                volume.setStatus(csVolume.getStatus());
                volume.setDiskMaxIops(csVolume.getDiskMaxIops());
                volume.setDiskMinIops(csVolume.getDiskMinIops());
                volume.setCreatedDateTime(csVolume.getCreatedDateTime());
                volume.setUpdatedDateTime(csVolume.getUpdatedDateTime());
                // 3.2 If found, update the volume object in app db
                volumeService.update(volume);

                // 3.3 Remove once updated, so that we can have the list of cs
                // volume which is not added in the app
                csVolumeMap.remove(volume.getUuid());
            } else {
                volumeService.softDelete(volume);
            }
        }
        // 4. Get the remaining list of cs server hash volume object, then
        // iterate and
        // add it to app db
        for (String key : csVolumeMap.keySet()) {
            volumeService.save(csVolumeMap.get(key));
        }

        // Update instance disk size from volume
        List<Volume> listVolume = volumeService.findAll();
        for (int j = 0; j < listVolume.size(); j++) {
            if (listVolume.get(j).getVolumeType() == VolumeType.ROOT) {
                VmInstance vmInstance = virtualMachineService.find(listVolume.get(j).getVmInstanceId());
                vmInstance.setVolumeSize(listVolume.get(j).getDiskSize());
                virtualMachineService.update(vmInstance);
            }
        }
    }

    /**
     * Sync with Cloud Server Snapshot.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    private void syncSnapshot() throws ApplicationException, Exception {

        // 1. Get all the snapshot objects from CS server as hash
        List<Snapshot> csSnapshotService = snapshotService.findAllFromCSServer();
        HashMap<String, Snapshot> csSnapshotMap = (HashMap<String, Snapshot>) Snapshot.convert(csSnapshotService);

        // 2. Get all the snapshot objects from application
        List<Snapshot> appSnapshotList = snapshotService.findAll();

        // 3. Iterate application snapshot list
        for (Snapshot snapshot : appSnapshotList) {
            snapshot.setSyncFlag(false);
            // 3.1 Find the corresponding CS server snapshot object by finding
            // it in a hash using uuid
            if (csSnapshotMap.containsKey(snapshot.getUuid())) {
                Snapshot csUser = csSnapshotMap.get(snapshot.getUuid());

                snapshot.setName(csUser.getName());

                // 3.2 If found, update the snapshot object in app db
                snapshotService.update(snapshot);

                // 3.3 Remove once updated, so that we can have the list of cs
                // snapshot which is not added in the app
                csSnapshotMap.remove(snapshot.getUuid());
            } else {
                snapshotService.softDelete(snapshot);
            }

        }
        // 4. Get the remaining list of cs server hash user object, then iterate
        // and
        // add it to app db
        for (String key : csSnapshotMap.keySet()) {
            snapshotService.save(csSnapshotMap.get(key));
        }
    }

    /**
     * Sync with Cloud Server Pod.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    private void syncPod() throws ApplicationException, Exception {

        // 1. Get all the pod objects from CS server as hash
        List<Pod> csPodService = podService.findAllFromCSServer();
        HashMap<String, Pod> csPodMap = (HashMap<String, Pod>) Pod.convert(csPodService);

        // 2. Get all the pod objects from application
        List<Pod> appPodList = podService.findAll();

        // 3. Iterate application pod list
        for (Pod pod : appPodList) {
            // 3.1 Find the corresponding CS server host object by finding it in
            // a hash using uuid
            if (csPodMap.containsKey(pod.getUuid())) {
                Pod csUser = csPodMap.get(pod.getUuid());

                pod.setName(csUser.getName());
                pod.setGateway(csUser.getGateway());
                pod.setNetmask(csUser.getNetmask());
                // 3.2 If found, update the pod object in app db
                podService.update(pod);

                // 3.3 Remove once updated, so that we can have the list of cs
                // host which is not added in the app
                csPodMap.remove(pod.getUuid());
            } else {
                podService.softDelete(pod);
            }

        }
        // 4. Get the remaining list of cs server hash user object, then iterate
        // and
        // add it to app db
        for (String key : csPodMap.keySet()) {
            podService.save(csPodMap.get(key));
        }
    }

    /**
     * Sync with Cloud Server Cluster.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    private void syncCluster() throws ApplicationException, Exception {

        // 1. Get all the cluster objects from CS server as hash
        List<Cluster> csClusterService = clusterService.findAllFromCSServer();
        HashMap<String, Cluster> csClusterMap = (HashMap<String, Cluster>) Cluster.convert(csClusterService);

        // 2. Get all the cluster objects from application
        List<Cluster> appClusterList = clusterService.findAll();

        // 3. Iterate application cluster list
        for (Cluster cluster : appClusterList) {
            // 3.1 Find the corresponding CS server host object by finding it in
            // a hash using uuid
            if (csClusterMap.containsKey(cluster.getUuid())) {
                Cluster csCluster = csClusterMap.get(cluster.getUuid());

                cluster.setName(csCluster.getName());

                // 3.2 If found, update the cluster object in app db
                clusterService.update(cluster);

                // 3.3 Remove once updated, so that we can have the list of cs
                // cluster which is not added in the app
                csClusterMap.remove(cluster.getUuid());
            } else {
                clusterService.delete(cluster);
                // 3.2 If not found, delete it from app db
                // TODO clarify the business requirement, since it has impact in
                // the application if it is used
                // TODO clarify is this a soft or hard delete
            }
        }
        // 4. Get the remaining list of cs server hash user object, then iterate
        // and
        // add it to app db
        for (String key : csClusterMap.keySet()) {
            clusterService.save(csClusterMap.get(key));
        }
    }

    /**
     * Sync with Cloud Server Cluster.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    @Override
    public void syncSnapshotPolicy() throws ApplicationException, Exception {

        // 1. Get all the cluster objects from CS server as hash
        List<SnapshotPolicy> csClusterService = snapshotPolicyService.findAllFromCSServer();
        HashMap<String, SnapshotPolicy> csPolicyMap = (HashMap<String, SnapshotPolicy>) SnapshotPolicy.convert(csClusterService);

        // 2. Get all the cluster objects from application
        List<SnapshotPolicy> appPolicyList = snapshotPolicyService.findAll();

        // 3. Iterate application cluster list
        for (SnapshotPolicy snapPolicy : appPolicyList) {
            // 3.1 Find the corresponding CS server host object by finding it in
            // a hash using uuid
            if (csPolicyMap.containsKey(snapPolicy.getUuid())) {
                SnapshotPolicy csCluster = csPolicyMap.get(snapPolicy.getUuid());
                // 3.2 If found, update the cluster object in app db
                snapshotPolicyService.update(snapPolicy);

                // 3.3 Remove once updated, so that we can have the list of cs
                // cluster which is not added in the app
                csPolicyMap.remove(snapPolicy.getUuid());
            } else {
                snapshotPolicyService.delete(snapPolicy);
                // 3.2 If not found, delete it from app db
                // TODO clarify the business requirement, since it has impact in
                // the application if it is used
                // TODO clarify is this a soft or hard delete
            }
        }
        // 4. Get the remaining list of cs server hash user object, then iterate
        // and
        // add it to app db
        for (String key : csPolicyMap.keySet()) {
            snapshotPolicyService.save(csPolicyMap.get(key));
        }
    }


    /**
     * Sync with Cloud Server Instance snapshots.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    @Override
    public void syncVmSnapshots() throws Exception {
        // 1. Get all the vm snapshot objects from CS server as hash
        List<VmSnapshot> csSnapshotService = vmsnapshotService.findAllFromCSServer();
        HashMap<String, VmSnapshot> csSnapshotMap = (HashMap<String, VmSnapshot>) VmSnapshot.convert(csSnapshotService);

        // 2. Get all the vm snapshot objects from application
        List<VmSnapshot> appSnapshotList = vmsnapshotService.findAll();

        // 3. Iterate application vm snapshot list
        for (VmSnapshot snapshot : appSnapshotList) {
            // 3.1 Find the corresponding CS server snapshot object by finding
            // it in a hash using uuid
            if (csSnapshotMap.containsKey(snapshot.getUuid())) {
                VmSnapshot snaps = csSnapshotMap.get(snapshot.getUuid());

                snapshot.setName(snaps.getName());

                // 3.2 If found, update the vm snapshot object in app db
                vmsnapshotService.update(snapshot);

                // 3.3 Remove once updated, so that we can have the list of cs
                // vm snapshot which is not added in the app
                csSnapshotMap.remove(snapshot.getUuid());
            } else {
                vmsnapshotService.delete(snapshot);
                // 3.2 If not found, delete it from app db
                // TODO clarify the business requirement, since it has impact in
                // the application if it is used
                // TODO clarify is this a soft or hard delete
            }

        }
        // 4. Get the remaining list of cs server hash user object, then iterate
        // and
        // add it to app db
        for (String key : csSnapshotMap.keySet()) {
            vmsnapshotService.save(csSnapshotMap.get(key));
        }
    }

    /**
     * Sync status of Vm resource with Cloud Server.
     *
     * @param object response object.
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    @Override
    public void syncResourceStatus(String object) throws Exception {
        CloudStackConfiguration cloudConfig = cloudConfigService.find(1L);
        server.setServer(cloudConfig.getApiURL(), cloudConfig.getSecretKey(), cloudConfig.getApiKey());
        cloudStackInstanceService.setServer(server);
        String instances = cloudStackInstanceService.queryAsyncJobResult(object, "json");
        JSONObject jobresult = new JSONObject(instances).getJSONObject("queryasyncjobresultresponse")
                .getJSONObject("jobresult");
        if (jobresult.has("virtualmachine")) {
            VmInstance vmInstance = VmInstance.convert(jobresult.getJSONObject("virtualmachine"));
            VmInstance instance = virtualMachineService.findByUUID(vmInstance.getUuid());
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
                    LOGGER.debug("sync VM for pass" + encryptedPassword);
                    instance.setVncPassword(encryptedPassword);
                }
                // 3.2 If found, update the vm object in app db
                virtualMachineService.update(instance);
                syncVolume();
            }
        }
    }

    @Override
    public void syncResourceLimit() throws ApplicationException, Exception {
        List<Domain> domains = domainService.findAllDomain();
        for (Domain domain : domains) {
            syncResourceLimitDomain(domain);
        }

        List<Project> projects = projectService.findAllByActive(true);
        for (Project project : projects) {
            syncResourceLimitProject(project);
        }
    }

    @Override
    public void syncResourceLimitDomain(Domain domain) throws ApplicationException, Exception {
        // 1. Get all the ResourceLimit objects from CS server as hash
        List<ResourceLimitDomain> csResourceList = resourceDomainService.findAllFromCSServerDomain(domain.getUuid());
        HashMap<String, ResourceLimitDomain> csResourceMap = (HashMap<String, ResourceLimitDomain>) ResourceLimitDomain
                .convert(csResourceList);

        // 2. Get all the resource objects from application
        List<ResourceLimitDomain> appResourceList = resourceDomainService.findAllByDomainIdAndIsActive(domain.getId(),
                true);

        // 3. Iterate Domain resource list
        LOGGER.debug("Total rows updated : " + (appResourceList.size()));
        for (ResourceLimitDomain resource : appResourceList) {
            LOGGER.debug("NEW DOMAIN ID " + resource.getDomainId());
            if (csResourceMap.containsKey(resource.getDomainId() + resource.getResourceType().toString())) {
                if (resource != null) {
                    resource.setMax(
                            csResourceMap.get(resource.getDomainId() + resource.getResourceType().toString()).getMax());
                    resource.setIsActive(true);
                    resource.setIsSyncFlag(false);
                    resourceDomainService.update(resource);
                }
                csResourceMap.remove(resource.getDomainId() + resource.getResourceType().toString());
            } else {
                resource.setIsActive(false);
                resource.setIsSyncFlag(false);
                resourceDomainService.update(resource);
            }
        }
        // 4. Get the remaining list of cs server hash resource object, then
        // iterate and
        // add it to app db
        for (String key : csResourceMap.keySet()) {
            resourceDomainService.save(csResourceMap.get(key));
        }
        LOGGER.debug("Total rows added", (csResourceMap.size()));
        // Used for setting optional values for resource count.
        HashMap<String, String> domainCountMap = new HashMap<String, String>();
        // Sync for resource count in domain
        String csResponse = cloudStackResourceCapacity.updateResourceCount(domain.getUuid(), domainCountMap, "json");
        convertEntityService.resourceCount(csResponse);
    }

    @Override
    public void syncResourceLimitProject(Project project) throws ApplicationException, Exception {

        // 1. Get all the ResourceLimit objects from CS server as hash
        List<ResourceLimitProject> csResourceList = resourceProjectService.findAllFromCSServerProject(project.getUuid());
               HashMap<String, ResourceLimitProject> csResourceMap = (HashMap<String, ResourceLimitProject>) ResourceLimitProject
                .convert(csResourceList);

        // 2. Get all the resource objects from application
        List<ResourceLimitProject> appResourceList = resourceProjectService.findAllByProjectIdAndIsActive(project.getId(), true);

        // 3. Iterate application resource list
        LOGGER.debug("Total rows updated : " + (appResourceList.size()));
        for (ResourceLimitProject resource : appResourceList) {

            LOGGER.debug("NEW PROJECT ID " + resource.getProjectId());
            if (csResourceMap.containsKey(resource.getProjectId() + resource.getResourceType().toString())) {
                if (resource != null) {
                    ResourceLimitProject resourceData = csResourceMap.get(resource.getProjectId() + resource.getResourceType().toString());
                    resource.setMax(resourceData.getMax());
                    resource.setDepartmentId(resourceData.getDepartmentId());
                    resource.setIsActive(true);
                    resource.setIsSyncFlag(false);
                    resourceProjectService.update(resource);
                }
                csResourceMap.remove(resource.getProjectId() + resource.getResourceType().toString());
            } else {
                resource.setIsActive(false);
                resource.setIsSyncFlag(false);
                resourceProjectService.update(resource);
            }
        }
        // 4. Get the remaining list of cs server hash resource object, then
        // iterate and
        // add it to app db
        for (String key : csResourceMap.keySet()) {
            LOGGER.debug("Syncservice resource Project id:");
            resourceProjectService.save(csResourceMap.get(key));

        }
        LOGGER.debug("Total rows added", (csResourceMap.size()));
        // Used for setting optional values for resource count.
        HashMap<String, String> projectCountMap = new HashMap<String, String>();
        projectCountMap.put("projectid", project.getUuid());
        //Sync for resource count in domain
        String csResponse = cloudStackResourceCapacity.updateResourceCount(
                convertEntityService.getDomainById(project.getDomainId()).getUuid(), projectCountMap, "json");
        convertEntityService.resourceCount(csResponse);
    }

    /**
     * Sync with Cloud Server Iso.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    private void syncIso() throws ApplicationException, Exception {

        // 1. Get all the iso objects from CS server as hash
        List<Iso> csIsoService = isoService.findAllFromCSServer();
        HashMap<String, Iso> csIsoMap = (HashMap<String, Iso>) Iso.convert(csIsoService);

        // 2. Get all the iso objects from application
        List<Iso> appPodList = isoService.findAll();

        // 3. Iterate application iso list
        for (Iso iso : appPodList) {
            // 3.1 Find the corresponding CS server iso object by finding it in
            // a hash using uuid
            if (csIsoMap.containsKey(iso.getUuid())) {
                Iso csIso = csIsoMap.get(iso.getUuid());

                iso.setName(csIso.getName());

                // 3.2 If found, update the iso object in app db
                isoService.update(iso);

                // 3.3 Remove once updated, so that we can have the list of cs
                // host which is not added in the app
                csIsoMap.remove(iso.getUuid());
            } else {
                isoService.softDelete(iso);
            }

        }
        // 4. Get the remaining list of cs server hash iso object, then iterate
        // and
        // add it to app db
        for (String key : csIsoMap.keySet()) {
            isoService.save(csIsoMap.get(key));
        }
    }

    @Override
    public void syncProject() throws ApplicationException, Exception {

        // 1. Get all the networkOffering objects from CS server as hash
        List<Project> csProjectList = projectService.findAllFromCSServerByDomain();
        HashMap<String, Project> csProjectMap = (HashMap<String, Project>) Project.convert(csProjectList);

        // 2. Get all the networkOffering objects from application
        List<Project> appProjectList = projectService.findAll();

        // 3. Iterate application networkOffering list
        for (Project project : appProjectList) {
            project.setSyncFlag(false);
            LOGGER.debug("Total rows updated : " + (appProjectList.size()));
            // 3.1 Find the corresponding CS server projectService object by
            // finding it in a hash using uuid
            if (csProjectMap.containsKey(project.getUuid())) {
                Project csProject = csProjectMap.get(project.getUuid());
                project.setName(csProject.getName());
                // check existing department.
                if (csProject.getDepartmentId() != project.getDepartmentId()) {
                    project.setDepartmentId(csProject.getDepartmentId());
                    // if department updated for project reset project owner.
                    project.setProjectOwnerId(null);
                    project.setProjectOwner(null);
                    project.setUserList(null);
                }
                project.setStatus(csProject.getStatus());
                project.setDescription(csProject.getDescription());
                project.setDomainId(csProject.getDomainId());
                // 3.2 If found, update the project object in app db
                projectService.update(project);
                // 3.3 Remove once updated, so that we can have the list of cs
                // project which is not added in the app
                csProjectMap.remove(project.getUuid());
            } else {
                projectService.softDelete(project);
            }
        }
        // 4. Get the remaining list of cs server hash NetworkOffering object,
        // then iterate and
        // add it to app db
        for (String key : csProjectMap.keySet()) {
            LOGGER.debug("Syncservice Project uuid:");
            projectService.save(csProjectMap.get(key));
        }
    }

    @Override
    public void syncSSHKey() throws ApplicationException, Exception {

        // 1. Get all the region objects from CS server as hash
        List<SSHKey> csSSHKeyList = sshKeyService.findAllFromCSServer();
        HashMap<String, SSHKey> csSSHkeyMap = (HashMap<String, SSHKey>) SSHKey.convert(csSSHKeyList);

        // 2. Get all the region objects from application
        List<SSHKey> appSSHKeyList = sshKeyService.findAllBySync();

        // 3. Iterate application region list
        for (SSHKey sshKey : appSSHKeyList) {
            sshKey.setIsSyncFlag(false);
            LOGGER.debug("Total rows updated : " + (appSSHKeyList.size()));
            // 3.1 Find the corresponding CS server region object by finding it
            // in a hash using uuid
            if (csSSHkeyMap.containsKey(sshKey.getFingerPrint())) {
                SSHKey csSSHKey = csSSHkeyMap.get(sshKey.getFingerPrint());

                sshKey.setName(csSSHKey.getName());
                // 3.2 If found, update the region object in app db
                sshKeyService.update(sshKey);

                // 3.3 Remove once updated, so that we can have the list of cs
                // region which is not added in the app
                csSSHkeyMap.remove(sshKey.getFingerPrint());
            } else {
                sshKeyService.delete(sshKey);
            }
        }
        // 4. Get the remaining list of cs server hash region object, then
        // iterate and
        // add it to app db
        for (String key : csSSHkeyMap.keySet()) {
            LOGGER.debug("Syncservice region name:");
            sshKeyService.save(csSSHkeyMap.get(key));
        }
        LOGGER.debug("Total rows added : " + (csSSHkeyMap.size()));

    }

    @Override
    public void syncNic() throws ApplicationException, Exception {

        // 1. Get all the nic objects from CS server as hash
        List<Nic> csNicList = nicService.findAllFromCSServer();
        HashMap<String, Nic> csNicMap = (HashMap<String, Nic>) Nic.convert(csNicList);

        // 2. Get all the nic objects from application
        List<Nic> appnicList = nicService.findAll();

        // 3. Iterate application nic list
        for (Nic nic : appnicList) {
            nic.setSyncFlag(false);
            LOGGER.debug("Total rows updated : " + (appnicList.size()));
            // 3.1 Find the corresponding CS server ntService object by
            // finding it in a hash using uuid
            if (csNicMap.containsKey(nic.getUuid())) {
                Nic csNic = csNicMap.get(nic.getUuid());
                nic.setUuid(csNic.getUuid());
                nic.setIpAddress(csNic.getIpAddress());
                nic.setVmIpAddress(csNic.getVmIpAddress());

                // 3.2 If found, update the nic object in app db
                nicService.update(nic);

                // 3.3 Remove once updated, so that we can have the list of cs
                // nic which is not added in the app
                csNicMap.remove(nic.getUuid());
            }else {
                nicService.softDelete(nic);
            }
        }
        // 4. Get the remaining list of cs server hash NetworkOffering object,
        // then iterate and
        // add it to app db
        for (String key : csNicMap.keySet()) {
            LOGGER.debug("Syncservice Nic uuid:");
            nicService.save(csNicMap.get(key));
        }
    }

    @Override
    public void syncEgressFirewallRules() throws ApplicationException, Exception {

        // 1. Get all the egressFirewallRules objects from CS server as hash
        List<FirewallRules> csEgressList = egressService.findAllFromCSServer();
        HashMap<String, FirewallRules> csEgressMap = (HashMap<String, FirewallRules>) FirewallRules.convert(csEgressList);

        // 2. Get all the egressFirewallRules objects from application
        List<FirewallRules> appEgressList = egressService.findAllByTrafficType(TrafficType.EGRESS);

        // 3. Iterate application egressFirewallRules list
        for (FirewallRules egress : appEgressList) {
            egress.setSyncFlag(false);
            LOGGER.debug("Total rows updated : " + (appEgressList.size()));
            // 3.1 Find the corresponding CS server egressFirewallRulesService object by
            // finding it in a hash using uuid
            if (csEgressMap.containsKey(egress.getUuid())) {
                FirewallRules csFirewall = csEgressMap.get(egress.getUuid());
                egress.setUuid(csFirewall.getUuid());
                egress.setProtocol(csFirewall.getProtocol());
                egress.setDisplay(csFirewall.getDisplay());
                egress.setSourceCIDR(csFirewall.getSourceCIDR());
                egress.setNetworkId(csFirewall.getNetworkId());
                egress.setState(csFirewall.getState());
                egress.setStartPort(csFirewall.getStartPort());
                egress.setEndPort(csFirewall.getEndPort());
                egress.setIcmpCode(csFirewall.getIcmpCode());
                egress.setIcmpMessage(csFirewall.getIcmpMessage());
                egress.setPurpose(csFirewall.getPurpose());
                egress.setTrafficType(csFirewall.getTrafficType());
                egress.setDepartmentId(csFirewall.getDepartmentId());
                egress.setProjectId(csFirewall.getProjectId());
                egress.setDomainId(csFirewall.getProjectId());
                egress.setIsActive(csFirewall.getIsActive());
                // 3.2 If found, update the egressFirewallRules object in app db
                egressService.update(egress);

                // 3.3 Remove once updated, so that we can have the list of cs
                // egressFirewallRules which is not added in the app
                csEgressMap.remove(egress.getUuid());
            } else {
                egressService.softDelete(egress);
            }
        }
        // 4. Get the remaining list of cs server hash NetworkOffering object,
        // then iterate and
        // add it to app db
        for (String key : csEgressMap.keySet()) {
            LOGGER.debug("Syncservice Egress uuid:");
            egressService.save(csEgressMap.get(key));
        }
    }

    @Override
    public void syncIngressFirewallRules() throws ApplicationException, Exception {

        // 1. Get all the egressFirewallRules objects from CS server as hash
        List<FirewallRules> csIngressList = egressService.findAllFromCSServerForIngress();
        HashMap<String, FirewallRules> csIngressMap = (HashMap<String, FirewallRules>) FirewallRules.convert(csIngressList);

        // 2. Get all the egressFirewallRules objects from application
        List<FirewallRules> appIngressList =  egressService.findAllByTrafficType(TrafficType.INGRESS);

        // 3. Iterate application egressFirewallRules list
        for (FirewallRules ingress : appIngressList) {
            ingress.setSyncFlag(false);
            if (csIngressMap.containsKey(ingress.getUuid())) {
                FirewallRules csFirewall = csIngressMap.get(ingress.getUuid());
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
                csIngressMap.remove(ingress.getUuid());
            } else {
                egressService.softDelete(ingress);
            }
        }
        // 4. Get the remaining list of cs server hash NetworkOffering object,
        // then iterate and
        // add it to app db
        for (String key : csIngressMap.keySet()) {
            LOGGER.debug("Syncservice Ingress uuid:");
            egressService.save(csIngressMap.get(key));
        }
    }

    @Override
    public void syncIpAddress() throws ApplicationException, Exception {

        // 1. Get all the nic objects from CS server as hash
        List<IpAddress> csIpList = ipAddressService.findAllFromCSServer();
        HashMap<String, IpAddress> csIpMap = (HashMap<String, IpAddress>) IpAddress.convert(csIpList);

        // 2. Get all the nic objects from application
        List<IpAddress> appIpList = ipAddressService.findAll();

        // 3. Iterate application nic list
        for (IpAddress ipAddress : appIpList) {
            ipAddress.setSyncFlag(false);
            LOGGER.debug("Total rows updated : " + (appIpList.size()));
            // 3.1 Find the corresponding CS server ntService object by
            // finding it in a hash using uuid
            if (csIpMap.containsKey(ipAddress.getUuid())) {
                IpAddress csIp = csIpMap.get(ipAddress.getUuid());

                ipAddress.setUuid(csIp.getUuid());
                ipAddress.setPublicIpAddress(csIp.getPublicIpAddress());
                ipAddress.setState(csIp.getState());
                ipAddress.setIsSourcenat(csIp.getIsSourcenat());
                ipAddress.setIsStaticnat(csIp.getIsStaticnat());
                ipAddress.setNetworkId(csIp.getNetworkId());
                ipAddress.setVpnUuid(csIp.getVpnUuid());
                ipAddress.setVpnPresharedKey(csIp.getVpnPresharedKey());
                ipAddress.setVpnState(csIp.getVpnState());
                ipAddress.setVpnForDisplay(csIp.getVpnForDisplay());

                // 3.2 If found, update the nic object in app db
                ipAddressService.update(ipAddress);

                // 3.3 Remove once updated, so that we can have the list of cs
                // nic which is not added in the app
                csIpMap.remove(ipAddress.getUuid());
            } else {
                ipAddressService.softDelete(ipAddress);
            }
        }
        // 4. Get the remaining list of cs server hash NetworkOffering object,
        // then iterate and
        // add it to app db
        for (String key : csIpMap.keySet()) {
            LOGGER.debug("Syncservice IP Address uuid:");
            ipAddressService.save(csIpMap.get(key));
        }
    }

    @Override
    public void syncPortForwarding() throws ApplicationException, Exception {

        // 1. Get all the PortForwarding objects from CS server as hash
        List<PortForwarding> csPortForwardingList = portForwardingService.findAllFromCSServer();
        HashMap<String, PortForwarding> csPortForwardingMap = (HashMap<String, PortForwarding>) PortForwarding.convert(csPortForwardingList);

        // 2. Get all the PortForwarding objects from application
        List<PortForwarding> appPortForwardingList = portForwardingService.findAll();

        // 3. Iterate application PortForwarding list
        for (PortForwarding portForwarding : appPortForwardingList) {
            portForwarding.setSyncFlag(false);
            LOGGER.debug("Total rows updated : " + (appPortForwardingList.size()));
            // 3.1 Find the corresponding CS server ntService object by
            // finding it in a hash using uuid
            if (csPortForwardingMap.containsKey(portForwarding.getUuid())) {
                PortForwarding csPortForwarding = csPortForwardingMap.get(portForwarding.getUuid());

                portForwarding.setUuid(csPortForwarding.getUuid());

                // 3.2 If found, update the PortForwarding object in app db
                portForwardingService.update(portForwarding);

                // 3.3 Remove once updated, so that we can have the list of cs
                // nic which is not added in the app
                csPortForwardingMap.remove(portForwarding.getUuid());
            } else {
                portForwardingService.softDelete(portForwarding);

                //Delete the load balancer firewall rule
                FirewallRules firewallRules = egressRuleService.findByUUID(portForwarding.getUuid());
                if (firewallRules != null) {
                    firewallRules.setSyncFlag(false);
                    egressRuleService.deleteFirewallIngressRule(firewallRules);
                }
            }
        }
        // 4. Get the remaining list of cs server hash NetworkOffering object,
        // then iterate and
        // add it to app db
        for (String key : csPortForwardingMap.keySet()) {
            LOGGER.debug("Syncservice Port Forwarding uuid:");
            portForwardingService.save(csPortForwardingMap.get(key));
            portForwardingFirewallRules(csPortForwardingMap.get(key));
        }
    }

    /**
     * Sync with Cloud Server Network Firewall Rules.
     *
     * @param portForwarding job result
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    public void portForwardingFirewallRules(PortForwarding portForwarding) throws ApplicationException, Exception {
        FirewallRules portForward = egressRuleService.findByUUID(portForwarding.getUuid());
        if (portForward == null) {
            FirewallRules csFirewallRule = new FirewallRules();
            csFirewallRule.setSyncFlag(false);
            csFirewallRule.setUuid(portForwarding.getUuid());
            csFirewallRule.setDisplay(portForwarding.getFordisplay());
            csFirewallRule.setStartPort(portForwarding.getPrivateStartPort());
            csFirewallRule.setEndPort(portForwarding.getPrivateEndPort());
            csFirewallRule.setProtocol(Protocol.valueOf(portForwarding.getProtocolType().name()));
            csFirewallRule.setIsActive(true);
            csFirewallRule.setIpAddressId(portForwarding.getIpAddressId());
            csFirewallRule.setPurpose(FirewallRules.Purpose.PORTFORWARDING);
            csFirewallRule.setNetworkId(convertEntityService.getNetworkByUuid(portForwarding.getTransNetworkId()));
            csFirewallRule.setDepartmentId(convertEntityService.getNetworkById(csFirewallRule.getNetworkId())
                    .getDepartmentId());
            csFirewallRule.setProjectId(convertEntityService.getNetworkById(csFirewallRule.getNetworkId())
                    .getProjectId());
            csFirewallRule.setDomainId(convertEntityService.getNetworkById(csFirewallRule.getNetworkId())
                    .getDomainId());
            egressRuleService.save(csFirewallRule);
        }
    }

    @Override
    public void syncLoadBalancer() throws ApplicationException, Exception {

        // 1. Get all the LoadBalancer objects from CS server as hash
        List<LoadBalancerRule> csLoadBalancerList = loadBalancerService.findAllFromCSServer();

        HashMap<String, LoadBalancerRule> csLoadBalancerMap = (HashMap<String, LoadBalancerRule>) LoadBalancerRule.convert(csLoadBalancerList);

        // 2. Get all the LoadBalancer objects from application
        List<LoadBalancerRule> appLoadBalancerList = loadBalancerService.findAll();

        // 3. Iterate application LoadBalancer list
        for (LoadBalancerRule loadBalancer : appLoadBalancerList) {
            loadBalancer.setSyncFlag(false);
            LOGGER.debug("Total rows updated : " + (appLoadBalancerList.size()));
            // 3.1 Find the corresponding CS server ntService object by
            // finding it in a hash using uuid
            if (csLoadBalancerMap.containsKey(loadBalancer.getUuid())) {
                LoadBalancerRule csLoadBalancer = csLoadBalancerMap.get(loadBalancer.getUuid());

                loadBalancer.setUuid(csLoadBalancer.getUuid());
                loadBalancer.setName(csLoadBalancer.getName());
                loadBalancer.setAlgorithm(csLoadBalancer.getAlgorithm());

                // 3.2 If found, update the LoadBalancer object in app db
                loadBalancerService.update(loadBalancer);

                // 3.3 Remove once updated, so that we can have the list of cs
                // nic which is not added in the app
                csLoadBalancerMap.remove(loadBalancer.getUuid());
            } else {
                loadBalancerService.softDelete(loadBalancer);

                //Delete the load balancer firewall rule
                FirewallRules firewallRules = egressRuleService.findByUUID(loadBalancer.getUuid());
                if (firewallRules != null) {
                    firewallRules.setSyncFlag(false);
                    egressRuleService.deleteFirewallIngressRule(firewallRules);
                }
            }
        }
        // 4. Get the remaining list of cs server hash NetworkOffering object,
        // then iterate and
        // add it to app db
        for (String key : csLoadBalancerMap.keySet()) {
            LOGGER.debug("Syncservice Load Balancer uuid:");
            loadBalancerService.save(csLoadBalancerMap.get(key));
            loadBalancerFirewallRules(csLoadBalancerMap.get(key));
        }
    }

   public void syncLoadBalancerStickyPolicy() throws ApplicationException, Exception {

        // 1. Get all the LoadBalancer objects from CS server as hash
        List<LbStickinessPolicy> csLoadBalancerList = lbPolicyService.findAllFromCSServer();
        HashMap<String, LbStickinessPolicy> csLoadBalancerMap = (HashMap<String, LbStickinessPolicy>) LbStickinessPolicy.convert(csLoadBalancerList);
        List<LbStickinessPolicy> appLoadBalancerList = lbPolicyService.findAll();
        // 3. Iterate application LoadBalancer list
        for (LbStickinessPolicy loadBalancer : appLoadBalancerList) {
            loadBalancer.setSyncFlag(false);
            LOGGER.debug("Total rows updated : " + (csLoadBalancerList.size()));
            // 3.1 Find the corresponding CS server ntService object by
            // finding it in a hash using uuid
            if (csLoadBalancerMap.containsKey(loadBalancer.getUuid())) {
                LbStickinessPolicy csLoadBalancer = csLoadBalancerMap.get(loadBalancer.getUuid());

            loadBalancer.setUuid(csLoadBalancer.getUuid());
            //loadBalancer.setStickinessName(csLoadBalancer.getStickinessName());
           // loadBalancer.setStickinessMethod(csLoadBalancer.getStickinessMethod());
                // 3.2 If found, update the LoadBalancer object in app db
            lbPolicyService.update(loadBalancer);

                // 3.3 Remove once updated, so that we can have the list of cs
                // nic which is not added in the app
            }else {
                lbPolicyService.softDelete(loadBalancer);
            }
        }
        // 4. Get the remaining list of cs server hash NetworkOffering object,
        // then iterate and
        // add it to app db
        for (String key : csLoadBalancerMap.keySet()) {
            LOGGER.debug("Syncservice Load Balancer uuid:");
            lbPolicyService.save(csLoadBalancerMap.get(key));
        }
   }

    /**
     * Sync with Cloud Server Network Firewall Rules.
     *
     * @param loadBalancerRule job result
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    public void loadBalancerFirewallRules(LoadBalancerRule loadBalancerRule) throws ApplicationException, Exception {
        FirewallRules loadBalancer = egressRuleService.findByUUID(loadBalancerRule.getUuid());
        if (loadBalancer == null) {
            FirewallRules csFirewallRule = new FirewallRules();
            csFirewallRule.setSyncFlag(false);
            csFirewallRule.setUuid(loadBalancerRule.getUuid());
            csFirewallRule.setDisplay(loadBalancerRule.getDisplay());
            csFirewallRule.setSourceCIDR(loadBalancerRule.getSourceCIDR());
            csFirewallRule.setStartPort(loadBalancerRule.getPrivatePort());
            csFirewallRule.setEndPort(loadBalancerRule.getPublicPort());
            csFirewallRule.setIsActive(true);
            csFirewallRule.setIpAddressId(loadBalancerRule.getIpAddressId());
            csFirewallRule.setPurpose(FirewallRules.Purpose.LOADBALANCING);
            csFirewallRule.setNetworkId(convertEntityService.getNetworkByUuid(loadBalancerRule.getTransNetworkId()));
            csFirewallRule.setDepartmentId(convertEntityService.getNetworkById(csFirewallRule.getNetworkId())
                    .getDepartmentId());
            csFirewallRule.setProjectId(convertEntityService.getNetworkById(csFirewallRule.getNetworkId())
                    .getProjectId());
            csFirewallRule.setDomainId(convertEntityService.getNetworkById(csFirewallRule.getNetworkId())
                    .getDomainId());
            egressRuleService.save(csFirewallRule);
        }
    }

    /**
     * Create default roles and permissions.
     *
     * @param departmnetList department list by Root and domain admin
     * @param existPermissionList list existing permission list
     */
    void createRole(List<Department> departmnetList, List<Permission> existPermissionList) {
        try {
            if (existPermissionList.size() == 0) {
                List<Permission> newPermissionList = PermissionUtil.createPermissions(instance, storage, network,
                        sshkey, quatoLimit, vpc, template, additionalServive, project, application, department, roles,
                        user, report);
                for (Permission permission : newPermissionList) {
                    permissionService.save(permission);
                }
                for (Department department : departmnetList) {
                    Role role = roleService.findByNameAndDepartmentIdAndIsActive("FULL_PERMISSION", department.getId(), true);
                    if (role == null) {
                        Role newRole = new Role();
                        newRole.setName("FULL_PERMISSION");
                        newRole.setDepartmentId(department.getId());
                        newRole.setDescription("Allow full permission");
                        newRole.setStatus(Role.Status.ENABLED);
                        newRole.setPermissionList(newPermissionList);
                        newRole.setSyncFlag(false);
                        roleService.save(newRole);
                    } else if (role != null) {
                        role.setName("FULL_PERMISSION");
                        role.setDepartmentId(department.getId());
                        role.setDescription("Allow full permission");
                        role.setStatus(Role.Status.ENABLED);
                        role.setPermissionList(permissionService.findAll());
                        role.setSyncFlag(false);
                        roleService.update(role);
                    }
                }
            } else {
                for (Department department : departmnetList) {
                    Role role = roleService.findByNameAndDepartmentIdAndIsActive("FULL_PERMISSION", department.getId(), true);
                    if (role != null) {
                        role.setName("FULL_PERMISSION");
                        role.setDepartmentId(department.getId());
                        role.setDescription("Allow full permission");
                        role.setStatus(Role.Status.ENABLED);
                        role.setPermissionList(permissionService.findAll());
                        role.setSyncFlag(false);
                        roleService.update(role);
                    } else if (role == null) {
                        Role newRole = new Role();
                        newRole.setName("FULL_PERMISSION");
                        newRole.setDepartmentId(department.getId());
                        newRole.setDescription("Allow full permission");
                        newRole.setStatus(Role.Status.ENABLED);
                        newRole.setPermissionList(permissionService.findAll());
                        newRole.setSyncFlag(false);
                        roleService.save(newRole);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.debug("createRole" + e);
        }
    }

    @Override
    public void syncUpdateUserRole() throws ApplicationException, Exception {
        List<UserType> types = new ArrayList<UserType>();
        types.add(UserType.ROOT_ADMIN);
        types.add(UserType.DOMAIN_ADMIN);
        try {
            List<User> userList = userService.findUsersByTypesAndActive(types, true);
            for (User user : userList) {
                Role role = roleService.findByNameAndDepartmentIdAndIsActive(ADMIN_PERMISSION, user.getDepartmentId(),
                        true);
                user.setRoleId(role.getId());

                user.setSyncFlag(false);
                user.setSyncFlag(false);
                userService.update(user);
            }
        } catch (Exception e) {
            LOGGER.debug("syncUpdateUserRole" + e);
        }
    }

    @Override
    public void syncVpnUser() throws ApplicationException, Exception {

        // 1. Get all the VPN user objects from CS server as hash
        List<VpnUser> csVpnList = vpnUserService.findAllFromCSServer();
        HashMap<String, VpnUser> csVpnMap = (HashMap<String, VpnUser>) VpnUser.convert(csVpnList);

        // 2. Get all the VPN user objects from application
        List<VpnUser> appVpnList = vpnUserService.findAll();

        // 3. Iterate application VPN user list
        for (VpnUser vpnUser : appVpnList) {
            vpnUser.setSyncFlag(false);
            LOGGER.debug("Total rows updated : " + (appVpnList.size()));
            // 3.1 Find the corresponding CS server with Service object by
            // finding it in a hash using uuid
            if (csVpnMap.containsKey(vpnUser.getUuid())) {
                VpnUser csVpn = csVpnMap.get(vpnUser.getUuid());

                vpnUser.setUuid(csVpn.getUuid());
                vpnUser.setUserName(csVpn.getUserName());

                // 3.2 If found, update the VPN user object in app db
                vpnUserService.update(vpnUser);

                // 3.3 Remove once updated, so that we can have the list of cs
                // VPN user which is not added in the app
                csVpnMap.remove(vpnUser.getUuid());
            } else {
                vpnUserService.softDelete(vpnUser);
            }
        }
        // 4. Get the remaining list of cs server hash NetworkOffering object,
        // then iterate and
        // add it to app db
        for (String key : csVpnMap.keySet()) {
            LOGGER.debug("Sync service VPN user uuid:");
            vpnUserService.save(csVpnMap.get(key));
        }
    }

    @Override
    public void syncResourceLimitActionEvent(ResponseEvent eventObject) throws ApplicationException, Exception {
        Domain domain = domainService.findbyUUID(eventObject.getEntityuuid());
        syncResourceLimitDomain(domain);
    }
}
