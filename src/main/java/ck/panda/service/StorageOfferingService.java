package ck.panda.service;

import java.util.List;

import org.springframework.stereotype.Service;
import ck.panda.domain.entity.StorageOffering;
import ck.panda.util.domain.CRUDService;

/**
 * Service class for Storage Offering.
 * This service provides basic CRUD and essential api's
 * for Storage Offering related business actions.
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
}
