package ck.panda.service;

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
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Account;
import ck.panda.domain.entity.CloudStackConfiguration;
import ck.panda.domain.entity.Cluster;
import ck.panda.domain.entity.ComputeOffering;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.Host;
import ck.panda.domain.entity.Hypervisor;
import ck.panda.domain.entity.Iso;
import ck.panda.domain.entity.Network;
import ck.panda.domain.entity.NetworkOffering;
import ck.panda.domain.entity.OsCategory;
import ck.panda.domain.entity.OsType;
import ck.panda.domain.entity.Pod;
import ck.panda.domain.entity.Project;
import ck.panda.domain.entity.Region;
import ck.panda.domain.entity.ResourceLimitDepartment;
import ck.panda.domain.entity.ResourceLimitDomain;
import ck.panda.domain.entity.ResourceLimitProject;
import ck.panda.domain.entity.Snapshot;
import ck.panda.domain.entity.StorageOffering;
import ck.panda.domain.entity.Template;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.VmSnapshot;
import ck.panda.domain.entity.Volume;
import ck.panda.domain.entity.Zone;
import ck.panda.util.CloudStackInstanceService;
import ck.panda.util.CloudStackServer;
import ck.panda.util.ConvertUtil;
import ck.panda.util.TokenDetails;
import ck.panda.util.error.exception.ApplicationException;

/**
 * We have to sync up with cloudstack server for the following data
 *
 * 1. Zone 2. Domain 3. Region 4. Hypervisor 5. OS Catogory 6. OS Type 7.
 * Network Offering 8. Template 9. User 10. Network 11. Instance
 *
 * Get the corresponding data from cloud stack server, if the application does
 * not have the data add it. If the application has the data update it, if the
 * application has data which the cloud stack server does not have, then delete
 * it.
 *
 */
@Service
public class SyncServiceImpl implements SyncService {

