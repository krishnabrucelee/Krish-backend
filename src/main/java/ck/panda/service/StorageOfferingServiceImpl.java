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
import ck.panda.domain.entity.ComputeOfferingCost;
import ck.panda.domain.entity.StorageOffering;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.StorageOfferingCost;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.Zone;
import ck.panda.domain.repository.jpa.StorageOfferingRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackOptionalUtil;
import ck.panda.util.CloudStackStorageOfferingService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;
import ck.panda.util.error.exception.EntityNotFoundException;
import ck.panda.constants.PingConstants;
import ck.panda.util.PingService;

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

    /** Storage offering cost service for reference .*/
    @Autowired
    private StorageOfferingCostService storageCostService;

    /** Mr.ping service reference. */
    @Autowired
    private PingService pingService;

    @Override
    public StorageOffering save(StorageOffering storage) throws Exception {
        if (storage.getIsSyncFlag()) {
            this.validateVolumeUniqueness(storage);
            Errors errors = validator.rejectIfNullEntity(CloudStackConstants.CS_STORAGE_OFFERING, storage);
            errors = validator.validateEntity(storage, errors);

            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else if (pingService.apiConnectionCheck(errors)) {
                createStorage(storage, errors);
                StorageOfferingCost cost = storage.getStoragePrice().get(0);
                Double totalCost = storageCostService.totalcost(cost);
                cost.setTotalCost(totalCost);
                cost.setStorageId(storage.getId());
                cost.setZoneId(cost.getZone().getId());
                StorageOffering persistStorage= storageOfferingRepo.save(storage);
                cost.setStorageId(persistStorage.getId());
                if (pingService.apiConnectionCheck(errors)) {
                    storage = storageOfferingRepo.save(persistStorage);
                    savePlanCostInPing(storage);
                }
                return storage;
            }
            return storage;
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
            } else if (pingService.apiConnectionCheck(errors)) {
                updateStorageOffering(storage, errors);
                if (pingService.apiConnectionCheck(errors)) {
                    storage = storageOfferingRepo.save(storage);
                    savePlanCostInPing(storage);
                }
                return storage;
            }
            return storage;
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
        config.setUserServer();
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
        config.setServer(1L);
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
        config.setUserServer();
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
        config.setUserServer();
        String storageOfferings = csStorageService.updateStorageOffering(String.valueOf(storage.getUuid()), CloudStackConstants.JSON,
                optional(storage));
        LOGGER.info("storage offer update response " + storageOfferings);
        JSONObject storageOfferingsResponse = new JSONObject(storageOfferings)
                .getJSONObject(CS_UPDATE_DISK_RESPONSE).getJSONObject(CloudStackConstants.CS_DISK_OFFERING);
        if (storage.getStoragePrice().size() != 0) {
            this.costCalculation(storage);
        }
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
    public List<String> findTags(Long userId, Boolean isActive) throws Exception {
        if (!convertEntityService.getOwnerById(userId).getType().equals(User.UserType.ROOT_ADMIN)) {
            return storageOfferingRepo.findTagsByDomain(convertEntityService.getOwnerById(userId).getDomainId(), isActive);
        } else {
            return storageOfferingRepo.findByTags(isActive);
        }
    }
    @Override
    public List<StorageOffering> findAllByTags(String tags, Long userId) throws Exception {
        if (tags.equals("") || tags == null) {
            tags = "ALL";
        }
        if (!convertEntityService.getOwnerById(userId).getType().equals(User.UserType.ROOT_ADMIN)) {
            return storageOfferingRepo.findAllByTags(tags, convertEntityService.getOwnerById(userId).getDomainId(), true);
        } else {
            return (List<StorageOffering>) storageOfferingRepo.findAll();
        }
    }

    @Override
    public List<StorageOffering> findByDomain(String tags, Long domainId) throws Exception {
        if (tags.equals("") || tags == null) {
            tags = "ALL";
        }
        return storageOfferingRepo.findAllByTags(tags, domainId, true);
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
        storage.setIsActive(false);
        if (storage.getIsSyncFlag()) {
        Errors errors = validator.rejectIfNullEntity(CloudStackConstants.CS_STORAGE_OFFERING, storage);
        errors = validator.validateEntity(storage, errors);
        // set server for finding value in configuration
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
            config.setUserServer();
            csStorageService.deleteStorageOffering(storage.getUuid(), CloudStackConstants.JSON);
        }
    }
    return storageOfferingRepo.save(storage);
}
    /**
     * Storage offering cost calculation for different plans.
     *
     * @param storage offering id.
     * @return storage offering.
     * @throws Exception if error occurs.
     */
    private StorageOffering costCalculation(StorageOffering storage) throws Exception {
        StorageOfferingCost cost = storage.getStoragePrice().get(0);
        Double totalCost = storageCostService.totalcost(cost);
        List<StorageOfferingCost> storageOfferingcostList = storageCostService.findByStorageId(storage.getId());
        if (storageOfferingcostList.size() != 0) {
            StorageOfferingCost persistedCost = storageOfferingcostList.get(storageOfferingcostList.size() - 1);
            if (!storage.getIsCustomDisk()) {
                int disk = Double.compare(offeringNullCheck(cost.getCostPerMonth()),offeringNullCheck(persistedCost.getCostPerMonth()));
                if (disk >0 || disk <0) {
                    this.storageCostSave(storage);
                }
            } else {
                int customdisk = Double.compare(offeringNullCheck(cost.getCostGbPerMonth()),offeringNullCheck(persistedCost.getCostGbPerMonth()));
                if (customdisk >0 || customdisk <0) {
                    this.storageCostSave(storage);
                }
            }
        } else {
            this.storageCostSave(storage);
        }
         return storage;
    }

    @Override
    public Page<StorageOffering> findAllByDomainId(Long domainId, PagingAndSorting pagingAndSorting) throws Exception {
        return storageOfferingRepo.findAllByDomainIdAndIsActive(domainId, true, pagingAndSorting.toPageRequest());
    }

    @Override
    public Page<StorageOffering> findAllByDomainIdAndSearchText(Long domainId, PagingAndSorting pagingAndSorting, String searchText) throws Exception {
        return storageOfferingRepo.findAllByDomainIdAndIsActiveAndSearchText(domainId, true, pagingAndSorting.toPageRequest(),searchText);
    }

    /**
     * Set optional value for MR.ping api call.
     *
     * @param storageOfferingCost storage offering cost
     * @return status
     * @throws Exception raise if error
     */
    public Boolean savePlanCostInPing(StorageOffering storageOfferingCost) throws Exception {
        JSONObject optional = new JSONObject();
        optional.put(PingConstants.PLAN_UUID, storageOfferingCost.getUuid());
        optional.put(PingConstants.NAME, storageOfferingCost.getName());
        optional.put(PingConstants.IS_CUSTOM, storageOfferingCost.getIsCustomDisk());
        optional.put(PingConstants.REFERENCE_NAME, PingConstants.STORAGE_OFFERING);
        optional.put(PingConstants.GROUP_NAME, PingConstants.STORAGE_OFFERING);
        if (storageOfferingCost.getStoragePrice().size() != 0) {
            if (storageOfferingCost.getIsCustomDisk()) {
                optional.put(PingConstants.TOTAL_COST, offeringNullCheck(storageOfferingCost.getStoragePrice().get(0).getCostGbPerMonth()));
            }
            else {
                optional.put(PingConstants.TOTAL_COST, offeringNullCheck(storageOfferingCost.getStoragePrice().get(0).getCostPerMonth()));
            }
              if (storageOfferingCost.getStoragePrice().get(0).getZoneId() != null) {
            Zone zone = convertEntityService.getZoneById(storageOfferingCost.getStoragePrice().get(0).getZoneId());
            optional.put(PingConstants.ZONE_ID, zone.getUuid());
              }
        }
        pingService.addPlanCost(optional);
        return true;
    }

    /**
     * Offering cost null value check.
     *
     * @param value offering cost
     * @return double value
     */
    public Double offeringNullCheck(Double value) {
        if (value == null) {
            value = 0.0;
        }
        return value;
    }

    /**
     * To save storage offering cost
     *
     * @param storage object of the storage offering
     * @return storage
     * @throws Exception if error occurs.
     */
    private StorageOffering storageCostSave(StorageOffering storage) throws Exception {
        List<StorageOfferingCost> storageCost = new ArrayList<StorageOfferingCost>();
        StorageOffering persistStorage = find(storage.getId());
        StorageOfferingCost cost = storage.getStoragePrice().get(0);
        Double totalCost = storageCostService.totalcost(cost);
        StorageOfferingCost storageOfferingcost = new StorageOfferingCost();
             storageOfferingcost.setStorageId(storage.getId());
             storageOfferingcost.setCostGbPerMonth(cost.getCostGbPerMonth());
             storageOfferingcost.setCostPerMonth(cost.getCostPerMonth());
             storageOfferingcost.setTotalCost(totalCost);
             storageOfferingcost.setZoneId(cost.getZoneId());
             storageOfferingcost = storageCostService.save(storageOfferingcost);
             storageCost.add(storageOfferingcost);
         storageCost.addAll(persistStorage.getStoragePrice());
         storage.setStoragePrice(storageCost);
        return storage;
  }
}
