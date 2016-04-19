package ck.panda.service;

import org.springframework.stereotype.Service;
import ck.panda.domain.entity.GeneralConfiguration;
import ck.panda.util.domain.CRUDService;

/**
 * Service interface for general configuration entity.
 *
 */
@Service
public interface GeneralConfigurationService extends CRUDService<GeneralConfiguration> {

     /**
     * Find general configuration by isActive.
     *
     * @param isActive status of the compute offer
     * @return list general configuration.
     * @throws Exception if error occurs.
     */
    GeneralConfiguration findByIsActive(Boolean isActive) throws Exception;

}