    /** Domain Service for listing domains. */
    @Autowired
    private DomainService domainService;

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncServiceImpl.class);

    /** RegionSerivce for listing Regions. */
    @Autowired
    private ZoneService zoneService;

    /** Virtual machine Service for listing vms. */
    @Autowired
    private VirtualMachineService virtualMachineService;

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

    /** Account service for listing users. */
    @Autowired
    private AccountService accountService;

    /** For listing users in cloudstack server. */
    @Autowired
    private UserService userService;

    /** For listing hosts in cloudstack server. */
    @Autowired
    private HostService hostService;

    /** For listing snapshots in cloudstack server. */
    @Autowired
    private SnapshotService snapshotService;

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

    /** Resource Limit Service for listing resource limits. */
    @Autowired
    private ResourceLimitDomainService resourceDomainService;

    /** Resource Limit Service for listing resource limits. */
    @Autowired
    private ResourceLimitDepartmentService resourceDepartmentService;

    /** Resource Limit Service for listing resource limits. */
    @Autowired
    private ResourceLimitProjectService resourceProjectService;

    /** CloudStack connector. */
    @Autowired
    private CloudStackServer server;

    /** Convert entity repository reference. */
    @Autowired
    private ConvertUtil entity;

    /** CloudStack configuration . */
    @Autowired
    private CloudStackConfigurationService cloudConfigService;

    /** Secret key value is append. */
    @Value(value = "${aes.salt.secretKey}")
    private String secretKey;

    @Autowired
    private TokenDetails tokenDetails;

    @Override
    public void init(CloudStackServer server) throws Exception {
        CloudStackConfiguration cloudConfig = cloudConfigService.find(1L);
        this.server = server;
        this.server.setServer(cloudConfig.getApiURL(), cloudConfig.getSecretKey(), cloudConfig.getApiKey());
    }

    /**
     * Sync call for synchronization list of Region, domain, region. template,
     * hypervisor
     *
     * @throws Exception
     *             unhandled errors.
     */
    @Override
    public void sync() throws Exception {

         try {
             // 1. Sync Region entity
             this.syncRegion();
         } catch (Exception e) {
             LOGGER.error("ERROR AT synch Region", e);
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
            // 9. Sync Account entity
            this.syncAccount();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Account", e);
        }

        try {
            // 10. Sync User entity
               this.syncUser();
        } catch (Exception e) {
               LOGGER.error("ERROR AT synch User", e);
        }


        try{
            // 11. Sync Project entity
            this.syncProject();
        } catch(Exception e){
            LOGGER.error("ERROR AT sync Project", e);
        }

        try {
            // 12. Sync OSCategory entity
            this.syncOsCategory();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch OS Category", e);
        }


        try {
            // 13. Sync OSType entity
            this.syncOsTypes();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch OS Types", e);
        }


        try {
            // 14. Sync Network offering entity
               this.syncNetworkOffering();
        } catch (Exception e) {
               LOGGER.error("ERROR AT synch NetworkOffering", e);
        }

        try {
            // 15. Sync Compute Offering entity
            this.syncComputeOffering();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Compute Offering", e);
        }

        try {
            // 16. Sync Storage offering entity
            this.syncStorageOffering();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Storage Offering", e);
        }

        try {
            // 17. Sync Iso entity
            this.syncIso();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Iso", e);
        }

        try {
            // 18. Sync Network entity
            this.syncNetwork();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Network ", e);
        }

        try {
            // 19. Sync Volume entity
            this.syncVolume();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Volume", e);
        }

        try {
            // 20. Sync Templates entity
               this.syncTemplates();
        } catch (Exception e) {
               LOGGER.error("ERROR AT synch Templates", e);
        }

        try{
            // 21. Sync ResourceLimit entity
            this.syncResourceLimit();
        }catch(Exception e){
            LOGGER.error("ERROR AT sync ResourceLimit Domain", e);
        }

        try {
            // 22. Sync Instance entity
              this.syncInstances();
        } catch (Exception e) {
              LOGGER.error("ERROR AT synch Instance", e);
        }

        try {
            // 23. Sync VmSnapshot entity
              this.syncVmSnapshots();
          } catch (Exception e) {
              LOGGER.error("ERROR AT synch vm snapshots", e);
          }


        try {
            // 24. Sync Snapshot entity
            this.syncSnapshot();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Snapshot", e);
        }
    }

    /**
     * Sync with CloudStack server Domain.
     *
     * @throws ApplicationException
     *             unhandled application errors.
     * @throws Exception
     *             cloudstack unhandled errors
     */
    @Override
    public void syncDomain() throws ApplicationException, Exception {

        // 1. Get all the domain objects from CS server as hash
        List<Domain> csDomainList = domainService.findAllFromCSServer();
        HashMap<String, Domain> csDomainMap = (HashMap<String, Domain>) Domain.convert(csDomainList);

        // 2. Get all the domain objects from application
        List<Domain> appDomainList = domainService.findAll();

        // 3. Iterate application domain list
        LOGGER.debug("Total rows updated : " + String.valueOf(appDomainList.size()));
        for (Domain domain : appDomainList) {
            // 3.1 Find the corresponding CS server domain object by finding it
            // in a hash using uuid
            if (csDomainMap.containsKey(domain.getUuid())) {
                Domain csDomain = csDomainMap.get(domain.getUuid());

                domain.setName(csDomain.getName());

                // 3.2 If found, update the domain object in app db
                domainService.update(domain);

                // 3.3 Remove once updated, so that we can have the list of cs
                // domain which is not added in the app
                csDomainMap.remove(domain.getUuid());
            } else {
                domainService.delete(domain);
                // 3.2 If not found, delete it from app db
                // TODO clarify the business requirement, since it has impact in
                // the application if it is used
                // TODO clarify is this a soft or hard delete
            }
        }
        // 4. Get the remaining list of cs server hash domain object, then
        // iterate and
        // add it to app db
        for (String key : csDomainMap.keySet()) {
            LOGGER.debug("Syncservice domain uuid:");
            domainService.save(csDomainMap.get(key));

        }
        LOGGER.debug("Total rows added", String.valueOf(csDomainMap.size()));

    }

    /**
     * Sync with Cloud Server Zone.
     *
     * @throws ApplicationException
     *             unhandled application errors.
     * @throws Exception
     *             cloudstack unhandled errors.
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
            LOGGER.debug("Total rows updated : " + String.valueOf(appZoneList.size()));
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
                zoneService.delete(zone);
                // 3.2 If not found, delete it from app db
                // TODO clarify the business requirement, since it has impact in
                // the application if it is used
                // TODO clarify is this a soft or hard delete
            }
        }
        // 4. Get the remaining list of cs server hash zone object, then iterate
        // and
        // add it to app db
        for (String key : csZoneMap.keySet()) {
            LOGGER.debug("Syncservice zone uuid:");
            zoneService.save(csZoneMap.get(key));
        }
        LOGGER.debug("Total rows added : " + String.valueOf(csZoneMap.size()));
    }

    /**
     * Sync with Cloud Server Region.
     *
     * @throws ApplicationException
     *             unhandled application errors.
     * @throws Exception
     *             cloudstack unhandled errors.
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
            LOGGER.debug("Total rows updated : " + String.valueOf(appRegionList.size()));
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
                // 3.2 If not found, delete it from app db
                // TODO clarify the business requirement, since it has impact in
                // the application if it is used
                // TODO clarify is this a soft or hard delete
            }
        }
        // 4. Get the remaining list of cs server hash region object, then
        // iterate and
        // add it to app db
        for (String key : csRegionMap.keySet()) {
            LOGGER.debug("Syncservice region name:");
            regionService.save(csRegionMap.get(key));
        }
        LOGGER.debug("Total rows added : " + String.valueOf(csRegionMap.size()));

    }

    /**
     * Sync with Cloud Server Hypervisor.
     *
     * @throws ApplicationException
     *             unhandled application errors.
     * @throws Exception
     *             cloudstack unhandled errors.
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
            LOGGER.debug("Total rows updated : " + String.valueOf(appHypervisorList.size()));
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
                // 3.2 If not found, delete it from app db
                // TODO clarify the business requirement, since it has impact in
                // the application if it is used
                // TODO clarify is this a soft or hard delete
            }
        }
        // 4. Get the remaining list of cs server hash hypervisor object, then
        // iterate and
        // add it to app db
        for (String key : csHypervisorMap.keySet()) {
            LOGGER.debug("Syncservice hypervisor uuid :");
            hypervisorService.save(csHypervisorMap.get(key));
        }
        LOGGER.debug("Total rows added : " + String.valueOf(csHypervisorMap.size()));

    }

    /**
     * Sync with Cloud Server osCategory.
     *
     * @throws ApplicationException
     *             unhandled application errors.
     * @throws Exception
     *             cloudstack unhandled errors.
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
            LOGGER.debug("Total rows updated : " + String.valueOf(appOsCategoryList.size()));
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
                // 3.2 If not found, delete it from app db
                // TODO clarify the business requirement, since it has impact in
                // the application if it is used
                // TODO clarify is this a soft or hard delete
            }
        }
        // 4. Get the remaining list of cs server hash oscategory object, then
        // iterate and
        // add it to app db
        for (String key : csOsCategoryMap.keySet()) {
            LOGGER.debug("Syncservice os category uuid:");
            osCategoryService.save(csOsCategoryMap.get(key));
        }
        LOGGER.debug("Total rows added : " + String.valueOf(csOsCategoryMap.size()));

    }

    /**
     * Sync with CloudStack server osType.
     *
     * @throws ApplicationException
     *             unhandled application errors.
     * @throws Exception
     *             cloudstack unhandled errors
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
            LOGGER.debug("Total rows updated : " + String.valueOf(appOsTypeList.size()));
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
                // 3.2 If not found, delete it from app db
                // TODO clarify the business requirement, since it has impact in
                // the application if it is used
                // TODO clarify is this a soft or hard delete
            }
        }
        // 4. Get the remaining list of cs server hash osType object, then
        // iterate and
        // add it to app db
        for (String key : csOsTypeMap.keySet()) {
            LOGGER.debug("Syncservice osType uuid :");
            osTypeService.save(csOsTypeMap.get(key));
        }
        LOGGER.debug("Total rows added : " + String.valueOf(csOsTypeMap.size()));

    }

    /**
     * Sync with CloudStack server osType.
     *
     * @throws ApplicationException
     *             unhandled application errors.
     * @throws Exception
     *             cloudstack unhandled errors
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
            LOGGER.debug("Total rows updated : " + String.valueOf(appstorageServiceList.size()));
            storageOffering.setIsSyncFlag(false);
            // 3.1 Find the corresponding CS server osType object by finding it
            // in a hash using uuid
            if (csStorageOfferingMap.containsKey(storageOffering.getUuid())) {
                StorageOffering csStorageOffering = csStorageOfferingMap.get(storageOffering.getUuid());

                csStorageOffering.setDescription(csStorageOffering.getDescription());
                // csOsType.setOsCategoryUuid(csOsType.getOsCategoryUuid());

                // 3.2 If found, update the osType object in app db
                storageService.update(storageOffering);

                // 3.3 Remove once updated, so that we can have the list of cs
                // osType which is not added in the app
                csStorageOfferingMap.remove(storageOffering.getUuid());
            } else {
                storageService.delete(storageOffering);
                // 3.2 If not found, delete it from app db
                // TODO clarify the business requirement, since it has impact in
                // the application if it is used
                // TODO clarify is this a soft or hard delete
            }
        }
        // 4. Get the remaining list of cs server hash osType object, then
        // iterate and
        // add it to app db
        for (String key : csStorageOfferingMap.keySet()) {
            LOGGER.debug("Syncservice storage offering uuid:");
            storageService.save(csStorageOfferingMap.get(key));
        }
        LOGGER.debug("Total rows added : " + String.valueOf(csStorageOfferingMap.size()));

    }

    /**
     * Sync with Cloud Server Account.
     *
     * @throws ApplicationException
     *             unhandled application errors.
     * @throws Exception
     *             cloudstack unhandled errors.
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
            LOGGER.debug("Total rows updated : " + String.valueOf(appUserList.size()));
            user.setSyncFlag(false);
            // 3.1 Find the corresponding CS server user object by finding it in
            // a hash using uuid
            if (csUserMap.containsKey(user.getUuid())) {
                User csUser = csUserMap.get(user.getUuid());

                user.setFirstName(csUser.getFirstName());

                // 3.2 If found, update the user object in app db
                userService.update(user);

                // 3.3 Remove once updated, so that we can have the list of cs
                // user which is not added in the app
                csUserMap.remove(user.getUuid());
            } else {
                if(user.getIsActive() !=  true){
                    userService.softDelete(user);
                } else{
                	userService.delete(user);
                }

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
        LOGGER.debug("Total rows added : " + String.valueOf(csUserMap.size()));

    }

    /**
     * Sync with CloudStack server Network offering.
     *
     * @throws ApplicationException
     *             unhandled application errors.
     * @throws Exception
     *             cloudstack unhandled errors
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
            LOGGER.debug("Total rows updated : " + String.valueOf(appNetworkOfferingList.size()));
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
     * @throws ApplicationException
     *             unhandled application errors.
     * @throws Exception
     *             cloudstack unhandled errors
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
            LOGGER.debug("Total rows updated : " + String.valueOf(appNetworkList.size()));
            network.setSyncFlag(false);
            // 3.1 Find the corresponding CS server network object by finding it
            // in a hash using uuid
            if (csNetworkMap.containsKey(network.getUuid())) {
                Network csNetwork = csNetworkMap.get(network.getUuid());

                network.setName(csNetwork.getName());
                network.setDomainId(csNetwork.getDomainId());
                network.setZoneId(csNetwork.getZoneId());
                network.setDisplayText(csNetwork.getDisplayText());

                // 3.2 If found, update the network object in app db
                networkService.update(network);

                // 3.3 Remove once updated, so that we can have the list of cs
                // network which is not added in the app
                csNetworkMap.remove(network.getUuid());
            } else {
                networkService.delete(network);
                // 3.2 If not found, delete it from app db
                // TODO clarify the business requirement, since it has impact in
                // the application if it is used
                // TODO clarify is this a soft or hard delete
            }
        }
        // 4. Get the remaining list of cs server hash network object, then
        // iterate and
        // add it to app db
        for (String key : csNetworkMap.keySet()) {
            LOGGER.debug("Syncservice network uuid:");
            networkService.save(csNetworkMap.get(key));
        }
        LOGGER.debug("Total rows added : " + String.valueOf(csNetworkMap.size()));

    }

    /**
     * Sync with CloudStack server Domain.
     *
     * @throws ApplicationException
     *             unhandled application errors.
     * @throws Exception
     *             cloudstack unhandled errors
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
            LOGGER.debug("Total rows updated : " + String.valueOf(appComputeList.size()));
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

                computeService.delete(computeOffering);
                // 3.2 If not found, delete it from app db
                // TODO clarify the business requirement, since it has impact in
                // the application if it is used
                // TODO clarify is this a soft or hard delete
            }
        }
        // 4. Get the remaining list of cs server hash domain object, then
        // iterate and
        // add it to app db
        for (String key : csComputeOfferingMap.keySet()) {
            LOGGER.debug("Syncservice compute offering uuid:");
            computeService.save(csComputeOfferingMap.get(key));
        }
        LOGGER.debug("Total rows added : " + String.valueOf(csComputeOfferingMap.size()));
    }

    /**
     * Sync with CloudStack server template.
     *
     * @throws ApplicationException
     *             unhandled application errors.
     * @throws Exception
     *             cloudstack unhandled errors
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
            LOGGER.debug("Total rows updated : " + String.valueOf(appTemplateList.size()));
            // 3.1 Find the corresponding CS server template object by finding
            // it in a hash using uuid
            if (csTemplateMap.containsKey(template.getUuid())) {
                Template csTemplate = csTemplateMap.get(template.getUuid());

                template.setSyncFlag(false);
                csTemplate.setUuid(csTemplate.getUuid());
                csTemplate.setName(csTemplate.getName());
                csTemplate.setDescription(csTemplate.getDescription());
                csTemplate.setShare(csTemplate.getShare());
                csTemplate.setPasswordEnabled(csTemplate.getPasswordEnabled());
                csTemplate.setFormat(csTemplate.getFormat());
                csTemplate.setFeatured(csTemplate.getFeatured());
                csTemplate.setOsType(csTemplate.getOsType());
                csTemplate.setZone(csTemplate.getZone());
                csTemplate.setStatus(csTemplate.getStatus());
                csTemplate.setType(csTemplate.getType());
                csTemplate.setHypervisor(csTemplate.getHypervisor());
                csTemplate.setExtractable(csTemplate.getExtractable());
                csTemplate.setDynamicallyScalable(csTemplate.getDynamicallyScalable());

                // 3.2 If found, update the template object in app db
                templateService.update(template);

                // 3.3 Remove once updated, so that we can have the list of cs
                // template which is not added in the app
                csTemplateMap.remove(template.getUuid());
            } else {
                template.setSyncFlag(false);
                templateService.delete(template);
                // 3.2 If not found, delete it from app db
                // TODO clarify the business requirement, since it has impact in
                // the application if it is used
                // TODO clarify is this a soft or hard delete
            }
        }
        // 4. Get the remaining list of cs server hash template object, then
        // iterate and
        // add it to app db
        for (String key : csTemplateMap.keySet()) {
            templateService.save(csTemplateMap.get(key));
        }
        LOGGER.debug("Total rows added : " + String.valueOf(csTemplateMap.size()));

    }

    /**
     * Sync with Cloud Server Account.
     *
     * @throws ApplicationException
     *             unhandled application errors.
     * @throws Exception
     *             cloudstack unhandled errors.
     */
    @Override
    public void syncDepartment() throws ApplicationException, Exception {

        // 1. Get all the user objects from CS server as hash
        List<Department> csAccountService = departmentService.findAllFromCSServerByDomain();
        HashMap<String, Department> csUserMap = (HashMap<String, Department>) Department.convert(csAccountService);

        // 2. Get all the user objects from application
        List<Department> appUserList = departmentService.findAll();

        // 3. Iterate application user list
        for (Department department : appUserList) {
            department.setSyncFlag(false);
            // 3.1 Find the corresponding CS server user object by finding it in
            // a hash using uuid
            if (csUserMap.containsKey(department.getUuid())) {
                Department csUser = csUserMap.get(department.getUuid());

                department.setFirstName(csUser.getFirstName());

                // 3.2 If found, update the user object in app db
                departmentService.update(department);

                // 3.3 Remove once updated, so that we can have the list of cs
                // user which is not added in the app
                csUserMap.remove(department.getUuid());
            } else {
                departmentService.delete(department);
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
            departmentService.save(csUserMap.get(key));
        }
    }

    /**
     * Sync with Cloud Server Instance.
     *
     * @throws ApplicationException
     *             unhandled application errors.
     * @throws Exception
     *             cloudstack unhandled errors.
     */
    @Override
    public void syncInstances() throws Exception {
        // 1. Get all the vm objects from CS server as hash
        List<VmInstance> csInstanceService = virtualMachineService.findAllFromCSServer();
        HashMap<String, VmInstance> vmMap = (HashMap<String, VmInstance>) VmInstance.convert(csInstanceService);
        // 2. Get all the vm objects from application
        List<VmInstance> appVmList = virtualMachineService.findAll();
        // 3. Iterate application user list
        for (VmInstance instance : appVmList) {
            instance.setSyncFlag(false);
            // 3.1 Find the corresponding CS server vm object by finding it in a
            // hash using uuid
            if (vmMap.containsKey(instance.getUuid())) {
                VmInstance csVm = vmMap.get(instance.getUuid());
                instance.setName(csVm.getName());
                instance.setCpuCore(csVm.getCpuCore());
                instance.setDomainId(csVm.getDomainId());
                instance.setStatus(csVm.getStatus());
                instance.setZoneId(csVm.getZoneId());
                instance.setHostId(csVm.getHostId());
                instance.setPodId(csVm.getPodId());
                instance.setComputeOfferingId(csVm.getComputeOfferingId());
                instance.setCpuSpeed(csVm.getCpuSpeed());
                instance.setMemory(csVm.getMemory());
                instance.setCpuUsage(csVm.getCpuUsage());
                instance.setPasswordEnabled(csVm.getPasswordEnabled());
                instance.setPassword(csVm.getPassword());
                instance.setIso(csVm.getIso());
                instance.setIsoName(csVm.getIsoName());
                instance.setIpAddress(csVm.getIpAddress());
                instance.setNetworkId(csVm.getNetworkId());
                instance.setDepartmentId(csVm.getDepartmentId());
                instance.setProjectId(csVm.getProjectId());
                instance.setInstanceOwnerId(csVm.getInstanceOwnerId());

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
     * @throws ApplicationException
     *             unhandled application errors.
     * @throws Exception
     *             cloudstack unhandled errors.
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
                Host csUser = csHostMap.get(host.getUuid());

                host.setName(csUser.getName());

                // 3.2 If found, update the user object in app db
                hostService.update(host);

                // 3.3 Remove once updated, so that we can have the list of cs
                // host which is not added in the app
                csHostMap.remove(host.getUuid());
            } else {
                hostService.delete(host);
                // 3.2 If not found, delete it from app db
                // TODO clarify the business requirement, since it has impact in
                // the application if it is used
                // TODO clarify is this a soft or hard delete
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
     * @throws ApplicationException
     *             unhandled application errors.
     * @throws Exception
     *             cloudstack unhandled errors.
     */
    public void syncVolume() throws ApplicationException, Exception {

        // 1. Get all the StorageOffering objects from CS server as hash
        List<Volume> volumeList = volumeService.findAllFromCSServer();
        HashMap<String, Volume> csVolumeMap = (HashMap<String, Volume>) Volume.convert(volumeList);

        // 2. Get all the osType objects from application
        List<Volume> appvolumeServiceList = volumeService.findAll();

        // 3. Iterate application osType list
        for (Volume volume : appvolumeServiceList) {
            volume.setIsSyncFlag(false);
            // 3.1 Find the corresponding CS server osType object by finding it
            // in a hash using uuid
            if (csVolumeMap.containsKey(volume.getUuid())) {
                Volume csvolume = csVolumeMap.get(volume.getUuid());

                csvolume.setName(csvolume.getName());
                // csvolume.setStorageOfferingId(csvolume.getStorageOfferingId());
                // csvolume.setZoneId(csvolume.getZoneId());
                // csOsType.setOsCategoryUuid(csOsType.getOsCategoryUuid());

                // 3.2 If found, update the osType object in app db
                volumeService.update(volume);

                // 3.3 Remove once updated, so that we can have the list of cs
                // osType which is not added in the app
                csVolumeMap.remove(volume.getUuid());
            } else {
                volume.setIsSyncFlag(false);
                volumeService.delete(volume);
                // 3.2 If not found, delete it from app db
                // TODO clarify the business requirement, since it has impact in
                // the application if it is used
                // TODO clarify is this a soft or hard delete
            }
        }
        // 4. Get the remaining list of cs server hash osType object, then
        // iterate and
        // add it to app db
        for (String key : csVolumeMap.keySet()) {

            volumeService.save(csVolumeMap.get(key));
        }
    }

    /**
     * Sync with Cloud Server Snapshot.
     *
     * @throws ApplicationException
     *             unhandled application errors.
     * @throws Exception
     *             cloudstack unhandled errors.
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
                snapshotService.delete(snapshot);
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
            snapshotService.save(csSnapshotMap.get(key));
        }
    }

    /**
     * Sync with Cloud Server Pod.
     *
     * @throws ApplicationException
     *             unhandled application errors.
     * @throws Exception
     *             cloudstack unhandled errors.
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

                // 3.2 If found, update the pod object in app db
                podService.update(pod);

                // 3.3 Remove once updated, so that we can have the list of cs
                // host which is not added in the app
                csPodMap.remove(pod.getUuid());
            } else {
                podService.delete(pod);
                // 3.2 If not found, delete it from app db
                // TODO clarify the business requirement, since it has impact in
                // the application if it is used
                // TODO clarify is this a soft or hard delete
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
     * @throws ApplicationException
     *             unhandled application errors.
     * @throws Exception
     *             cloudstack unhandled errors.
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
     * Sync with Cloud Server Instance snapshots.
     *
     * @throws ApplicationException
     *             unhandled application errors.
     * @throws Exception
     *             cloudstack unhandled errors.
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
     * @param Object
     *            response object.
     * @throws ApplicationException
     *             unhandled application errors.
     * @throws Exception
     *             cloudstack unhandled errors.
     */
    @Override
    public void syncResourceStatus(String Object) throws Exception {
        CloudStackConfiguration cloudConfig = cloudConfigService.find(1L);
        server.setServer(cloudConfig.getApiURL(), cloudConfig.getSecretKey(), cloudConfig.getApiKey());
        cloudStackInstanceService.setServer(server);
        String instances = cloudStackInstanceService.queryAsyncJobResult(Object, "json");
        JSONObject jobresult = new JSONObject(instances).getJSONObject("queryasyncjobresultresponse")
                .getJSONObject("jobresult");
        if (jobresult.has("virtualmachine")) {
            VmInstance vmInstance = VmInstance.convert(jobresult.getJSONObject("virtualmachine"), entity);
            VmInstance instance = virtualMachineService.findByUUID(vmInstance.getUuid());
            instance.setSyncFlag(false);
            // 3.1 Find the corresponding CS server vm object by finding it in a
            // hash using uuid
            if (vmInstance.getUuid().equals(instance.getUuid())) {
                VmInstance csVm = vmInstance;
                instance.setName(csVm.getName());
                instance.setCpuCore(csVm.getCpuCore());
                instance.setDomainId(csVm.getDomainId());
                instance.setStatus(csVm.getStatus());
                instance.setZoneId(csVm.getZoneId());
                instance.setHostId(csVm.getHostId());
                instance.setPodId(csVm.getPodId());
                instance.setComputeOfferingId(csVm.getComputeOfferingId());
                instance.setCpuSpeed(csVm.getCpuSpeed());
                instance.setMemory(csVm.getMemory());
                instance.setCpuUsage(csVm.getCpuUsage());
                instance.setPasswordEnabled(csVm.getPasswordEnabled());
                instance.setPassword(csVm.getPassword());
                instance.setIso(csVm.getIso());
                instance.setIsoName(csVm.getIsoName());
                instance.setIpAddress(csVm.getIpAddress());
                instance.setNetworkId(csVm.getNetworkId());
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
            }
        }
    }

    @Override
    public void syncResourceLimit() throws ApplicationException, Exception {
        List<Domain> domains = domainService.findAll();
        for (Domain domain : domains) {
            syncResourceLimitDomain(domain.getUuid());
        }

        List<Department> departments = departmentService.findAllByIsActive(true);
        for (Department department : departments) {
            syncResourceLimitDepartment(department.getDomainId(), department.getUserName());
        }

        List<Project> projects = projectService.findAllByActive(true);
        for (Project project : projects) {
            syncResourceLimitProject(project.getUuid());
        }

    }

    @Override
    public void syncResourceLimitDomain(String domainId) throws ApplicationException, Exception {

        // 1. Get all the ResourceLimit objects from CS server as hash
        List<ResourceLimitDomain> csResourceList = resourceDomainService.findAllFromCSServerDomain(domainId);
        HashMap<String, ResourceLimitDomain> csResourceMap = (HashMap<String, ResourceLimitDomain>) ResourceLimitDomain
                .convert(csResourceList);

        // 2. Get all the resource objects from application
        List<ResourceLimitDomain> appResourceList = resourceDomainService.findAll();

        // 3. Iterate Domain resource list
        LOGGER.debug("Total rows updated : " + String.valueOf(appResourceList.size()));
        for (ResourceLimitDomain resource : appResourceList) {
            resource.setIsSyncFlag(false);
            String resourceLimit = resource.getDomainId() + "-" + resource.getResourceType();
            // 3.1 Find the corresponding CS server resource object by finding
            // it in a hash using uuid
            if (csResourceMap.containsKey(resourceLimit)) {
                ResourceLimitDomain csResource = csResourceMap.get(resourceLimit);
                resource.setIsActive(true);
                // resource.setName(csResource.getName());

                // 3.2 If found, update the resource object in app db
                resourceDomainService.update(resource);

                // 3.3 Remove once updated, so that we can have the list of cs
                // resource which is not added in the app
                csResourceMap.remove(resourceLimit);
            } else {
                // resource.setIsSyncFlag(false);
                  resourceDomainService.update(resource);
                // resourceDomainService.delete(resource);
                // 3.2 If not found, delete it from app db
                // TODO clarify the business requirement, since it has impact in
                // the application if it is used
                // TODO clarify is this a soft or hard delete
            }
        }
        // 4. Get the remaining list of cs server hash resource object, then
        // iterate and
        // add it to app db
        for (String key : csResourceMap.keySet()) {
            LOGGER.debug("Syncservice resource Domain id:");
            resourceDomainService.save(csResourceMap.get(key));

        }
        LOGGER.debug("Total rows added", String.valueOf(csResourceMap.size()));

    }

    @Override
    public void syncResourceLimitDepartment(Long domainId, String department) throws ApplicationException, Exception {

        // 1. Get all the ResourceLimit objects from CS server as hash
        List<ResourceLimitDepartment> csResourceList = resourceDepartmentService.findAllFromCSServerDepartment(domainId,
                department);
        HashMap<String, ResourceLimitDepartment> csResourceMap = (HashMap<String, ResourceLimitDepartment>) ResourceLimitDepartment
                .convert(csResourceList);

        // 2. Get all the resource objects from application
        List<ResourceLimitDepartment> appResourceList = resourceDepartmentService.findAll();

        // 3. Iterate application resource list
        LOGGER.debug("Total rows updated : " + String.valueOf(appResourceList.size()));
        for (ResourceLimitDepartment resource : appResourceList) {
            resource.setIsSyncFlag(false);
            String resourceLimit = resource.getDepartmentId() + "-" + resource.getResourceType();
            // 3.1 Find the corresponding CS server resource object by finding
            // it in a hash using uuid
            if (csResourceMap.containsKey(resourceLimit)) {
                ResourceLimitDepartment csResource = csResourceMap.get(resourceLimit);
                resource.setIsActive(true);
                // 3.2 If found, update the resource object in app db
                resourceDepartmentService.update(resource);

                // 3.3 Remove once updated, so that we can have the list of cs
                // resource which is not added in the app
                csResourceMap.remove(resourceLimit);
            } else {
                resource.setIsSyncFlag(false);
                resourceDepartmentService.update(resource);
                // resourceDepartmentService.delete(resource);
                // 3.2 If not found, delete it from app db
                // TODO clarify the business requirement, since it has impact in
                // the application if it is used
                // TODO clarify is this a soft or hard delete
            }
        }
        // 4. Get the remaining list of cs server hash resource object, then
        // iterate and
        // add it to app db
        for (String key : csResourceMap.keySet()) {
            LOGGER.debug("Syncservice resource Department id:");
            resourceDepartmentService.save(csResourceMap.get(key));

        }
        LOGGER.debug("Total rows added", String.valueOf(csResourceMap.size()));

    }

    @Override
    public void syncResourceLimitProject(String projectId) throws ApplicationException, Exception {

        // 1. Get all the ResourceLimit objects from CS server as hash
        List<ResourceLimitProject> csResourceList = resourceProjectService.findAllFromCSServerProject(projectId);
               HashMap<String, ResourceLimitProject> csResourceMap = (HashMap<String, ResourceLimitProject>) ResourceLimitProject
                .convert(csResourceList);

        // 2. Get all the resource objects from application
        List<ResourceLimitProject> appResourceList = resourceProjectService.findAll();

        // 3. Iterate application resource list
        LOGGER.debug("Total rows updated : " + String.valueOf(appResourceList.size()));
        for (ResourceLimitProject resource : appResourceList) {
            resource.setIsSyncFlag(false);
            String resourceLimit = resource.getProjectId() + "-" + resource.getResourceType();
            // 3.1 Find the corresponding CS server resource object by finding
            // it in a hash using uuid
            if (csResourceMap.containsKey(resourceLimit)) {
                ResourceLimitProject csResource = csResourceMap.get(resourceLimit);
                resource.setIsActive(true);
                // resource.setName(csResource.getName());

                // 3.2 If found, update the resource object in app db
                resourceProjectService.update(resource);

                // 3.3 Remove once updated, so that we can have the list of cs
                // resource which is not added in the app
                csResourceMap.remove(resourceLimit);
            } else {
                resourceProjectService.update(resource);
                // 3.2 If not found, delete it from app db
                // TODO clarify the business requirement, since it has impact in
                // the application if it is used
                // TODO clarify is this a soft or hard delete
            }
        }
        // 4. Get the remaining list of cs server hash resource object, then
        // iterate and
        // add it to app db
        for (String key : csResourceMap.keySet()) {
            LOGGER.debug("Syncservice resource Domain id:");
            resourceProjectService.save(csResourceMap.get(key));

        }
        LOGGER.debug("Total rows added", String.valueOf(csResourceMap.size()));

    }

    /**
     * Sync with Cloud Server Iso.
     *
     * @throws ApplicationException
     *             unhandled application errors.
     * @throws Exception
     *             cloudstack unhandled errors.
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
                isoService.delete(iso);
                // 3.2 If not found, delete it from app db
                // TODO clarify the business requirement, since it has impact in
                // the application if it is used
                // TODO clarify is this a soft or hard delete
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
            LOGGER.debug("Total rows updated : " + String.valueOf(appProjectList.size()));
            // 3.1 Find the corresponding CS server projectService object by
            // finding it in a hash using uuid
            if (csProjectMap.containsKey(project.getUuid())) {
                Project csNetworkOffering = csProjectMap.get(project.getUuid());

                project.setName(csNetworkOffering.getName());

                // 3.2 If found, update the project object in app db
                projectService.update(project);

                // 3.3 Remove once updated, so that we can have the list of cs
                // project which is not added in the app
                csProjectMap.remove(project.getUuid());
            } else {
                projectService.delete(project);
                // 3.2 If not found, delete it from app db
                // TODO clarify the business requirement, since it has impact in
                // the application if it is used
                // TODO clarify is this a soft or hard delete
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

    /**
     * Sync with Cloud Server Account.
     *
     * @throws ApplicationException
     *             unhandled application errors.
     * @throws Exception
     *             cloudstack unhandled errors.
     */
    public void syncAccount() throws ApplicationException, Exception {

        // 1. Get all the account objects from CS server as hash
        List<Account> csAccountService = accountService.findAllFromCSServerByDomain();
        HashMap<String, Account> csAccountMap = (HashMap<String, Account>) Account.convert(csAccountService);

        // 2. Get all the account objects from application
        List<Account> appAccountList = accountService.findAll();

        // 3. Iterate application account list
        for (Account account : appAccountList) {
            // 3.1 Find the corresponding CS server account object by finding it in
            // a hash using uuid
            if (csAccountMap.containsKey(account.getUuid())) {
                Account csUser = csAccountMap.get(account.getUuid());

                account.setFirstName(csUser.getFirstName());

                // 3.2 If found, update the account object in app db
                accountService.update(account);

                // 3.3 Remove once updated, so that we can have the list of cs
                // user which is not added in the app
                csAccountMap.remove(account.getUuid());
            } else {
                accountService.delete(account);
                // 3.2 If not found, delete it from app db
                // TODO clarify the business requirement, since it has impact in
                // the application if it is used
                // TODO clarify is this a soft or hard delete
            }

        }
        // 4. Get the remaining list of cs server hash account object, then iterate
        // and
        // add it to app db
        for (String key : csAccountMap.keySet()) {
            accountService.save(csAccountMap.get(key));
        }
    }
}
