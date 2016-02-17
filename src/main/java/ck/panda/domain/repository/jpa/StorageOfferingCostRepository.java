package ck.panda.domain.repository.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.StorageOfferingCost;

/**
 * StorageOfferingRepository interface that extends PagingAndSortingRepository along with sorting and pagination.
 */
public interface StorageOfferingCostRepository extends PagingAndSortingRepository<StorageOfferingCost, Long> {

    /**
     * Find Storage offering cost by total cost value and storage offering id.
     *
     * @param storageId storage offering id.
     * @param totalCost of the storage offering.
     * @return storage offering cost.
     */
    @Query(value = "SELECT cost FROM StorageOfferingCost cost WHERE cost.storageId = :storageId AND cost.totalCost = :totalcost")
    StorageOfferingCost findByStorageAndTotalCost(@Param("storageId") Long storageId,
            @Param("totalcost") Double totalCost);
}
