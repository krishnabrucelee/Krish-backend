package ck.panda.service;

import org.springframework.stereotype.Service;
import ck.panda.domain.entity.CloudStackConfiguration;
import ck.panda.util.domain.CRUDService;

/**
 * Service interface for the CloudStack configuration entity.
 */
@Service
public interface CloudStackConfigurationService extends CRUDService<CloudStackConfiguration> {

    // TODO for validation.
    // CloudStackConfiguration findByKeys(String apiKey);

    /**
     * Import individual sync from CS.
     *
     * @param keyName sync type id
     * @param type sync type
     * @throws Exception unhandled errors.
     */
    void importCsData(String keyName, String type) throws Exception;

}
