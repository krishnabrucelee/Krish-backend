package ck.panda.service;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Zone;
import ck.panda.domain.repository.jpa.ZoneRepository;
import ck.panda.util.CloudStackZoneService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Service implementation for Zone entity.
 *
 */
@Service
public class ZoneServiceImpl implements ZoneService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ZoneServiceImpl.class);

    /** Zone repository reference. */
    @Autowired
    private ZoneRepository zoneRepo;

    /** CloudStack Domain service for connectivity with cloudstack. */
    @Autowired
    private CloudStackZoneService zoneService;

    @Override
    public Zone save(Zone zone) throws Exception {
        LOGGER.debug(zone.getUuid());
        return zoneRepo.save(zone);
    }

    @Override
    public Zone update(Zone zone) throws Exception {
         LOGGER.debug(zone.getUuid());
         return zoneRepo.save(zone);
    }

    @Override
    public void delete(Zone zone) throws Exception {
        zoneRepo.delete(zone);

    }

    @Override
    public void delete(Long id) throws Exception {
        zoneRepo.delete(id);
    }

    @Override
    public Zone find(Long id) throws Exception {
        return zoneRepo.findOne(id);
    }

    @Override
    public List<Zone> findAll() throws Exception {
        return (List<Zone>) zoneRepo.findAll();
    }

    @Override
    public Page<Zone> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return zoneRepo.findAll(pagingAndSorting.toPageRequest());
    }

    /**
     * Find all the Zone objects from the cloud stack server.
     *
     * @return Zone list.
     * @throws Exception unhadled errors
     */
    public List<Zone> findAllFromCSServer() throws Exception {
        List<Zone> zoneList = new ArrayList<Zone>();
        HashMap<String, String> zoneMap = new HashMap<String, String>();
        zoneMap.put("available", "true");
        // 1. Get the list of Zones from CS server using CS connector
        String response = zoneService.listZones(zoneMap, "json");
        JSONArray zoneListJSON = new JSONObject(response).getJSONObject("listzonesresponse")
                .getJSONArray("zone");

        // 2. Iterate the json list, convert the single json entity to Zone
        for (int i = 0, size = zoneListJSON.length(); i < size; i++) {
            // 2.1 Call convert by passing JSONObject to Zone entity and Add
            // the converted Zone entity to list
            zoneList.add(Zone.convert(zoneListJSON.getJSONObject(i)));
        }
        return zoneList;
    }

    @Override
    public Zone findByUUID(String uuid) throws Exception {
       return zoneRepo.findByUUID(uuid);
    }

    @Override
    public Zone softDelete(Zone zone) throws Exception {
            zone.setIsActive(false);
            zone.setStatus(Zone.Status.DISABLED);
        return zoneRepo.save(zone);
    }

	@Override
	public Zone findById(Long id) throws Exception {
		return zoneRepo.findById(id);
	}

}
