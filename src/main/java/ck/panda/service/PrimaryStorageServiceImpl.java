package ck.panda.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.constants.CloudStackConstants;
import ck.panda.domain.entity.PrimaryStorage;
import ck.panda.domain.repository.jpa.PrimaryStorageRepository;
import ck.panda.domain.repository.jpa.SecondaryStoageRepository;
import ck.panda.util.CloudStackImageStoreService;
import ck.panda.util.CloudStackPoolService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * PrimaryStorage service implementation class.
 *
 */
@Service
public class PrimaryStorageServiceImpl implements PrimaryStorageService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DomainServiceImpl.class);

    /** Department repository reference. */
    @Autowired
    private PrimaryStorageRepository storageRepo;

    /** CloudStack Domain service for connectivity with cloudstack. */
    @Autowired
    private CloudStackPoolService poolService;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** object(server) created for CloudStackServer. */
    @Autowired
    private ConfigUtil configServer;

    @Override
    public PrimaryStorage save(PrimaryStorage storage) throws Exception {
        LOGGER.debug(storage.getUuid());
        return storageRepo.save(storage);
    }

    @Override
    public PrimaryStorage update(PrimaryStorage storage) throws Exception {
        LOGGER.debug(storage.getUuid());
        return storageRepo.save(storage);
    }

    @Override
    public void delete(PrimaryStorage storage) throws Exception {
        storage.setIsActive(false);
        storageRepo.delete(storage);
    }

    @Override
    public void delete(Long id) throws Exception {
        storageRepo.delete(id);
    }

    @Override
    public PrimaryStorage find(Long id) throws Exception {
        PrimaryStorage storage = storageRepo.findOne(id);
        return storage;
    }

    @Override
    public Page<PrimaryStorage> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return storageRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<PrimaryStorage> findAll() throws Exception {
        return (List<PrimaryStorage>) storageRepo.findAll();
    }

    @Override
    public List<PrimaryStorage> findAllFromCSServer() throws Exception {
        List<PrimaryStorage> storageList = new ArrayList<PrimaryStorage>();
        HashMap<String, String> storageMap = new HashMap<String, String>();
        storageMap.put(CloudStackConstants.CS_LIST_ALL, CloudStackConstants.STATUS_ACTIVE);
        // 1. Get the list of pods from CS server using CS connector
        String response = poolService.listStoragePools(CloudStackConstants.JSON, storageMap);

        JSONArray podListJSON = null;
        configServer.setServer(1L);
        JSONObject responseObject = new JSONObject(response).getJSONObject("liststoragepoolsresponse");
        if (responseObject.has("storagepool")) {
            podListJSON = responseObject.getJSONArray("storagepool");
            // 2. Iterate the json list, convert the single json entity to storage
            for (int i = 0, size = podListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to Domain entity and
                // Add
                // the converted storage entity to list
                PrimaryStorage storage = PrimaryStorage.convert(podListJSON.getJSONObject(i));
                storage.setZoneId(convertEntityService.getZoneId(storage.getTransZoneId()));
                if(storage.getTransClusterId() != null) {
                   storage.setClusterId(convertEntityService.getClusterId(storage.getTransClusterId()));
                }
                storageList.add(storage);
            }
        }
        return storageList;
    }
}
