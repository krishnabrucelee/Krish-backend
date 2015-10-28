package ck.panda.service;

import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.Hypervisor;
import ck.panda.domain.entity.OsCategory;
import ck.panda.domain.entity.OsType;
import ck.panda.domain.entity.Region;
import ck.panda.domain.entity.StorageOffering;
import ck.panda.domain.entity.Zone;
import ck.panda.util.CloudStackDomainService;
import ck.panda.util.CloudStackHypervisorsService;
import ck.panda.util.CloudStackOSService;
import ck.panda.util.CloudStackRegionService;
import ck.panda.util.CloudStackZoneService;
import ck.panda.util.error.exception.ApplicationException;

/**
 * We have to sync up with cloudstack server for the following data
 *
 * 1. Zone
 * 2. Domain
 * 3. Region
 * 4. Template
 * 5. Hypervisor
 * 6. OS Catogory
 * 7. OS Type
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
    private static final Logger LOGGER = LoggerFactory.getLogger(DomainServiceImpl.class);

    /** CloudStackDomainService for domain connectivity with cloudstack. */
    @Autowired
    private CloudStackDomainService csdomain;

    /** RegionSerivce for listing Regions. */
    @Autowired
    private ZoneService zoneService;

    /** CloudStackRegionService for Region connectivity with cloudstack. */

    @Autowired
    private CloudStackZoneService csZone;

    /** RegionSerivce for listing Regions. */
    @Autowired
    private RegionService regionService;

    /** CloudStackRegionService for Region connectivity with cloudstack. */
    @Autowired
    private CloudStackRegionService csRegion;

    /** RegionSerivce for listing Regions. */
    @Autowired
    private HypervisorService hypervisorService;

    /** CloudStackRegionService for Region connectivity with cloudstack. */
    @Autowired
    private CloudStackHypervisorsService csHypervisor;

    /** OSCategoryService for listing operating sytem in cloudstack server. */
    @Autowired
    private OsCategoryService osCategoryService;

    /** CloudStackRegionService for Region connectivity with cloudstack. */
    @Autowired
    private CloudStackOSService csOsCategory;

    /** OSCategoryService for listing operating sytem in cloudstack server. */
    @Autowired
    private OsTypeService osTypeService;

    @Autowired
    private StorageOfferingService storageService;

    /**
     * Sync call for synchronization list of Region, domain, region. template, hypervisor
     * @throws Exception unhandled errors.
     */
    public void sync() throws Exception {

        //1. Sync Domain entity
        this.syncDomain();

        //2. Sync Region entity
        this.syncRegion();

        //3. Sync Zone entity
        this.syncZone();

        //4. Sync Hypervisor entity
        this.syncHypervisor();

        //5. Sync OSCategory entity
        this.syncOsCategory();

        //6. Sync OSType entity
        this.syncOsTypes();

        this.syncStorageOffering();

    }

    /**
     * Sync with CloudStack server Domain.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors
     */
    private void syncDomain() throws ApplicationException, Exception {

        //1. Get all the domain objects from CS server as hash
        List<Domain> csDomainList = domainService.findAllFromCSServer();
        HashMap<String, Domain> csDomainMap = (HashMap<String, Domain>) Domain.convert(csDomainList);

        //2. Get all the domain objects from application
        List<Domain> appDomainList = domainService.findAll();

        // 3. Iterate application domain list
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
            domainService.save(csDomainMap.get(key));
        }
    }

    /**
     * Sync with Cloud Server Zone.
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    private void syncZone() throws ApplicationException, Exception {

        //1. Get all the zone objects from CS server as hash
        List<Zone> csZoneList = zoneService.findAllFromCSServer();
        HashMap<String, Zone> csZoneMap = (HashMap<String, Zone>) Zone.convert(csZoneList);

        //2. Get all the zone objects from application
        List<Zone> appZoneList = zoneService.findAll();

        // 3. Iterate application zone list
        for (Zone zone: appZoneList) {
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
            zoneService.save(csZoneMap.get(key));
        }
    }

    /**
     * Sync with Cloud Server Region.
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    private void syncRegion() throws ApplicationException, Exception {

        //1. Get all the region objects from CS server as hash
        List<Region> csRegionList = regionService.findAllFromCSServer();
        HashMap<String, Region> csRegionMap = (HashMap<String, Region>) Region.convert(csRegionList);

        //2. Get all the region objects from application
        List<Region> appRegionList = regionService.findAll();

        // 3. Iterate application region list
        for (Region region: appRegionList) {
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
            regionService.save(csRegionMap.get(key));
        }
    }

    /**
     * Sync with Cloud Server Region.
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    private void syncHypervisor() throws ApplicationException, Exception {

        //1. Get all the hypervisor objects from CS server as hash
        List<Hypervisor> csHypervisorList = hypervisorService.findAllFromCSServer();
        HashMap<String, Hypervisor> csHypervisorMap = (HashMap<String, Hypervisor>) Hypervisor.convert(csHypervisorList);

        //2. Get all the hypervisor objects from application
        List<Hypervisor> appHypervisorList = hypervisorService.findAll();

        // 3. Iterate application hypervisor list
        for (Hypervisor hypervisor: appHypervisorList) {

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
            hypervisorService.save(csHypervisorMap.get(key));
        }
    }

    /**
     * Sync with Cloud Server Region.
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    private void syncOsCategory() throws ApplicationException, Exception {

        //1. Get all the oscategory objects from CS server as hash
        List<OsCategory> csOsCategoryList = osCategoryService.findAllFromCSServer();
        HashMap<String, OsCategory> csOsCategoryMap = (HashMap<String, OsCategory>) OsCategory.convert(csOsCategoryList);

        //2. Get all the oscategory objects from application
        List<OsCategory> appOsCategoryList = osCategoryService.findAll();

        // 3. Iterate application oscategory list
        for (OsCategory osCategory: appOsCategoryList) {

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
            osCategoryService.save(csOsCategoryMap.get(key));
        }
    }

    /**
     * Sync with CloudStack server osType.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors
     */
    private void syncOsTypes() throws ApplicationException, Exception {

        //1. Get all the osType objects from CS server as hash
        List<OsType> csOsTypesList = osTypeService.findAllFromCSServer();
        HashMap<String, OsType> csOsTypeMap = (HashMap<String, OsType>) OsType.convert(csOsTypesList);

        //2. Get all the osType objects from application
        List<OsType> appOsTypeList = osTypeService.findAll();

        // 3. Iterate application osType list
        for (OsType osType: appOsTypeList) {
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
            osTypeService.save(csOsTypeMap.get(key));
        }
    }

    /**
     * Sync with CloudStack server osType.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors
     */
    private void syncStorageOffering() throws ApplicationException, Exception {

        //1. Get all the StorageOffering objects from CS server as hash
        List<StorageOffering> csStorageOfferingsList = storageService.findAllFromCSServer();
        HashMap<String, StorageOffering> csStorageOfferingMap = (HashMap<String, StorageOffering>) StorageOffering.convert(csStorageOfferingsList);

        //2. Get all the osType objects from application
        List<StorageOffering> appstorageServiceList = storageService.findAll();

        // 3. Iterate application osType list
        for (StorageOffering storageOffering: appstorageServiceList) {
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

            storageService.save(csStorageOfferingMap.get(key));
        }
    }
}

