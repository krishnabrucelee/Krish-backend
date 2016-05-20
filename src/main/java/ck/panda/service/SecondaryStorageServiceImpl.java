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
import ck.panda.domain.entity.SecondaryStorage;
import ck.panda.domain.repository.jpa.SecondaryStoageRepository;
import ck.panda.util.CloudStackImageStoreService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * SecondaryStorage service implementation class.
 *
 */
@Service
public class SecondaryStorageServiceImpl implements SecondaryStorageService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DomainServiceImpl.class);

    /** Department repository reference. */
    @Autowired
    private SecondaryStoageRepository storageRepo;

    /** CloudStack Domain service for connectivity with cloudstack. */
    @Autowired
    private CloudStackImageStoreService imageService;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** object(server) created for CloudStackServer. */
    @Autowired
    private ConfigUtil configServer;

    @Override
    public SecondaryStorage save(SecondaryStorage storage) throws Exception {
        LOGGER.debug(storage.getUuid());
        return storageRepo.save(storage);
    }

    @Override
    public SecondaryStorage update(SecondaryStorage storage) throws Exception {
        LOGGER.debug(storage.getUuid());
        return storageRepo.save(storage);
    }

    @Override
    public void delete(SecondaryStorage storage) throws Exception {
        storage.setIsActive(false);
        storageRepo.delete(storage);
    }

    @Override
    public void delete(Long id) throws Exception {
        storageRepo.delete(id);
    }

    @Override
    public SecondaryStorage find(Long id) throws Exception {
        SecondaryStorage storage = storageRepo.findOne(id);
        return storage;
    }

    @Override
    public Page<SecondaryStorage> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return storageRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<SecondaryStorage> findAll() throws Exception {
        return (List<SecondaryStorage>) storageRepo.findAll();
    }

    @Override
    public List<SecondaryStorage> findAllFromCSServer() throws Exception {
        List<SecondaryStorage> storageList = new ArrayList<SecondaryStorage>();
        HashMap<String, String> storageMap = new HashMap<String, String>();
        storageMap.put(CloudStackConstants.CS_LIST_ALL, CloudStackConstants.STATUS_ACTIVE);
        // 1. Get the list of pods from CS server using CS connector
        String response = imageService.listImageStores(CloudStackConstants.JSON, storageMap);

        JSONArray podListJSON = null;
        configServer.setServer(1L);
        JSONObject responseObject = new JSONObject(response).getJSONObject("listimagestoresresponse");
        if (responseObject.has("imagestore")) {
            podListJSON = responseObject.getJSONArray("imagestore");
            // 2. Iterate the json list, convert the single json entity to storage
            for (int i = 0, size = podListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to Domain entity and
                // Add
                // the converted storage entity to list
                SecondaryStorage storage = SecondaryStorage.convert(podListJSON.getJSONObject(i));
                storage.setZoneId(convertEntityService.getZoneId(storage.getTransZoneId()));
                storageList.add(storage);
            }
        }
        return storageList;
    }
}
