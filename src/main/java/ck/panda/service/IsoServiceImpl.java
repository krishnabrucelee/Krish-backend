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
import ck.panda.domain.entity.Iso;
import ck.panda.domain.entity.Zone;
import ck.panda.domain.repository.jpa.IsoRepository;
import ck.panda.util.CloudStackIsoService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Iso service implementation class.
 *
 */
@Service
public class IsoServiceImpl implements IsoService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DomainServiceImpl.class);

    /** Department repository reference. */
    @Autowired
    private IsoRepository isoRepo;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** CloudStack Domain service for connectivity with cloudstack. */
    @Autowired
    private CloudStackIsoService isoService;

    @Override
    public Iso save(Iso iso) throws Exception {
        LOGGER.debug(iso.getUuid());
        return isoRepo.save(iso);
    }

    @Override
    public Iso update(Iso iso) throws Exception {
        LOGGER.debug(iso.getUuid());
        return isoRepo.save(iso);
    }

    @Override
    public void delete(Iso iso) throws Exception {
        isoRepo.delete(iso);
    }

    @Override
    public void delete(Long id) throws Exception {
        isoRepo.delete(id);
    }

    @Override
    public Iso find(Long id) throws Exception {
        Iso domain = isoRepo.findOne(id);
        return domain;
    }

    @Override
    public Page<Iso> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return isoRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<Iso> findAll() throws Exception {
        return (List<Iso>) isoRepo.findAll();
    }

    @Override
    public Iso findbyUUID(String uuid) throws Exception {
        return isoRepo.findByUUID(uuid);
    }

    @Override
	public Iso softDelete(Iso iso) throws Exception {
    	iso.setIsActive(false);
    	iso.setIsRemoved(true);
	      return isoRepo.save(iso);
	}

    @Override
    public List<Iso> findAllFromCSServer() throws Exception {
        List<Iso> isoList = new ArrayList<Iso>();
        HashMap<String, String> isoMap = new HashMap<String, String>();
        isoMap.put("listall", "true");
        isoMap.put("isofilter", "all");
        // 1. Get the list of iso from CS server using CS connector
        String response = isoService.listIsos("json", isoMap);
        JSONArray isoListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject("listisosresponse");
        if (responseObject.has("iso")) {
            isoListJSON = responseObject.getJSONArray("iso");
            // 2. Iterate the json list, convert the single json entity to iso
            for (int i = 0, size = isoListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to iso entity and Add
                // the converted Domain entity to list
             Iso iso = Iso.convert(isoListJSON.getJSONObject(i));
             iso.setDomainId(convertEntityService.getDomainId(iso.getTransDomainId()));
             iso.setOsTypeId(convertEntityService.getOsTypeId(iso.getTransOsTypeId()));
                isoList.add(iso);
            }
        }
        return isoList;
    }

}
