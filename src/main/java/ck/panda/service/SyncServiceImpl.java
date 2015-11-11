package ck.panda.service;

import java.util.HashMap;
import java.util.List;

import org.neo4j.cypher.internal.compiler.v2_1.perty.docbuilders.toStringDocBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.CloudStackConfiguration;
import ck.panda.domain.entity.ComputeOffering;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.Hypervisor;
import ck.panda.domain.entity.Network;
import ck.panda.domain.entity.NetworkOffering;
import ck.panda.domain.entity.OsCategory;
import ck.panda.domain.entity.OsType;
import ck.panda.domain.entity.Region;
import ck.panda.domain.entity.StorageOffering;
import ck.panda.domain.entity.Template;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.Zone;
import ck.panda.util.CloudStackServer;
import ck.panda.util.error.exception.ApplicationException;

/**
 * We have to sync up with cloudstack server for the following data
 *
 * 1. Zone
 * 2. Domain
 * 3. Region
 * 4. Hypervisor
 * 5. OS Catogory
 * 6. OS Type
 * 7. Network Offering
 * 8. Template
 * 9. User
 * 10. Network
 *
 * Get the corresponding data from cloud stack server, if the application does not have the data add it.
 * If the application has the data update it, if the application has data which the cloud stack server does not have,
 * then delete it.
 *
 */
@Service
public class SyncServiceImpl  implements SyncService {

