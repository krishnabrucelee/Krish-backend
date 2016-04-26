package ck.panda.service;

import java.util.List;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.constants.PingConstants;
import ck.panda.domain.entity.CloudStackConfiguration;
import ck.panda.domain.entity.ComputeOffering;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.ManualCloudSync;
import ck.panda.domain.entity.Network;
import ck.panda.domain.entity.NetworkOffering;
import ck.panda.domain.entity.Project;
import ck.panda.domain.entity.StorageOffering;
import ck.panda.domain.entity.Template;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.Zone;
import ck.panda.domain.repository.jpa.CloudStackConfigurationRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackServer;
import ck.panda.util.ConfigUtil;
import ck.panda.util.PingService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;
import ck.panda.util.error.exception.EntityNotFoundException;

/**
 * CloudStackConfiguration service implementation.
 */
@Service
public class CloudStackConfigurationServiceImpl implements CloudStackConfigurationService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CloudStackConfigurationServiceImpl.class);

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** CloudStack connector. */
    @Autowired
    private CloudStackServer server;

    /** CloudStackConfiguration Repository . */
    @Autowired
    private CloudStackConfigurationRepository configRepo;

    /** synchronization with cloudstack. */
    @Autowired
    private SyncService syncService;

    /** Mr.ping service reference. */
    @Autowired
    private PingService pingService;

    /** Cloud stack configuration reference. */
    @Autowired
    private ConfigUtil configUtil;

    /** Domain service for listing domains. */
    @Autowired
    private DomainService domainService;

    /** Region service for listing regions. */
    @Autowired
    private ZoneService zoneService;

    /** Department service for listing departments. */
    @Autowired
    private DepartmentService departmentService;

    /** User service for listing users. */
    @Autowired
    private UserService userService;

    /** Project service for listing projects. */
    @Autowired
    private ProjectService projectService;

    /** Compute offering Service for listing compute offers. */
    @Autowired
    private ComputeOfferingService computeService;

    /** Storage offering service for listing storage offers. */
    @Autowired
    private StorageOfferingService storageService;

    /** Network offering service for listing network offers. */
    @Autowired
    private NetworkOfferingService networkOfferingService;

    /** Template Service for listing templates. */
    @Autowired
    private TemplateService templateService;

    /** Network service for listing networks. */
    @Autowired
    private NetworkService networkService;

    /** Manual cloud sync service reference. */
    @Autowired
    private ManualCloudSyncService manualCloudSyncService;

    /** Manual sync domain key. */
    public static final String DOMAIN = "DOMAIN";

    /** Manual sync zone key. */
    public static final String ZONE = "ZONE";

    /** Manual sync department key. */
    public static final String DEPARTMENT = "DEPARTMENT";

    /** Manual sync user key. */
    public static final String USER = "USER";

    /** Manual sync project key. */
    public static final String PROJECT = "PROJECT";

    /** Manual sync compute offer key. */
    public static final String COMPUTE_OFFER = "COMPUTE_OFFER";

    /** Manual sync disk offer key. */
    public static final String DISK_OFFER = "DISK_OFFER";

    /** Manual sync network offer key. */
    public static final String NETWORK_OFFER = "NETWORK_OFFER";

    /** Manual sync template key. */
    public static final String TEMPLATE = "TEMPLATE";

    /** Manual sync vpc offer key. */
    public static final String VPC_OFFER = "VPC_OFFER";

    /** Manual sync network key. */
    public static final String NETWORK = "NETWORK";

    /** Manual sync vpc key. */
    public static final String VPC = "VPC";

    /** Manual sync type. */
    public static final String IMPORT = "import", CHECK = "check", CHECKALL = "checkall";

    @Override
    public CloudStackConfiguration save(CloudStackConfiguration config) throws Exception {

        Errors errors = validator.rejectIfNullEntity("config", config);
        errors = validator.validateEntity(config, errors);

        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        }
        server.setServer(config.getApiURL(), config.getSecretKey(), config.getApiKey());
        syncService.syncRegion();
        pingConfigurationSetup(config, errors);
        configRepo.save(config);
        syncService.sync();
        return config;
    }

    /**
     * Configuration setup for ping application.
     *
     * @param config cloud configuration
     * @param errors object
     * @throws Exception raise if error
     */
    public void pingConfigurationSetup(CloudStackConfiguration config, Errors errors) throws Exception {
        // Check ping server is reachable or not.
        pingService.apiConnectionCheck(errors);
        JSONObject optional = new JSONObject();
        optional.put(PingConstants.API_URL, config.getApiURL());
        optional.put(PingConstants.API_KEY, config.getApiKey());
        optional.put(PingConstants.SECRET_KEY, config.getSecretKey());
        pingService.pingInitialSync(optional);
    }

    @Override
    public CloudStackConfiguration update(CloudStackConfiguration config) throws Exception {

        Errors errors = validator.rejectIfNullEntity("config", config);
        errors = validator.validateEntity(config, errors);

        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        }

        return configRepo.save(config);
    }

    @Override
    public void delete(CloudStackConfiguration config) throws Exception {
        configRepo.delete(config);
    }

    @Override
    public void delete(Long id) throws Exception {
        configRepo.delete(id);
    }

    @Override
    public CloudStackConfiguration find(Long id) throws Exception {
        CloudStackConfiguration config = configRepo.findOne(id);
        if (config == null) {
            throw new EntityNotFoundException("config.not.found");
        }
        return config;
    }

    @Override
    public Page<CloudStackConfiguration> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return configRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<CloudStackConfiguration> findAll() throws Exception {
        return (List<CloudStackConfiguration>) configRepo.findAll();
    }

    @Override
    public void importCsData(String keyName, String type) throws Exception {
        configUtil.setServer(1L);
        switch (keyName) {
        case DOMAIN:
            //Sync domain
            if (type.equals(IMPORT)) {
                syncService.syncDomain();
            } else {
                syncDomainCount(keyName, type);
            }
            break;
        case ZONE:
            //Sync zone
            if (type.equals(IMPORT)) {
                syncService.syncZone();
            } else {
                syncZoneCount(keyName, type);
            }
            break;
        case DEPARTMENT:
            //Sync department
            if (type.equals(IMPORT)) {
                syncService.syncDepartment();
            } else {
                syncDepartmentCount(keyName, type);
            }
            break;
        case USER:
            //Sync user
            if (type.equals(IMPORT)) {
                syncService.syncUser();
                syncService.syncUpdateUserRole();
            } else {
                syncUserCount(keyName, type);
            }
            break;
        case PROJECT:
            //Sync project
            if (type.equals(IMPORT)) {
                syncService.syncProject();
            } else {
                syncProjectCount(keyName, type);
            }
            break;
        case COMPUTE_OFFER:
            //Sync compute offering
            if (type.equals(IMPORT)) {
                syncService.syncComputeOffering();
            } else {
                syncComputeOfferingCount(keyName, type);
            }
            break;
        case DISK_OFFER:
            //Sync storage offering
            if (type.equals(IMPORT)) {
                syncService.syncStorageOffering();
            } else {
                syncStorageOfferingCount(keyName, type);
            }
            break;
        case NETWORK_OFFER:
            //Sync network offering
            if (type.equals(IMPORT)) {
                syncService.syncNetworkOffering();
            } else {
                syncNetworkOfferingCount(keyName, type);
            }
            break;
        case TEMPLATE:
            //Sync templates
            if (type.equals(IMPORT)) {
                syncService.syncTemplates();
            } else {
                syncTemplatesCount(keyName, type);
            }
            break;
        case VPC_OFFER:
            //Sync VPC offerings
            break;
        case NETWORK:
            //Sync network and dependent functionality
            if (type.equals(IMPORT)) {
                syncNetworkWithDependency();
            } else {
                syncNetworkWithDependencyCount(keyName, type);
            }
            break;
        case VPC:
            //Sync VPC
            break;
        case CHECKALL:
            //Sync count
            syncDomainCount(keyName, type);
            syncZoneCount(keyName, type);
            syncDepartmentCount(keyName, type);
            syncUserCount(keyName, type);
            syncProjectCount(keyName, type);
            syncComputeOfferingCount(keyName, type);
            syncStorageOfferingCount(keyName, type);
            syncNetworkOfferingCount(keyName, type);
            syncTemplatesCount(keyName, type);
            syncNetworkWithDependencyCount(keyName, type);
            break;
        default:
            break;
        }
    }

    /**
     * Sync network with dependent functionality.
     *
     * @throws Exception raise if error
     */
    public void syncNetworkWithDependency() throws Exception {
        try {
            // 1. Sync Network entity
            syncService.syncNetwork();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Network ", e);
        }
        try {
            // 2. Sync Instance entity
            syncService.syncInstances();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Instance", e);
        }
        try {
            // 3. Sync Volume entity
            syncService.syncVolume();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Volume", e);
        }
        try {
            // 4. Sync VmSnapshot entity
            syncService.syncVmSnapshots();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch vm snapshots", e);
        }
        try {
            // 5. Sync Snapshot entity
            syncService.syncSnapshot();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Snapshot", e);
        }
        try {
            // 6. Sync Nic entity
            syncService.syncNic();
            LOGGER.debug("nic");
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Nic", e);
        }
        try {
            // 7. Sync IP address entity
            syncService.syncIpAddress();
            LOGGER.debug("ipAddress");
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Ip Address", e);
        }
        try {
            // 8. Sync Egress firewall rules entity
            syncService.syncEgressFirewallRules();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch EgressRule", e);
        }
        try {
            // 9. Sync Ingress firewall rules entity
            syncService.syncIngressFirewallRules();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch EgressRule", e);
        }
        try {
            // 10. Sync Port Forwarding entity
            syncService.syncPortForwarding();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch PortForwarding", e);
        }
        try {
            // 11. Sync SnapshotPolicy entity
            syncService.syncSnapshotPolicy();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch SnapshotPolicy", e);
        }
        try {
            // 12. Sync Load Balancer entity
            syncService.syncLoadBalancer();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch LoadBalancer", e);
        }
        try {
            // 13. Sync Load Balancer sticky policy entity
            syncService.syncLoadBalancerStickyPolicy();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch LoadBalancer Sticky Policy", e);
        }
    }

    /**
     * Sync domain count.
     *
     * @param keyName key name
     * @param type key type
     * @throws Exception raise if error
     */
    public void syncDomainCount(String keyName, String type) throws Exception {
        List<Domain> csDomainList = domainService.findAllFromCSServer();
        updateManualSyncCount("DOMAIN", csDomainList.size());
    }

    /**
     * Sync domain count.
     *
     * @param keyName key name
     * @param type key type
     * @throws Exception raise if error
     */
    public void syncZoneCount(String keyName, String type) throws Exception {
        List<Zone> csZoneList = zoneService.findAllFromCSServer();
        updateManualSyncCount("ZONE", csZoneList.size());
    }

    /**
     * Sync domain count.
     *
     * @param keyName key name
     * @param type key type
     * @throws Exception raise if error
     */
    public void syncDepartmentCount(String keyName, String type) throws Exception {
        List<Department> csAccountService = departmentService.findAllFromCSServer();
        updateManualSyncCount("DEPARTMENT", csAccountService.size());
    }

    /**
     * Sync domain count.
     *
     * @param keyName key name
     * @param type key type
     * @throws Exception raise if error
     */
    public void syncUserCount(String keyName, String type) throws Exception {
        List<User> csUserService = userService.findAllFromCSServerByDomain();
        updateManualSyncCount("USER", csUserService.size());
    }

    /**
     * Sync domain count.
     *
     * @param keyName key name
     * @param type key type
     * @throws Exception raise if error
     */
    public void syncProjectCount(String keyName, String type) throws Exception {
        List<Project> csProjectList = projectService.findAllFromCSServerByDomain();
        updateManualSyncCount("PROJECT", csProjectList.size());
    }

    /**
     * Sync domain count.
     *
     * @param keyName key name
     * @param type key type
     * @throws Exception raise if error
     */
    public void syncComputeOfferingCount(String keyName, String type) throws Exception {
        List<ComputeOffering> csComputeOfferingList = computeService.findAllFromCSServer();
        updateManualSyncCount("COMPUTE_OFFER", csComputeOfferingList.size());
    }

    /**
     * Sync domain count.
     *
     * @param keyName key name
     * @param type key type
     * @throws Exception raise if error
     */
    public void syncStorageOfferingCount(String keyName, String type) throws Exception {
        List<StorageOffering> csStorageOfferingsList = storageService.findAllFromCSServer();
        updateManualSyncCount("DISK_OFFER", csStorageOfferingsList.size());
    }

    /**
     * Sync domain count.
     *
     * @param keyName key name
     * @param type key type
     * @throws Exception raise if error
     */
    public void syncNetworkOfferingCount(String keyName, String type) throws Exception {
        List<NetworkOffering> csNetworkOfferingList = networkOfferingService.findAllFromCSServer();
        updateManualSyncCount("NETWORK_OFFER", csNetworkOfferingList.size());
    }

    /**
     * Sync domain count.
     *
     * @param keyName key name
     * @param type key type
     * @throws Exception raise if error
     */
    public void syncTemplatesCount(String keyName, String type) throws Exception {
        List<Template> csTemplatesList = templateService.findAllFromCSServer();
        updateManualSyncCount("TEMPLATE", csTemplatesList.size());
    }

    /**
     * Sync domain count.
     *
     * @param keyName key name
     * @param type key type
     * @throws Exception raise if error
     */
    public void syncNetworkWithDependencyCount(String keyName, String type) throws Exception {
        List<Network> csNetworkList = networkService.findAllFromCSServerByDomain();
        updateManualSyncCount("NETWORK", csNetworkList.size());
    }

    /**
     * Update manual sync data count.
     *
     * @param keyName key name
     * @param acsCount ACS count
     * @throws Exception raise if error
     */
    public void updateManualSyncCount(String keyName, Integer acsCount) throws Exception {
        //Update manual sync update counts
        ManualCloudSync manualSyncItem = manualCloudSyncService.findBySyncName(keyName);
        manualSyncItem.setAcsCount(acsCount);
        manualCloudSyncService.save(manualSyncItem);
    }

}
