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
import ck.panda.domain.entity.StorageOffering;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.repository.jpa.StorageOfferingRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackOptionalUtil;
import ck.panda.util.CloudStackStorageOfferingService;
import ck.panda.util.ConfigUtil;
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

    /** Constant for list disk offering response. */
    public static final String CS_LIST_DISK_RESPONSE = "listdiskofferingsresponse";

    /** Constant for create disk offering response. */
    public static final String CS_CREATE_DISK_RESPONSE = "creatediskofferingresponse";

    /** Constant for update disk offering response. */
    public static final String CS_UPDATE_DISK_RESPONSE = "updatediskofferingresponse";

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

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** Virtual Machine service reference. */
    @Autowired
    private VirtualMachineService vmService;

    @Override
    public StorageOffering save(StorageOffering storage) throws Exception {
        if (storage.getIsSyncFlag()) {
            this.validateVolumeUniqueness(storage);
            Errors errors = validator.rejectIfNullEntity(CloudStackConstants.CS_STORAGE_OFFERING, storage);
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
            this.validateVolumeUniqueness(storage);
            Errors errors = validator.rejectIfNullEntity(CloudStackConstants.CS_STORAGE_OFFERING, storage);
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
        csStorageService.deleteStorageOffering(storage.getUuid(), CloudStackConstants.JSON);
        storageOfferingRepo.delete(id);
    }

    @Override
    public StorageOffering find(Long id) throws Exception {
        StorageOffering storageOffering = storageOfferingRepo.findOne(id);
        if (storageOffering == null) {
            throw new EntityNotFoundException("error.storageOffering.not.found");
        }
        return storageOffering;
    }

    @Override
    public Page<StorageOffering> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return storageOfferingRepo.findAllByActive(pagingAndSorting.toPageRequest(), true);
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
     * @throws Exception error at optional values
     */
    public HashMap<String, String> optional(StorageOffering storage) throws Exception {
        HashMap<String, String> stoarageMap = new HashMap<String, String>();
        CloudStackOptionalUtil.updateOptionalStringValue(CloudStackConstants.CS_TAGS, storage.getStorageTags(),
                stoarageMap);
        CloudStackOptionalUtil.updateOptionalStringValue(CloudStackConstants.CS_NAME, storage.getName(), stoarageMap);
        CloudStackOptionalUtil.updateOptionalStringValue(CloudStackConstants.CS_DISPLAY_TEXT, storage.getDescription(),
                stoarageMap);
        CloudStackOptionalUtil.updateOptionalBooleanValue(CloudStackConstants.CS_PUBLIC, storage.getIsPublic(),
                stoarageMap);
        CloudStackOptionalUtil.updateOptionalBooleanValue(CloudStackConstants.CS_CUSTOM_OFFER,
                storage.getIsCustomDisk(), stoarageMap);
        CloudStackOptionalUtil.updateOptionalStringValue(CloudStackConstants.CS_STORAGE_TYPE,
                storage.getStorageType().toString(), stoarageMap);
        CloudStackOptionalUtil.updateOptionalBooleanValue(CloudStackConstants.CS_CUSTOM_IOPS,
                storage.getIsCustomizedIops(), stoarageMap);
        CloudStackOptionalUtil.updateOptionalLongValue(CloudStackConstants.CS_DISK_SIZE,
                storage.getDiskSize(), stoarageMap);
        CloudStackOptionalUtil.updateOptionalLongValue(CloudStackConstants.CS_IOPS_READ,
                storage.getDiskIopsReadRate(), stoarageMap);
        CloudStackOptionalUtil.updateOptionalLongValue(CloudStackConstants.CS_IOPS_WRITE,
                storage.getDiskIopsWriteRate(), stoarageMap);
        CloudStackOptionalUtil.updateOptionalLongValue(CloudStackConstants.CS_BYTES_READ,
                storage.getDiskBytesReadRate(), stoarageMap);
        CloudStackOptionalUtil.updateOptionalLongValue(CloudStackConstants.CS_BYTES_WRITE,
                storage.getDiskBytesWriteRate(), stoarageMap);
        CloudStackOptionalUtil.updateOptionalLongValue(CloudStackConstants.CS_MIN_IOPS,
                storage.getDiskMinIops(), stoarageMap);
        CloudStackOptionalUtil.updateOptionalLongValue(CloudStackConstants.CS_MAX_IOPS,
                storage.getDiskMaxIops(), stoarageMap);
        if (storage.getDomainId() != null) {
            CloudStackOptionalUtil.updateOptionalStringValue(CloudStackConstants.CS_DOMAIN_ID,
                    convertEntityService.getDomainById(storage.getDomainId()).getUuid(), stoarageMap);
        }
        return stoarageMap;
    }

    @Override
    public List<StorageOffering> findAllFromCSServer() throws Exception {
        List<StorageOffering> storageOfferingList = new ArrayList<StorageOffering>();
        HashMap<String, String> storageOfferingMap = new HashMap<String, String>();
        storageOfferingMap.put(CloudStackConstants.CS_LIST_ALL, CloudStackConstants.STATUS_ACTIVE);
        // 1. Get the list of StorageOffering from CS server using CS connector
        String response = csStorageService.listStorageOfferings(CloudStackConstants.JSON, storageOfferingMap);
        JSONArray storageOfferingListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject(CS_LIST_DISK_RESPONSE);
        if (responseObject.has(CloudStackConstants.CS_DISK_OFFERING)) {
            storageOfferingListJSON = responseObject.getJSONArray(CloudStackConstants.CS_DISK_OFFERING);
            // 2. Iterate the json list, convert the single json entity to
            // StorageOffering
            for (int i = 0, size = storageOfferingListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to StorageOffering
                // entity
                // and Add
                // the converted StorageOffering entity to list
                storageOfferingList.add(StorageOffering.convert(storageOfferingListJSON.getJSONObject(i)));
            }
        }
        return storageOfferingList;
    }

    /**
     * Cloud stack create storage offering.
     *
     * @param storage Storage offering
     * @param errors global error and field errors
     * @throws Exception error at storage creation
     */
    private void createStorage(StorageOffering storage, Errors errors) throws Exception {
        config.setServer(1L);
        String storageOfferings = csStorageService.createStorageOffering(CloudStackConstants.JSON, optional(storage));
        LOGGER.info("storage offer create response " + storageOfferings);
        JSONObject storageOfferingsResponse = new JSONObject(storageOfferings)
                .getJSONObject(CS_CREATE_DISK_RESPONSE);
        if (storageOfferingsResponse.has(CloudStackConstants.CS_ERROR_CODE)) {
            errors = this.validateEvent(errors, storageOfferingsResponse.getString(CloudStackConstants.CS_ERROR_TEXT));
            throw new ApplicationException(errors);
        } else {
            storage.setUuid(storageOfferingsResponse.getJSONObject(CloudStackConstants.CS_DISK_OFFERING).getString(CloudStackConstants.CS_ID));
            if (storageOfferingsResponse.getJSONObject(CloudStackConstants.CS_DISK_OFFERING).get(CloudStackConstants.CS_DISK_SIZE).equals(0)) {
                storage.setDiskSize(0L);
            }
            if (storageOfferingsResponse.getJSONObject(CloudStackConstants.CS_DISK_OFFERING).get(CloudStackConstants.CS_CUSTOM_STATUS).equals(false)) {
                storage.setIsCustomDisk(false);
            }
        }
    }

    /**
     * Cloud stack update storage offering.
     *
     * @param storage Storage offering
     * @param errors global error and field errors
     * @throws Exception error at update storage
     */
    private void updateStorageOffering(StorageOffering storage, Errors errors) throws Exception {
        config.setServer(1L);
        String storageOfferings = csStorageService.updateStorageOffering(String.valueOf(storage.getUuid()), CloudStackConstants.JSON,
                optional(storage));
        LOGGER.info("storage offer update response " + storageOfferings);
        JSONObject storageOfferingsResponse = new JSONObject(storageOfferings)
                .getJSONObject(CS_UPDATE_DISK_RESPONSE).getJSONObject(CloudStackConstants.CS_DISK_OFFERING);
        if (storageOfferingsResponse.has(CloudStackConstants.CS_ERROR_CODE)) {
            errors = this.validateEvent(errors, storageOfferingsResponse.getString(CloudStackConstants.CS_ERROR_TEXT));
            throw new ApplicationException(errors);
        } else {
            storage.setUuid((String) storageOfferingsResponse.get(CloudStackConstants.CS_ID));
        }
    }

    /**
     * Check the Storage offering CS error handling.
     *
     * @param errors error creating status.
     * @param errmessage error message.
     * @return errors.
     * @throws Exception error at validation
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
        return storageOfferingRepo.findAllByTags(tags, true);
    }

    /**
     * Validate the StorageOffering.
     *
     * @param storage reference of the Volume.
     * @throws Exception error occurs
     */
    private void validateVolumeUniqueness(StorageOffering storage) throws Exception {
        Errors errors = validator.rejectIfNullEntity(CloudStackConstants.CS_STORAGE_OFFERING, storage);
        errors = validator.validateEntity(storage, errors);
        StorageOffering validateStorage = storageOfferingRepo.findByNameAndIsActive(storage.getName(), true);
        if (validateStorage != null && storage.getId() != validateStorage.getId()) {
            errors.addGlobalError("error.storage.offering.already.exist");
        }
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        }
    }

    @Override
    public StorageOffering softDelete(StorageOffering storage) throws Exception {
    if (storage.getIsSyncFlag()) {
        Errors errors = validator.rejectIfNullEntity(CloudStackConstants.CS_STORAGE_OFFERING, storage);
        errors = validator.validateEntity(storage, errors);
        // set server for finding value in configuration
        config.setUserServer();
        List<VmInstance> vmResponse = vmService.findAllByStorageOfferingIdAndVmStatus(storage.getId(),
                VmInstance.Status.EXPUNGING);
        if (vmResponse.size() != 0) {
            errors.addGlobalError("plan.delete.confirmation");
        }
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            storage.setIsActive(false);
            // update compute offering in ACS.
            csStorageService.deleteStorageOffering(storage.getUuid(), CloudStackConstants.JSON);
        }
    }
    return storageOfferingRepo.save(storage);
}
}
