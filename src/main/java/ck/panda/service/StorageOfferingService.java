package ck.panda.service;

import java.util.List;

import org.springframework.stereotype.Service;
import ck.panda.domain.entity.StorageOffering;
import ck.panda.util.domain.CRUDService;

/**
 * Service class for Storage Offering. This service provides basic CRUD and essential api's for Storage Offering related
 * business actions.
 */
@Service
public interface StorageOfferingService extends CRUDService<StorageOffering> {

    /**
     * To get list of Storage Offer from cloudstack server.
     *
     * @return os types list from server
     * @throws Exception unhandled errors.
     */
    List<StorageOffering> findAllFromCSServer() throws Exception;

    /**
     * To get Uuid list of Storage Offer from cloudstack server.
     *
     * @param uuid unique id.
     * @return storage tags
     */
    StorageOffering findUuid(String uuid);

    /**
     * To get Tags list of Storage Offer from cloudstack server.
     *
     * @param isActive unique id.
     * @return storage tags
     */
    List<String> findTags(Boolean isActive);

    /**
     * To get Tags list of Storage Offer from cloudstack server.
     *
     * @param tags tags.
     * @return storage tags
     */
    List<StorageOffering> findAllByTags(String tags);

    /**
     * Soft delete for storage offering.
     *
     * @param storage object
     * @return storage
     * @throws Exception unhandled errors.
     */
    StorageOffering softDelete(StorageOffering storage) throws Exception;

}
