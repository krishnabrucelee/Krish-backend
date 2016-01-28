package ck.panda.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Item;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Service class for Billable Item.
 * This service provides basic CRUD and essential api's for Billable Item related business actions.
 *
 */
@Service
public interface ItemService extends CRUDService<Item> {

    /**
     * Find all the items with status.
     *
     * @param pagingAndSorting pagination and sorting values.
     * @param isActive status either true or false.
     * @return list of items with pagination.
     * @throws Exception error occurs
     */
     Page<Item> findAllByIsActive(PagingAndSorting pagingAndSorting, Boolean isActive) throws Exception;

     /**
      * Find all the items with status.
      *
      * @param isActive status either true or false.
      * @return list of items with pagination.
      * @throws Exception error occurs
      */
      List<Item> findAllByIsActive(Boolean isActive) throws Exception;
}

