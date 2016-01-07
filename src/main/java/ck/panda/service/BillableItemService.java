package ck.panda.service;

import org.springframework.stereotype.Service;
import ck.panda.domain.entity.BillableItem;
import ck.panda.util.domain.CRUDService;

/**
 * Service class for Billable Item.
 * This service provides basic CRUD and essential api's for Billable Item related business actions.
 *
 */
@Service
public interface BillableItemService extends CRUDService<BillableItem> {

}

