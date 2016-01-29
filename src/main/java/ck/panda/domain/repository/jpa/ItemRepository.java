package ck.panda.domain.repository.jpa;

import ck.panda.domain.entity.Item;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

/**
 * Jpa Repository for Item entity.
 */
@Service
public interface ItemRepository extends PagingAndSortingRepository<Item, Long> {

    /**
     * Find the items by name.
     *
     * @param itemName name of the billable item
     * @return Billable Item object
     */
    @Query(value = "SELECT bi FROM Item bi WHERE bi.name =:itemName")
    Item findByName(@Param("itemName") String itemName);

    /**
     * Find all the active or inactive items with pagination.
     *
     * @param pageable to get the list with pagination.
     * @param isActive get the billable item list based on active/inactive status.
     * @return list of items.
     */
    @Query(value = "SELECT bi FROM Item bi WHERE bi.isActive =:isActive")
    Page<Item> findAllByIsActive(Pageable pageable, @Param("isActive") Boolean isActive);

    /**
     * Find all the active or inactive items.
     *
     * @param isActive get the billable item list based on active/inactive status.
     * @return list of items.
     */
    @Query(value = "SELECT bi FROM Item bi WHERE bi.isActive =:isActive")
    List<Item> findAllByIsActive(@Param("isActive") Boolean isActive);
}
