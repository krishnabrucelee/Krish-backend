package ck.panda.domain.repository.jpa;

import ck.panda.domain.entity.BillableItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

/**
 * Jpa Repository for BillableItem entity.
 */
@Service
public interface BillableItemRepository extends PagingAndSortingRepository<BillableItem, Long> {

    /**
     * Find the billable items by name.
     *
     * @param billable item name for login check
     * @return Billable Item object
     */
    @Query(value = "select bi from BillableItem bi where bi.name =:itemName")
    BillableItem findByName(@Param("itemName") String itemName);
}
