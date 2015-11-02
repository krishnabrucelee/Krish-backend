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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ck.panda.domain.entity.StorageOffering;
import ck.panda.domain.repository.jpa.StorageOfferingRepository;
import ck.panda.util.AppValidator;
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

	@Override
	public StorageOffering save(StorageOffering storage) throws Exception {
		if (storage.getIsSyncFlag()) {

			Errors errors = validator.rejectIfNullEntity("storageOffering", storage);
			errors = validator.validateEntity(storage, errors);

			if (errors.hasErrors()) {
				throw new ApplicationException(errors);
			} else {
				config.setServer(1L);
				String storageOfferings = csStorageService.createStorageOffering(storage.getName(),
						storage.getDescription(), "json", optional(storage));
				LOGGER.info("storage offer create response " + storageOfferings);
				ObjectMapper mapper = new ObjectMapper();
				JsonNode json = mapper.readTree(storageOfferings);
				LOGGER.info("uuid" + json.get("creatediskofferingresponse").get("diskoffering").get("id"));

				storage.setUuid(json.get("creatediskofferingresponse").get("diskoffering").get("id").toString()
						.replace("\"", "").trim());
				if(json.get("creatediskofferingresponse").get("diskoffering").get("disksize").toString() == "0") {
					storage.setDiskSize(0L);
				}
				if(json.get("creatediskofferingresponse").get("diskoffering").get("iscustomized").toString() == "false") {
					storage.setIsCustomDisk(false);
				}

				return storageOfferingRepo.save(storage);
			}

		} else {
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
				config.setServer(1L);
				String storageOfferings = csStorageService.updateStorageOffering(String.valueOf(storage.getUuid()), "json",
						optional(storage));
				LOGGER.info("storage offer update response " + storageOfferings);

				ObjectMapper mapper = new ObjectMapper();
				JsonNode json = mapper.readTree(storageOfferings);
				LOGGER.info("uuid" + json.get("updatediskofferingresponse").get("diskoffering").get("id"));

				storage.setUuid(json.get("updatediskofferingresponse").get("diskoffering").get("id").toString()
						.replace("\"", "").trim());
				return storageOfferingRepo.save(storage);
			}
		} else {
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
		csStorageService.deleteStorageOffering(storage.getUuid(), "json");
		storageOfferingRepo.delete(id);
	}

	@Override
	@PreAuthorize("hasAuthority('ROLE_DOMAIN_USER')")
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
	 * @param storage
	 *            optional storage offering values
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
		// 1. Get the list of domains from CS server using CS connector
		String response = csStorageService.listStorageOfferings("json", storageOfferingMap);
		JSONArray storageOfferingListJSON = new JSONObject(response).getJSONObject("listdiskofferingsresponse")
				.getJSONArray("diskoffering");
		// 2. Iterate the json list, convert the single json entity to domain
		for (int i = 0, size = storageOfferingListJSON.length(); i < size; i++) {
			// 2.1 Call convert by passing JSONObject to Domain entity and Add
			// the converted Domain entity to list
			storageOfferingList.add(StorageOffering.convert(storageOfferingListJSON.getJSONObject(i)));
		}
		return storageOfferingList;
	}

}
