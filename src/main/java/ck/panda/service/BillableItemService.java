package ck.panda.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.BillableItem;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Service class for Billable Item.
 * This service provides basic CRUD and essential api's for Billable Item related business actions.
 *
 */
@Service
public interface BillableItemService extends CRUDService<BillableItem> {

    /**
     * Delete the billable item.
     *
     * @param billableItem BillableItem entity.
     * @return BillableItem.
     * @throws Exception error occurs
     */
    BillableItem softDelete(BillableItem billableItem) throws Exception;

    /**
     * Find all the billable items with status.
     *
     * @param pagingAndSorting pagination and sorting values.
     * @param isActive status either true or false.
     * @return list of billable items with pagination.
     * @throws Exception error occurs
     */
     Page<BillableItem> findAllByIsActive(PagingAndSorting pagingAndSorting, Boolean isActive) throws Exception;

     /**
      * Find all the billable items with status.
      *
      * @param isActive status either true or false.
      * @return list of billable items with pagination.
      * @throws Exception error occurs
      */
      List<BillableItem> findAllByIsActive(Boolean isActive) throws Exception;

}

