package ck.panda.domain.repository.jpa;

import ck.panda.domain.entity.BillableItem;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     * @param itemName name of the billable item
     * @return Billable Item object
     */
    @Query(value = "SELECT bi FROM BillableItem bi WHERE bi.name =:itemName")
    BillableItem findByName(@Param("itemName") String itemName);

    /**
     * Find all the active or inactive billable items with pagination.
     *
     * @param pageable to get the list with pagination.
     * @param isActive get the billable item list based on active/inactive status.
     * @return list of billable items.
     */
    @Query(value = "SELECT bi FROM BillableItem bi WHERE bi.isActive =:isActive")
    Page<BillableItem> findAllByIsActive(Pageable pageable, @Param("isActive") Boolean isActive);

    /**
     * Find all the active or inactive billable items.
     *
     * @param isActive get the billable item list based on active/inactive status.
     * @return list of billable items.
     */
    @Query(value = "SELECT bi FROM BillableItem bi WHERE bi.isActive =:isActive")
    List<BillableItem> findAllByIsActive(@Param("isActive") Boolean isActive);
}
