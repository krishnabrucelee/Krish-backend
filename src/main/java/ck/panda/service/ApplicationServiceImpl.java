package ck.panda.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ck.panda.constants.CloudStackConstants;
import ck.panda.domain.entity.Application;
import ck.panda.domain.entity.Application.Status;
import ck.panda.domain.entity.User;
import ck.panda.domain.repository.jpa.ApplicationRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;
import ck.panda.util.error.exception.EntityNotFoundException;

/** Application service implementation class. */
@Service
public class ApplicationServiceImpl implements ApplicationService {

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** Application repository reference. */
    @Autowired
    private ApplicationRepository applicationRepo;

    /** Convert entity service reference. */
    @Autowired
    private ConvertEntityService convertEntity;

    /** Constant for application. */
    public static final String APPLICATION = "application";

    @Override
    @PreAuthorize("hasPermission(null, 'CREATE_APPLICATION_TYPE')")
    public Application save(Application application) throws Exception {
        this.validateApplication(application);
        application.setDomainId(application.getDomainId());
        application.setStatus(Application.Status.ENABLED);
        application.setIsActive(true);
        return applicationRepo.save(application);
    }

    /**
     * Validate the application.
     *
     * @param application reference of the application
     * @throws Exception if error occurs
     */
    private void validateApplication(Application application) throws Exception {
        Errors errors = validator.rejectIfNullEntity(APPLICATION, application);
        errors = validator.validateEntity(application, errors);
        Application app = applicationRepo.findByTypeAndDomainAndIsActive(application.getType(),
            application.getDomainId(), true, Status.ENABLED);
        if (app != null && application.getId() != app.getId()) {
            errors.addFieldError(CloudStackConstants.CS_TYPE, "error.application.type.duplicate.check");
        }
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        }
    }

    @Override
    @PreAuthorize("hasPermission(null, 'EDIT_APPLICATION_TYPE')")
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
            throw new EntityNotFoundException("error.application.not.found");
        }
        return application;
    }

    @Override
    public Page<Application> findAll(PagingAndSorting pagingAndSorting, Long id) throws Exception {
        if (((convertEntity.getOwnerById(id).getType()).equals(User.UserType.USER))
            || ((convertEntity.getOwnerById(id).getType()).equals(User.UserType.DOMAIN_ADMIN))) {
            return applicationRepo.findAllByDomainIsActive(convertEntity.getOwnerById(id).getDomainId(), true,
                pagingAndSorting.toPageRequest());
        }
        return applicationRepo.findAllByIsActive(pagingAndSorting.toPageRequest(), true);
    }

    @Override
    public List<Application> findAll(Long id) throws Exception {
        if (((convertEntity.getOwnerById(id).getType()).equals(User.UserType.USER))
            || ((convertEntity.getOwnerById(id).getType()).equals(User.UserType.DOMAIN_ADMIN))) {
            return applicationRepo.findAllByDomainIsActive(convertEntity.getOwnerById(id).getDomainId(), true);
        }
        return (List<Application>) applicationRepo.findAllByIsActive(true);
    }

    @Override
    @PreAuthorize("hasPermission(null, 'DELETE_APPLICATION_TYPE')")
    public Application softDelete(Application application) throws Exception {
        application.setIsActive(false);
        application.setStatus(Status.DISABLED);
        return applicationRepo.save(application);
    }

    @Override
    public List<Application> findAllByDomain(Long domainId) throws Exception {
        return applicationRepo.findAllByDomainAndIsActiveAndStatus(domainId, true, Status.ENABLED);
    }

    @Override
    public Page<Application> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return applicationRepo.findAllByIsActive(pagingAndSorting.toPageRequest(), true);
    }

    @Override
    public List<Application> findAll() throws Exception {
        return (List<Application>) applicationRepo.findAll();
    }

    @Override
    public Page<Application> findAllByDomainId(Long domainId, PagingAndSorting pagingAndSorting) throws Exception {
        return applicationRepo.findAllByDomainIdAndIsActive(domainId, true, pagingAndSorting.toPageRequest());
    }

    @Override
    public Page<Application> findAllByDomainIdAndSearchText(Long domainId, PagingAndSorting pagingAndSorting, String searchText, Long userId) throws Exception {
        return applicationRepo.findDomainBySearchText(domainId, pagingAndSorting.toPageRequest(), searchText, true);
    }
}
