package ck.panda.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Application;
import ck.panda.domain.repository.jpa.ApplicationRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;
import ck.panda.util.error.exception.EntityNotFoundException;

/**Application service implementation class. */
@Service
public class ApplicationServiceImpl implements ApplicationService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationServiceImpl.class);

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** Application repository reference. */
    @Autowired
    private ApplicationRepository applicationRepo;

    @Override
    public Application save(Application application) throws Exception {

        Errors errors = validator.rejectIfNullEntity("application", application);
        errors = validator.validateEntity(application, errors);
        errors = validator.validateType(errors, application.getType());

        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            return applicationRepo.save(application);
        }
    }

    @Override
    public Application update(Application application) throws Exception {

        Errors errors = validator.rejectIfNullEntity("application", application);
        errors = validator.validateEntity(application, errors);

        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            return applicationRepo.save(application);
        }
    }

    @Override
    public void delete(Application application) throws Exception {
        applicationRepo.delete(application);
    }

    @Override
    public void delete(Long id) throws Exception {
        applicationRepo.delete(id);
    }

    @Override
    public Application find(Long id) throws Exception {
        Application application = applicationRepo.findOne(id);

        LOGGER.debug("Sample Debug Message");
        LOGGER.trace("Sample Trace Message");

        if (application == null) {
            throw new EntityNotFoundException("application.not.found");
        }
        return application;
    }

    @Override
    public Page<Application> findAll(PagingAndSorting pagingAndSorting) throws Exception {
           return applicationRepo.findAllByActive(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<Application> findAll() throws Exception {
            return null;
    }

	@Override
	public Application findByType(String type) throws Exception {
		return	applicationRepo.findByType(type);
	}
}
