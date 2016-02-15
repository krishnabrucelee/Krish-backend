package ck.panda.service;

import org.springframework.stereotype.Service;
import ck.panda.domain.entity.StorageOfferingCost;
import ck.panda.util.domain.CRUDService;

/**
 * Service class for Storage Offering cost.
 *
 */
@Service
public interface StorageOfferingCostService extends CRUDService<StorageOfferingCost> {

    /**
     * Find Storage offering cost by passing total cost value and storage offering id.
     *
     * @param storageId of the offering.
     * @param totalCost for the plan
     * @return storage offering cost.
     */
    StorageOfferingCost findByCostAndId(Long storageId, Double totalCost);

    /**
     * Calculate total cost of the storage offering.
     *
     * @param storagecost for the particular offering
     * @return storage offering cost.
     * @throws Exception if error occurs.
     */
    Double totalcost(StorageOfferingCost storagecost) throws Exception;
}
