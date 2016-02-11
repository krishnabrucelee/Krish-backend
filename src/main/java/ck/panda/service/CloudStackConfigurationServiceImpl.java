package ck.panda.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.CloudStackConfiguration;
import ck.panda.domain.repository.jpa.CloudStackConfigurationRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackServer;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;
import ck.panda.util.error.exception.EntityNotFoundException;

/**
 * CloudStackConfiguration service implementation.
 */
@Service
public class CloudStackConfigurationServiceImpl implements CloudStackConfigurationService {

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** CloudStack connector. */
    @Autowired
    private CloudStackServer server;

    /** CloudStackConfiguration Repository . */
    @Autowired
    private CloudStackConfigurationRepository configRepo;

    /** synchronization with cloudstack. */
    @Autowired
    private SyncService syncService;

    @Override
    public CloudStackConfiguration save(CloudStackConfiguration config) throws Exception {

        Errors errors = validator.rejectIfNullEntity("config", config);
        errors = validator.validateEntity(config, errors);

        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        }
        server.setServer(config.getApiURL(), config.getSecretKey(), config.getApiKey());
        syncService.syncRegion();
        configRepo.save(config);
        syncService.sync();
        return config;
    }

    @Override
    public CloudStackConfiguration update(CloudStackConfiguration config) throws Exception {

        Errors errors = validator.rejectIfNullEntity("config", config);
        errors = validator.validateEntity(config, errors);

        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        }

        return configRepo.save(config);
    }

    @Override
    public void delete(CloudStackConfiguration config) throws Exception {
        configRepo.delete(config);
    }

    @Override
    public void delete(Long id) throws Exception {
        configRepo.delete(id);
    }

    @Override
    public CloudStackConfiguration find(Long id) throws Exception {
        CloudStackConfiguration config = configRepo.findOne(id);
        if (config == null) {
            throw new EntityNotFoundException("config.not.found");
        }
        return config;

    }

    @Override
    public Page<CloudStackConfiguration> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return configRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<CloudStackConfiguration> findAll() throws Exception {
        return (List<CloudStackConfiguration>) configRepo.findAll();
    }

}
