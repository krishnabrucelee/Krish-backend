package ck.panda.service;

import org.springframework.stereotype.Service;
import ck.panda.domain.entity.ManualCloudSync;
import ck.panda.util.domain.CRUDService;

/**
 * Service class for Manual Cloud Sync.
 *
 * This service provides basic CRUD and essential api's for Manual Cloud Sync related business actions.
 */
@Service
public interface ManualCloudSyncService extends CRUDService<ManualCloudSync> {

    /**
     * Find manual sync data by key value.
     *
     * @param keyName sync key name
     * @return manual sync object.
     * @throws Exception unhandled errors.
     */
    ManualCloudSync findBySyncName(String keyName) throws Exception;
}
