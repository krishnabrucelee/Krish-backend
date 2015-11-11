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

/**Application service implementation class. */
@Service
public class ApplicationServiceImpl implements ApplicationService {

    /** Logger attribute. */
	//TODO Yasin: remove not used attribute.
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationServiceImpl.class);

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** Application repository reference. */
    @Autowired
    private ApplicationRepository applicationRepo;

    @Override
    public Application save(Application application) throws Exception {
    	//TODO Yasin: move all the validations to private method with a relevant method name
        Errors errors = validator.rejectIfNullEntity("application", application);
        errors = validator.validateEntity(application, errors);
        errors = validateType(errors, application.getType());

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
        //TODO Yasin: incomplete validation as referred in create

        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            return applicationRepo.save(application);
        }
    }

    @Override
    public void delete(Application application) throws Exception {
    	//TODO Yasin: Validation missing. When a domain is using this application, can we able to delete?
        applicationRepo.delete(application);
    }

    @Override
    public void delete(Long id) throws Exception {
    	//TODO Yasin: Same as above
        applicationRepo.delete(id);
    }

    @Override
    public Application find(Long id) throws Exception {
        return applicationRepo.findOne(id);
    }

    @Override
    public Page<Application> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return applicationRepo.findAllByActive(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<Application> findAll() throws Exception {
        return (List<Application>) applicationRepo.findAll();
    }

    @Override
    public Application findByType(String type) throws Exception {
        return applicationRepo.findByType(type);
    }

    /**
     * Validates the type field.
     *
     * @param errors ,an error object
     * @param type of the entity to be validated.
     * @return error is present,else new error object is returned.
     * @throws Exception if error is present.
     */
    //TODO Yasin: why we are having public access specifier here?
    public Errors validateType(Errors errors, String type) throws Exception {

        if (this.findByType(type) != null) {
            errors.addFieldError("type", "Application Type is already exists");
        }
        return errors;
    }
}
