package ck.panda.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.CloudStackConfiguration;
import ck.panda.domain.repository.jpa.CloudStackConfigurationRepository;
import ck.panda.service.DepartmentServiceImpl;
import ck.panda.util.error.exception.EntityNotFoundException;

/**
 * This class acts as intermediate between cloudstack and cloudstack configuration repository.
 *
 */

@Service
public class ConfigUtil {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    /** Repository to store apikey, secretkey and url. */
    @Autowired
    private CloudStackConfigurationRepository configRepo;

    /** server objects which creates connectivity between cloudstack and UI. */
    @Autowired
    private CloudStackServer server;

    /**
     * To find the apikey, secret key and url from our db.
     * @param id table id
     * @return server
     * @throws EntityNotFoundException if entity not found
     */
    public CloudStackServer setServer(Long id) throws EntityNotFoundException {
        CloudStackConfiguration config = configRepo.findOne(id);
        if (config == null) {
            throw new EntityNotFoundException("config.not.found");
        }
        else {
            server.setServer(config.getApiURL(), config.getSecretKey(), config.getApiKey());
        }
        return server;
    }
}
