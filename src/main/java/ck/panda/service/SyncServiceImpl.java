package ck.panda.service;

import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.Region;
import ck.panda.domain.entity.Zone;
import ck.panda.util.CloudStackDomainService;
import ck.panda.util.CloudStackRegionService;
import ck.panda.util.CloudStackZoneService;
import ck.panda.util.error.exception.ApplicationException;

/**
 * We have to sync up with cloudstack server for the following data
 *
 * 1. Region
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
    private RegionService RegionService;

    /** CloudStackRegionService for Region connectivity with cloudstack. */
    @Autowired
    private CloudStackRegionService csRegion;

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
            }
            else {
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

        //1. Get all the domain objects from CS server as hash
        List<Zone> csZoneList = zoneService.findAllFromCSServer();
        HashMap<String, Zone> csZoneMap = (HashMap<String, Zone>) Zone.convert(csZoneList);

        //2. Get all the domain objects from application
        List<Zone> appZoneList = zoneService.findAll();

        // 3. Iterate application domain list
        for (Zone Zone: appZoneList) {
             //3.1 Find the corresponding CS server domain object by finding it in a hash using uuid
            if (csZoneMap.containsKey(Zone.getUuid())) {
                Zone csZone = csZoneMap.get(Zone.getUuid());

                Zone.setName(csZone.getName());

                //3.2 If found, update the domain object in app db
                zoneService.update(Zone);

                //3.3 Remove once updated, so that we can have the list of cs domain which is not added in the app
                csZoneMap.remove(Zone.getUuid());
            }
            else {
                zoneService.delete(Zone);
                //3.2 If not found, delete it from app db
                //TODO clarify the business requirement, since it has impact in the application if it is used
                //TODO clarify is this a soft or hard delete
            }
        }
        //4. Get the remaining list of cs server hash domain object, then iterate and
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

        //1. Get all the domain objects from CS server as hash
        List<Region> csRegionList = RegionService.findAllFromCSServer();
        HashMap<String, Region> csRegionMap = (HashMap<String, Region>) Region.convert(csRegionList);

        //2. Get all the domain objects from application
        List<Region> appRegionList = RegionService.findAll();

        // 3. Iterate application domain list
        for (Region Region: appRegionList) {
             //3.1 Find the corresponding CS server domain object by finding it in a hash using uuid
            if (csRegionMap.containsKey(Region.getUuid())) {
                Region csRegion = csRegionMap.get(Region.getName());

                Region.setName(csRegion.getName());
                Region.setEndPoint(csRegion.getEndPoint());

                //3.2 If found, update the domain object in app db
                RegionService.update(Region);

                //3.3 Remove once updated, so that we can have the list of cs domain which is not added in the app
                csRegionMap.remove(Region.getName());
            }
            else {
                RegionService.delete(Region);
                //3.2 If not found, delete it from app db
                //TODO clarify the business requirement, since it has impact in the application if it is used
                //TODO clarify is this a soft or hard delete
            }
        }
        //4. Get the remaining list of cs server hash domain object, then iterate and
        //add it to app db
        for (String key: csRegionMap.keySet()) {
            RegionService.save(csRegionMap.get(key));
        }
    }
}