    /** Domain Service  for listing domains. */
    @Autowired
    private DomainService domainService;

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncServiceImpl.class);

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

    /** Storage offering service for listing storage offers. */
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

    /** User service for listing users. */
    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private UserService userService;

    /** Template Service  for listing templates. */
    @Autowired
    private TemplateService templateService;

    /** CloudStack connector. */
    @Autowired
    private CloudStackServer server;

    /** CloudStack configuration . */
    @Autowired
    private CloudStackConfigurationService cloudConfigService;


    @Override
    public void init() throws Exception {
       CloudStackConfiguration cloudConfig = cloudConfigService.find(1L);
       System.out.println(server.getClass().getName());
       server.setServer(cloudConfig.getApiURL(), cloudConfig.getSecretKey(), cloudConfig.getApiKey());
    }

   /**
     * Sync call for synchronization list of Region, domain, region. template, hypervisor
     * @throws Exception unhandled errors.
     */
    @Override
    public void sync() throws Exception {
      try {
         // 1. Sync Domain entity
         this.syncDomain();
      } catch (Exception e) {
            LOGGER.error("ERROR AT synch Domaim", e);
      }
        try {
         // 2. Sync Region entity
         this.syncRegion();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Region", e);
        }
        try {
         // 3. Sync Zone entity
         this.syncZone();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Zone", e);
        }
        try {
         // 4. Sync Hypervisor entity
         this.syncHypervisor();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Hypervisor", e);
        }
        try {
         // 5. Sync OSCategory entity
         this.syncOsCategory();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch OS Category", e);
        }
        try {
         // 6. Sync OSType entity
         this.syncOsTypes();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch OS Types", e);
        }
        try {
         // 7. Sync Storage offering entity
         this.syncStorageOffering();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Storage Offering", e);
        }
        try {
         // 8. Sync Network Offering entity
         this.syncNetworkOffering();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Network Offering", e);
        }
        try {
         // 9. Sync Network entity
         this.syncNetwork();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Network ", e);
        }
        try {
         // 10. Sync Compute Offering entity
         this.syncComputeOffering();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Compute Offering", e);
        }
        try {
         // 11. Sync User entity
         this.syncUser();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch User", e);
        }
        try {
         // 12. Sync Templates entity
         this.syncTemplates();
        } catch (Exception e) {
            LOGGER.error("ERROR AT synch Templates", e);
        }

        try{
            //12. Sync Templates entity
            this.syncDepartment();
            }catch(Exception e){
                LOGGER.error("ERROR AT synch Department", e);
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

        //1. Get all the domain objects from CS server as hash
        List<Domain> csDomainList = domainService.findAllFromCSServer();
        HashMap<String, Domain> csDomainMap = (HashMap<String, Domain>) Domain.convert(csDomainList);

        //2. Get all the domain objects from application
        List<Domain> appDomainList = domainService.findAll();

        // 3. Iterate application domain list
        LOGGER.debug("Total rows updated : " + String.valueOf(appDomainList.size()));
        for (Domain domain: appDomainList) {
               //3.1 Find the corresponding CS server domain object by finding it in a hash using uuid
            if (csDomainMap.containsKey(domain.getUuid())) {
                Domain csDomain = csDomainMap.get(domain.getUuid());

                domain.setName(csDomain.getName());

                //3.2 If found, update the domain object in app db
                domainService.update(domain);

                //3.3 Remove once updated, so that we can have the list of cs domain which is not added in the app
                csDomainMap.remove(domain.getUuid());
            } else {
                domainService.delete(domain);
                //3.2 If not found, delete it from app db
                //TODO clarify the business requirement, since it has impact in the application if it is used
                //TODO clarify is this a soft or hard delete
            }
        }
        //4. Get the remaining list of cs server hash domain object, then iterate and
        //add it to app db
           for (String key: csDomainMap.keySet()) {
            Domain domain = new Domain();
            LOGGER.debug("Syncservice domain uuid:");
            domainService.save(csDomainMap.get(key));

        }
     LOGGER.debug("Total rows added",String.valueOf(csDomainMap.size()));

    }

    /**
     * Sync with Cloud Server Zone.
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    @Override
    public void syncZone() throws ApplicationException, Exception {

        //1. Get all the zone objects from CS server as hash
        List<Zone> csZoneList = zoneService.findAllFromCSServer();
        HashMap<String, Zone> csZoneMap = (HashMap<String, Zone>) Zone.convert(csZoneList);

        //2. Get all the zone objects from application
        List<Zone> appZoneList = zoneService.findAll();

        // 3. Iterate application zone list
        for (Zone zone: appZoneList) {
            LOGGER.debug("Total rows updated : " + String.valueOf(appZoneList.size()));
             //3.1 Find the corresponding CS server zone object by finding it in a hash using uuid
            if (csZoneMap.containsKey(zone.getUuid())) {
                Zone csZone = csZoneMap.get(zone.getUuid());

                zone.setName(csZone.getName());

                //3.2 If found, update the zone object in app db
                zoneService.update(zone);

                //3.3 Remove once updated, so that we can have the list of cs zone which is not added in the app
                csZoneMap.remove(zone.getUuid());
            } else {
                zoneService.delete(zone);
                //3.2 If not found, delete it from app db
                //TODO clarify the business requirement, since it has impact in the application if it is used
                //TODO clarify is this a soft or hard delete
            }
        }
        //4. Get the remaining list of cs server hash zone object, then iterate and
        //add it to app db
        for (String key: csZoneMap.keySet()) {
            LOGGER.debug("Syncservice zone uuid:");
            zoneService.save(csZoneMap.get(key));
        }
        LOGGER.debug("Total rows added : " + String.valueOf(csZoneMap.size()));
    }

    /**
     * Sync with Cloud Server Region.
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    @Override
    public void syncRegion() throws ApplicationException, Exception {

        //1. Get all the region objects from CS server as hash
        List<Region> csRegionList = regionService.findAllFromCSServer();
        HashMap<String, Region> csRegionMap = (HashMap<String, Region>) Region.convert(csRegionList);

        //2. Get all the region objects from application
        List<Region> appRegionList = regionService.findAll();

        // 3. Iterate application region list
        for (Region region: appRegionList) {
             LOGGER.debug("Total rows updated : " + String.valueOf(appRegionList.size()));
             //3.1 Find the corresponding CS server region object by finding it in a hash using uuid
            if (csRegionMap.containsKey(region.getName())) {
                Region csRegion = csRegionMap.get(region.getName());

                region.setName(csRegion.getName());
                region.setEndPoint(csRegion.getEndPoint());

                //3.2 If found, update the region object in app db
                regionService.update(region);

                //3.3 Remove once updated, so that we can have the list of cs region which is not added in the app
                csRegionMap.remove(region.getName());
            } else {
                regionService.delete(region);
                //3.2 If not found, delete it from app db
                //TODO clarify the business requirement, since it has impact in the application if it is used
                //TODO clarify is this a soft or hard delete
            }
        }
        //4. Get the remaining list of cs server hash region object, then iterate and
        //add it to app db
        for (String key: csRegionMap.keySet()) {
            LOGGER.debug("Syncservice region name:");
            regionService.save(csRegionMap.get(key));
        }
        LOGGER.debug("Total rows added : " + String.valueOf(csRegionMap.size()));

    }

    /**
     * Sync with Cloud Server Hypervisor.
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    @Override
    public void syncHypervisor() throws ApplicationException, Exception {

        //1. Get all the hypervisor objects from CS server as hash
        List<Hypervisor> csHypervisorList = hypervisorService.findAllFromCSServer();
        HashMap<String, Hypervisor> csHypervisorMap = (HashMap<String, Hypervisor>) Hypervisor.convert(csHypervisorList);

        //2. Get all the hypervisor objects from application
        List<Hypervisor> appHypervisorList = hypervisorService.findAll();

        // 3. Iterate application hypervisor list
        for (Hypervisor hypervisor: appHypervisorList) {
             LOGGER.debug("Total rows updated : " + String.valueOf(appHypervisorList.size()));
            //3.1 Find the corresponding CS server hypervisor object by finding it in a hash using uuid
            if (csHypervisorMap.containsKey(hypervisor.getName())) {
                 Hypervisor csHypervisor = csHypervisorMap.get(hypervisor.getName());

                 hypervisor.setName(csHypervisor.getName());
                //3.2 If found, update the hypervisor object in app db
                hypervisorService.update(hypervisor);

                //3.3 Remove once updated, so that we can have the list of cs hypervisor which is not added in the app
                csHypervisorMap.remove(hypervisor.getName());
            } else {
                hypervisorService.delete(hypervisor);
                //3.2 If not found, delete it from app db
                //TODO clarify the business requirement, since it has impact in the application if it is used
                //TODO clarify is this a soft or hard delete
            }
        }
        //4. Get the remaining list of cs server hash hypervisor object, then iterate and
        //add it to app db
        for (String key: csHypervisorMap.keySet()) {
            LOGGER.debug("Syncservice hypervisor uuid :");
            hypervisorService.save(csHypervisorMap.get(key));
        }
        LOGGER.debug("Total rows added : " + String.valueOf(csHypervisorMap.size()));

    }

    /**
     * Sync with Cloud Server Region.
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    @Override
    public void syncOsCategory() throws ApplicationException, Exception {

        //1. Get all the oscategory objects from CS server as hash
        List<OsCategory> csOsCategoryList = osCategoryService.findAllFromCSServer();
        HashMap<String, OsCategory> csOsCategoryMap = (HashMap<String, OsCategory>) OsCategory.convert(csOsCategoryList);

        //2. Get all the oscategory objects from application
        List<OsCategory> appOsCategoryList = osCategoryService.findAll();

        // 3. Iterate application oscategory list
        for (OsCategory osCategory: appOsCategoryList) {
            LOGGER.debug("Total rows updated : " + String.valueOf(appOsCategoryList.size()));
            //3.1 Find the corresponding CS server oscategory object by finding it in a hash using uuid
            if (csOsCategoryMap.containsKey(osCategory.getUuid())) {
                OsCategory csOsCategory = csOsCategoryMap.get(osCategory.getUuid());

                osCategory.setName(csOsCategory.getName());
                //3.2 If found, update the oscategory object in app db
                osCategoryService.update(osCategory);

                //3.3 Remove once updated, so that we can have the list of cs oscategory which is not added in the app
                csOsCategoryMap.remove(osCategory.getUuid());
            } else {
                osCategoryService.delete(osCategory);
                //3.2 If not found, delete it from app db
                //TODO clarify the business requirement, since it has impact in the application if it is used
                //TODO clarify is this a soft or hard delete
            }
        }
        //4. Get the remaining list of cs server hash oscategory object, then iterate and
        //add it to app db
        for (String key: csOsCategoryMap.keySet()) {
            LOGGER.debug("Syncservice os category uuid:");
            osCategoryService.save(csOsCategoryMap.get(key));
        }
        LOGGER.debug("Total rows added : " + String.valueOf(csOsCategoryMap.size()));

    }

    /**
     * Sync with CloudStack server osType.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors
     */
    @Override
    public void syncOsTypes() throws ApplicationException, Exception {

        //1. Get all the osType objects from CS server as hash
        List<OsType> csOsTypesList = osTypeService.findAllFromCSServer();
        HashMap<String, OsType> csOsTypeMap = (HashMap<String, OsType>) OsType.convert(csOsTypesList);

        //2. Get all the osType objects from application
        List<OsType> appOsTypeList = osTypeService.findAll();

        // 3. Iterate application osType list
        for (OsType osType: appOsTypeList) {
               LOGGER.debug("Total rows updated : " + String.valueOf(appOsTypeList.size()));
             //3.1 Find the corresponding CS server osType object by finding it in a hash using uuid
            if (csOsTypeMap.containsKey(osType.getUuid())) {
                OsType csOsType = csOsTypeMap.get(osType.getUuid());

                csOsType.setDescription(csOsType.getDescription());
                csOsType.setOsCategoryUuid(csOsType.getOsCategoryUuid());

                //3.2 If found, update the osType object in app db
                osTypeService.update(osType);

                //3.3 Remove once updated, so that we can have the list of cs osType which is not added in the app
                csOsTypeMap.remove(osType.getUuid());
            } else {
                osTypeService.delete(osType);
                //3.2 If not found, delete it from app db
                //TODO clarify the business requirement, since it has impact in the application if it is used
                //TODO clarify is this a soft or hard delete
            }
        }
        //4. Get the remaining list of cs server hash osType object, then iterate and
        //add it to app db
        for (String key: csOsTypeMap.keySet()) {
            LOGGER.debug("Syncservice osType uuid :");
            osTypeService.save(csOsTypeMap.get(key));
        }
        LOGGER.debug("Total rows added : " + String.valueOf(csOsTypeMap.size()));

    }

    /**
     * Sync with CloudStack server osType.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors
     */
    @Override
    public void syncStorageOffering() throws ApplicationException, Exception {

        //1. Get all the StorageOffering objects from CS server as hash
        List<StorageOffering> csStorageOfferingsList = storageService.findAllFromCSServer();
        HashMap<String, StorageOffering> csStorageOfferingMap = (HashMap<String, StorageOffering>) StorageOffering.convert(csStorageOfferingsList);

        //2. Get all the osType objects from application
        List<StorageOffering> appstorageServiceList = storageService.findAll();

        // 3. Iterate application osType list
        for (StorageOffering storageOffering: appstorageServiceList) {
              LOGGER.debug("Total rows updated : " + String.valueOf(appstorageServiceList.size()));
            storageOffering.setIsSyncFlag(false);
             //3.1 Find the corresponding CS server osType object by finding it in a hash using uuid
            if (csStorageOfferingMap.containsKey(storageOffering.getUuid())) {
                StorageOffering csStorageOffering = csStorageOfferingMap.get(storageOffering.getUuid());

                csStorageOffering.setDescription(csStorageOffering.getDescription());
//                csOsType.setOsCategoryUuid(csOsType.getOsCategoryUuid());

                //3.2 If found, update the osType object in app db
                storageService.update(storageOffering);

                //3.3 Remove once updated, so that we can have the list of cs osType which is not added in the app
                csStorageOfferingMap.remove(storageOffering.getUuid());
            } else {
                storageService.delete(storageOffering);
                //3.2 If not found, delete it from app db
                //TODO clarify the business requirement, since it has impact in the application if it is used
                //TODO clarify is this a soft or hard delete
            }
        }
        //4. Get the remaining list of cs server hash osType object, then iterate and
        //add it to app db
        for (String key: csStorageOfferingMap.keySet()) {
            LOGGER.debug("Syncservice storage offering uuid:");
            storageService.save(csStorageOfferingMap.get(key));
        }
        LOGGER.debug("Total rows added : " + String.valueOf(csStorageOfferingMap.size()));

    }

     /**
     * Sync with Cloud Server Account.
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    @Override
    public void syncUser() throws ApplicationException, Exception {

        //1. Get all the user objects from CS server as hash
        List<User> csAccountService = userService.findAllFromCSServer();
        HashMap<String, User> csUserMap = (HashMap<String, User>) User.convert(csAccountService);

        //2. Get all the user objects from application
        List<User> appUserList = userService.findAll();

        // 3. Iterate application user list
        for (User user: appUserList) {
              LOGGER.debug("Total rows updated : " + String.valueOf(appUserList.size()));
            user.setSyncFlag(false);
             //3.1 Find the corresponding CS server user object by finding it in a hash using uuid
            if (csUserMap.containsKey(user.getUuid())) {
                User csUser = csUserMap.get(user.getUuid());

                user.setFirstName(csUser.getFirstName());

                //3.2 If found, update the user object in app db
                userService.update(user);

                //3.3 Remove once updated, so that we can have the list of cs user which is not added in the app
                csUserMap.remove(user.getUuid());
            } else {
                userService.delete(user);
                //3.2 If not found, delete it from app db
                //TODO clarify the business requirement, since it has impact in the application if it is used
                //TODO clarify is this a soft or hard delete
             }

             }
        //4. Get the remaining list of cs server hash user object, then iterate and
        //add it to app db
        for (String key: csUserMap.keySet()) {
            LOGGER.debug("Syncservice user uuid:");
            userService.save(csUserMap.get(key));
        }
        LOGGER.debug("Total rows added : " + String.valueOf(csUserMap.size()));

    }

   /**
     * Sync with CloudStack server Network offering.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors
     */
    @Override
    public void syncNetworkOffering() throws ApplicationException, Exception {

        //1. Get all the networkOffering objects from CS server as hash
        List<NetworkOffering> csNetworkOfferingList = networkOfferingService.findAllFromCSServer();
        HashMap<String, NetworkOffering> csNetworkOfferingMap = (HashMap<String, NetworkOffering>) NetworkOffering.convert(csNetworkOfferingList);

        //2. Get all the networkOffering objects from application
        List<NetworkOffering> appNetworkOfferingList = networkOfferingService.findAll();

        // 3. Iterate application networkOffering list
        for (NetworkOffering networkOffering: appNetworkOfferingList) {
              LOGGER.debug("Total rows updated : " + String.valueOf(appNetworkOfferingList.size()));
             //3.1 Find the corresponding CS server networkOfferingService object by finding it in a hash using uuid
            if (csNetworkOfferingMap.containsKey(networkOffering.getUuid())) {
                NetworkOffering csNetworkOffering = csNetworkOfferingMap.get(networkOffering.getUuid());

                networkOffering.setName(csNetworkOffering.getName());

                //3.2 If found, update the networkOffering object in app db
                networkOfferingService.update(networkOffering);

                //3.3 Remove once updated, so that we can have the list of cs networkOffering which is not added in the app
                csNetworkOfferingMap.remove(networkOffering.getUuid());
            } else {
                networkOfferingService.delete(networkOffering);
                //3.2 If not found, delete it from app db
                //TODO clarify the business requirement, since it has impact in the application if it is used
                //TODO clarify is this a soft or hard delete
            }
        }
        //4. Get the remaining list of cs server hash NetworkOffering object, then iterate and
        //add it to app db
        for (String key: csNetworkOfferingMap.keySet()) {
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

        //1. Get all the network objects from CS server as hash
        List<Network> csNetworkList = networkService.findAllFromCSServer();
        HashMap<String, Network> csNetworkMap = (HashMap<String, Network>) Network.convert(csNetworkList);

        //2. Get all the network objects from application
        List<Network> appNetworkList = networkService.findAll();

        // 3. Iterate application network list
        for (Network network: appNetworkList) {
             LOGGER.debug("Total rows updated : " + String.valueOf(appNetworkList.size()));
             //3.1 Find the corresponding CS server network object by finding it in a hash using uuid
            if (csNetworkMap.containsKey(network.getUuid())) {
                Network csNetwork = csNetworkMap.get(network.getUuid());

                network.setName(csNetwork.getName());
                network.setDomainId(csNetwork.getDomainId());
                network.setNetworkOfferingId(csNetwork.getNetworkOfferingId());
                network.setZoneId(csNetwork.getZoneId());
                network.setCidr(csNetwork.getCidr());
                network.setState(csNetwork.getState());
                network.setDisplayNetwork(csNetwork.getDisplayNetwork());
                network.setDisplayText(csNetwork.getDisplayText());

                //3.2 If found, update the network object in app db
                networkService.update(network);

                //3.3 Remove once updated, so that we can have the list of cs network which is not added in the app
                csNetworkMap.remove(network.getUuid());
            } else {
                networkService.delete(network);
                //3.2 If not found, delete it from app db
                //TODO clarify the business requirement, since it has impact in the application if it is used
                //TODO clarify is this a soft or hard delete
            }
        }
        //4. Get the remaining list of cs server hash network object, then iterate and
        //add it to app db
        for (String key: csNetworkMap.keySet()) {
            LOGGER.debug("Syncservice network uuid:");
            networkService.save(csNetworkMap.get(key));
        }
        LOGGER.debug("Total rows added : " + String.valueOf(csNetworkMap.size()));

    }

    /**
     * Sync with CloudStack server Domain.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors
     */
    @Override
    public void syncComputeOffering() throws ApplicationException, Exception {

        //1. Get all the compute offering objects from CS server as hash
        List<ComputeOffering> csComputeOfferingList = computeService.findAllFromCSServer();
        HashMap<String, ComputeOffering> csComputeOfferingMap = (HashMap<String, ComputeOffering>) ComputeOffering.convert(csComputeOfferingList);

        //2. Get all the compute offering objects from application
        List<ComputeOffering> appComputeList = computeService.findAll();

        // 3. Iterate application compute offering list
        for (ComputeOffering computeOffering: appComputeList) {
             LOGGER.debug("Total rows updated : " + String.valueOf(appComputeList.size()));
            computeOffering.setIsSyncFlag(false);
             //3.1 Find the corresponding CS server compute offering object by finding it in a hash using uuid
            if (csComputeOfferingMap.containsKey(computeOffering.getUuid())) {
                ComputeOffering csComputeService = csComputeOfferingMap.get(computeOffering.getUuid());

                computeOffering.setName(csComputeService.getName());
                computeOffering.setDisplayText(csComputeService.getDisplayText());

                //3.2 If found, update the compute offering object in app db
                computeService.update(computeOffering);

                //3.3 Remove once updated, so that we can have the list of cs compute offering which is not added in the app
                csComputeOfferingMap.remove(computeOffering.getUuid());
            } else {

                computeService.delete(computeOffering);
                //3.2 If not found, delete it from app db
                //TODO clarify the business requirement, since it has impact in the application if it is used
                //TODO clarify is this a soft or hard delete
            }
        }
        //4. Get the remaining list of cs server hash domain object, then iterate and
        //add it to app db
        for (String key: csComputeOfferingMap.keySet()) {
            ComputeOffering computeOffering = new ComputeOffering();
            LOGGER.debug("Syncservice compute offering uuid:");
            computeService.save(csComputeOfferingMap.get(key));
        }
        LOGGER.debug("Total rows added : " + String.valueOf(csComputeOfferingMap.size()));
    }

    /**
     * Sync with CloudStack server template.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors
     */
    @Override
    public void syncTemplates() throws ApplicationException, Exception {

        //1. Get all the template objects from CS server as hash
        List<Template> csTemplatesList = templateService.findAllFromCSServer();
        HashMap<String, Template> csTemplateMap = (HashMap<String, Template>) Template.convert(csTemplatesList);

        //2. Get all the template objects from application
        List<Template> appTemplateList = templateService.findAll();

        // 3. Iterate application template list
        for (Template template: appTemplateList) {
             LOGGER.debug("Total rows updated : " + String.valueOf(appTemplateList.size()));
             //3.1 Find the corresponding CS server template object by finding it in a hash using uuid
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

                //3.2 If found, update the template object in app db
                templateService.update(template);

                //3.3 Remove once updated, so that we can have the list of cs template which is not added in the app
                csTemplateMap.remove(template.getUuid());
            } else {
                template.setSyncFlag(false);
                templateService.delete(template);
                //3.2 If not found, delete it from app db
                //TODO clarify the business requirement, since it has impact in the application if it is used
                //TODO clarify is this a soft or hard delete
            }
        }
        //4. Get the remaining list of cs server hash template object, then iterate and
        //add it to app db
        for (String key: csTemplateMap.keySet()) {
            templateService.save(csTemplateMap.get(key));
        }
        LOGGER.debug("Total rows added : " + String.valueOf(csTemplateMap.size()));

    }

    /**
     * Sync with Cloud Server Account.
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    private void syncDepartment() throws ApplicationException, Exception {

        //1. Get all the user objects from CS server as hash
        List<Department> csAccountService = departmentService.findAllFromCSServer();
        HashMap<String, Department> csUserMap = (HashMap<String, Department>) Department.convert(csAccountService);

        //2. Get all the user objects from application
        List<Department> appUserList = departmentService.findAll();

        // 3. Iterate application user list
        for (Department department: appUserList) {
            department.setSyncFlag(false);
             //3.1 Find the corresponding CS server user object by finding it in a hash using uuid
            if (csUserMap.containsKey(department.getUuid())) {
                Department csUser = csUserMap.get(department.getUuid());

                department.setFirstName(csUser.getFirstName());

                //3.2 If found, update the user object in app db
                departmentService.update(department);

                //3.3 Remove once updated, so that we can have the list of cs user which is not added in the app
                csUserMap.remove(department.getUuid());
            } else {
                departmentService.delete(department);
                //3.2 If not found, delete it from app db
                //TODO clarify the business requirement, since it has impact in the application if it is used
                //TODO clarify is this a soft or hard delete
             }

             }
        //4. Get the remaining list of cs server hash user object, then iterate and
        //add it to app db
        for (String key: csUserMap.keySet()) {
            departmentService.save(csUserMap.get(key));
        }
    }
}
