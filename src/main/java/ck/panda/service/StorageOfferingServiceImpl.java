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
import ck.panda.domain.entity.StorageOffering;
import ck.panda.domain.repository.jpa.StorageOfferingRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackStorageOfferingService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.ConvertUtil;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;
import ck.panda.util.error.exception.EntityNotFoundException;

/**
 * Storage Offering service implementation class.
 */
@Service
public class StorageOfferingServiceImpl implements StorageOfferingService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageOfferingServiceImpl.class);

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** StorageOffering repository reference. */
    @Autowired
    private StorageOfferingRepository storageOfferingRepo;

    /** Lists types of operating systems in cloudstack server. */
    @Autowired
    private CloudStackStorageOfferingService csStorageService;

    /** Cloud stack configuration utility class. */
    @Autowired
    private ConfigUtil config;

    /** Conversation  utility class. */
    @Autowired
    private ConvertUtil convertUtil;

    /** Json response for storage offering. */
    private static final String JSON = "json";

    /** Disk offering response field from cloud stack. */
    private static final String DISK = "diskoffering";

    @Override
    public StorageOffering save(StorageOffering storage) throws Exception {
        if (storage.getIsSyncFlag()) {

            Errors errors = validator.rejectIfNullEntity("storageOffering", storage);
            errors = validator.validateEntity(storage, errors);

            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                createStorage(storage, errors);
                return storageOfferingRepo.save(storage);
            }
        } else {
             LOGGER.debug(storage.getUuid());
            return storageOfferingRepo.save(storage);
        }

    }

    @Override
    public StorageOffering update(StorageOffering storage) throws Exception {
        if (storage.getIsSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity("storageOffering", storage);
            errors = validator.validateEntity(storage, errors);

            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                updateStorageOffering(storage, errors);
                return storageOfferingRepo.save(storage);
            }
        } else {
            LOGGER.debug(storage.getUuid());
            return storageOfferingRepo.save(storage);
        }
    }

    @Override
    public void delete(StorageOffering storage) throws Exception {
        storageOfferingRepo.delete(storage);
    }

    @Override
    public void delete(Long id) throws Exception {
        StorageOffering storage = this.find(id);
        // set server for finding value in configuration
        config.setServer(1L);
        csStorageService.deleteStorageOffering(storage.getUuid(), JSON);
        storageOfferingRepo.delete(id);
    }

    @Override
    public StorageOffering find(Long id) throws Exception {
        StorageOffering storageOffering = storageOfferingRepo.findOne(id);

        LOGGER.debug("Sample Debug Message");
        LOGGER.trace("Sample Trace Message");

        if (storageOffering == null) {
            throw new EntityNotFoundException("StorageOffering.not.found");
        }
        return storageOffering;
    }

    @Override
    public Page<StorageOffering> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return storageOfferingRepo.findAllByActive(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<StorageOffering> findAll() throws Exception {
        return (List<StorageOffering>) storageOfferingRepo.findAll();
    }

    /**
     * To set optional values by validating null and empty parameters.
     *
     * @param storage optional storage offering values
     * @return optional values
     */
    public HashMap<String, String> optional(StorageOffering storage) {
        HashMap<String, String> optional = new HashMap<String, String>();

        if (storage.getStorageTags() != null) {
            optional.put("tags", storage.getStorageTags().toString());
        }

        if (storage.getIsPublic() != null) {
            optional.put("public", storage.getIsPublic().toString());
        }

        if (storage.getIsCustomDisk() != null) {
            optional.put("customized", storage.getIsCustomDisk().toString());
        }

        if (storage.getStorageType() != null) {
            optional.put("storagetype", storage.getStorageType().toString());
        }

        if (storage.getIsCustomizedIops() != null) {
            optional.put("customizediops", storage.getIsCustomizedIops().toString());
        }

        if (storage.getStorageTags() != null) {
            optional.put("tags", storage.getStorageTags().toString());
        }

        if (storage.getDiskSize() != null) {
            optional.put("disksize", storage.getDiskSize().toString());
        }

        if (storage.getDiskBytesReadRate() != null) {
            optional.put("bytesreadrate", storage.getDiskBytesReadRate().toString());
        }

        if (storage.getDiskBytesWriteRate() != null) {
            optional.put("byteswriterate", storage.getDiskBytesWriteRate().toString());
        }

        if (storage.getDiskIopsReadRate() != null) {
            optional.put("iopsreadrate", storage.getDiskIopsReadRate().toString());
        }

        if (storage.getDiskIopsWriteRate() != null) {
            optional.put("iopswriterate", storage.getDiskIopsWriteRate().toString());
        }

        if (storage.getDiskMaxIops() != null) {
            optional.put("maxiops", storage.getDiskMaxIops().toString());
        }

        if (storage.getDiskMinIops() != null) {
            optional.put("miniops", storage.getDiskMinIops().toString());
        }
        return optional;
    }

    @Override
    public List<StorageOffering> findAllFromCSServer() throws Exception {
        List<StorageOffering> storageOfferingList = new ArrayList<StorageOffering>();
        HashMap<String, String> storageOfferingMap = new HashMap<String, String>();
        storageOfferingMap.put("listall", "true");
        // 1. Get the list of StorageOffering from CS server using CS connector
        String response = csStorageService.listStorageOfferings(JSON, storageOfferingMap);
        JSONArray storageOfferingListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject("listdiskofferingsresponse");
        if (responseObject.has(DISK)) {
            storageOfferingListJSON = responseObject.getJSONArray(DISK);
            // 2. Iterate the json list, convert the single json entity to
            // StorageOffering
            for (int i = 0, size = storageOfferingListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to StorageOffering
                // entity
                // and Add
                // the converted StorageOffering entity to list
                storageOfferingList.add(StorageOffering.convert(storageOfferingListJSON.getJSONObject(i), convertUtil));
            }
        }
        return storageOfferingList;
    }

    /**
     * Cloud stack create storage offering.
     *
     * @param storage Storage offering
     * @param errors global error and field errors
     * @throws Exception error
     */
    private void createStorage(StorageOffering storage, Errors errors) throws Exception {
        config.setServer(1L);
        String storageOfferings = csStorageService.createStorageOffering(storage.getName(),
                storage.getDescription(), JSON, optional(storage));
        LOGGER.info("storage offer create response " + storageOfferings);
        JSONObject storageOfferingsResponse = new JSONObject(storageOfferings).getJSONObject("creatediskofferingresponse")
                .getJSONObject("diskoffering");
        if (storageOfferingsResponse.has("errorcode")) {
            errors = this.validateEvent(errors, storageOfferingsResponse.getString("errortext"));
            throw new ApplicationException(errors);
        } else {
            storage.setUuid((String) storageOfferingsResponse.get("id"));

            if (storageOfferingsResponse.get("disksize").equals(0)) {
                storage.setDiskSize(0L);
            }
            if (storageOfferingsResponse.get("iscustomized").equals(false)) {
                storage.setIsCustomDisk(false);
            }
        }
    }

    /**
     * Cloud stack update storage offering.
     *
     * @param storage Storage offering
     * @param errors global error and field errors
     * @throws Exception error
     */
    private void updateStorageOffering(StorageOffering storage, Errors errors) throws Exception {
      config.setServer(1L);
      String storageOfferings = csStorageService.updateStorageOffering(String.valueOf(storage.getUuid()),
              JSON, optional(storage));
      LOGGER.info("storage offer update response " + storageOfferings);
      JSONObject storageOfferingsResponse = new JSONObject(storageOfferings).getJSONObject("updatediskofferingresponse")
              .getJSONObject("diskoffering");
      if (storageOfferingsResponse.has("errorcode")) {
          errors = this.validateEvent(errors, storageOfferingsResponse.getString("errortext"));
          throw new ApplicationException(errors);
      } else {
          storage.setUuid((String) storageOfferingsResponse.get("id"));
          }
    }

    /**
     * Check the Storage offering CS error handling.
     *
     * @param errors error creating status.
     * @param errmessage error message.
     * @return errors.
     * @throws Exception error
     */
    private Errors validateEvent(Errors errors, String errmessage) throws Exception {
        errors.addGlobalError(errmessage);
        return errors;
    }

    @Override
    public StorageOffering findUuid(String uuid) {
        return storageOfferingRepo.findByUUID(uuid);
    }

    @Override
    public List<String> findTags(Boolean isActive) {
        return storageOfferingRepo.findByTags(isActive);

    }

    @Override
    public List<StorageOffering> findAllByTags(String tags) {
        if (tags.equals("") || tags == null) {
            tags = "ALL";
        }
        return storageOfferingRepo.findAllByTags(tags);
    }
}
