package ck.panda.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Application;
import ck.panda.domain.entity.Application.Status;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.repository.jpa.ApplicationRepository;
import ck.panda.domain.repository.jpa.DomainRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.TokenDetails;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;
import ck.panda.util.error.exception.EntityNotFoundException;

/**Application service implementation class. */
@Service
public class ApplicationServiceImpl implements ApplicationService {

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** Application repository reference. */
    @Autowired
    private ApplicationRepository applicationRepo;

    /** Autowired TokenDetails. */
    @Autowired
    private TokenDetails tokenDetails;

    /** Domain repository reference. */
    @Autowired
    private DomainRepository domainRepository;

    @Override
    public Application save(Application application) throws Exception {
        this.validateApplication(application);
        application.setDomainId(application.getDomain().getId());
        application.setIsActive(true);
        return applicationRepo.save(application);
    }

    /**
     * Validate the application.
     *
     * @param application reference of the application.
     * @throws Exception error occurs
     */
    private void validateApplication(Application application) throws Exception {
        Errors errors = validator.rejectIfNullEntity("application", application);
        errors = validator.validateEntity(application, errors);
        Application app = applicationRepo.findByTypeAndDomainAndIsActive(application.getType(), application.getDomain(), true,
                Status.ENABLED);
        if (app != null && application.getId() != app.getId()) {
            errors.addFieldError("type", "application.already.exist.for.same.domain");
        }
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        }
    }

    @Override
    public Application update(Application application) throws Exception {
        this.validateApplication(application);
        return applicationRepo.save(application);
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
        if (application == null) {
            throw new EntityNotFoundException("application.not.found");
        }
        return application;
    }

    @Override
    public Page<Application> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        Domain domain = domainRepository.findOne(Long.valueOf(tokenDetails.getTokenDetails("domainid")));
        if (domain != null && !domain.getName().equals("ROOT")) {
            if (Long.valueOf(tokenDetails.getTokenDetails("departmentid")) == 1000L) {
                return applicationRepo.findAllByIsActive(pagingAndSorting.toPageRequest(), true);
            } else {
                return applicationRepo.findAllByDomainIsActive(domain.getId(), true, pagingAndSorting.toPageRequest());
            }
        }
        return applicationRepo.findAllByIsActive(pagingAndSorting.toPageRequest(), true);
    }

    @Override
    public List<Application> findAll() throws Exception {
        Domain domain = domainRepository.findOne(Long.valueOf(tokenDetails.getTokenDetails("domainid")));
        if (domain != null && !domain.getName().equals("ROOT")) {
            if (Long.valueOf(tokenDetails.getTokenDetails("departmentid")) == 1000L) {
                return (List<Application>) applicationRepo.findAllByIsActive(true);
            } else {
                return applicationRepo.findAllByDomainIsActive(domain.getId(), true);
            }
        }
        return (List<Application>) applicationRepo.findAllByIsActive(true);
    }

    @Override
    public Page<Application> findAllByActive(PagingAndSorting pagingAndSorting) throws Exception {
        Domain domain = domainRepository.findOne(Long.valueOf(tokenDetails.getTokenDetails("domainid")));
        if (domain != null && !domain.getName().equals("ROOT")) {
            return applicationRepo.findAllByDomainIsActiveAndStatus(domain.getId(), pagingAndSorting.toPageRequest(), true, Status.ENABLED);
        }
        return applicationRepo.findAllByIsActiveAndStatus(pagingAndSorting.toPageRequest(), true, Status.ENABLED);
    }

    @Override
    public Application softDelete(Application application) throws Exception {
        application.setIsActive(false);
        application.setStatus(Status.DISABLED);
        return applicationRepo.save(application);
    }

    @Override
    public List<Application> findAllByIsActive(Boolean isActive) throws Exception {
        Domain domain = domainRepository.findOne(Long.valueOf(tokenDetails.getTokenDetails("domainid")));
        if (domain != null && !domain.getName().equals("ROOT")) {
            return applicationRepo.findAllByIsActiveAndDomainAndStatus(domain.getId(), true, Status.ENABLED);
        }
        return applicationRepo.findAllByIsActiveAndStatus(true, Status.ENABLED);
    }

}
