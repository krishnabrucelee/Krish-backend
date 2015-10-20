package ck.panda.service;

import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.Zone;
import ck.panda.util.CloudStackDomainService;
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

    /** ZoneSerivce for listing zones. */
    @Autowired
    private ZoneService zoneService;

    /** CloudStackZoneService for zone connectivity with cloudstack. */
    @Autowired
    private CloudStackZoneService csZone;

    /**
     * Sync call for synchronization list of zone, domain, region. template, hypervisor
     * @throws Exception unhandled errors.
     */
    public void sync() throws Exception {

        //1. Sync Domain entity
        this.syncDomain();
        this.syncZone();
        //2. Sync Zone entity

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
        for (Zone zone: appZoneList) {
             //3.1 Find the corresponding CS server domain object by finding it in a hash using uuid
            if (csZoneMap.containsKey(zone.getUuid())) {
                Zone csZone = csZoneMap.get(zone.getUuid());

                zone.setName(csZone.getName());

                //3.2 If found, update the domain object in app db
                zoneService.update(zone);

                //3.3 Remove once updated, so that we can have the list of cs domain which is not added in the app
                csZoneMap.remove(zone.getUuid());
            }
            else {
                zoneService.delete(zone);
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
}

