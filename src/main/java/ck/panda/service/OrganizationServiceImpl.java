package ck.panda.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Organization;
import ck.panda.domain.repository.jpa.OrganizationRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;


/** Organization Service implementation class. */
@Service
public class OrganizationServiceImpl implements OrganizationService {

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** Organization repository reference. */
    @Autowired
    private OrganizationRepository organizationRepo;

    /** Organization string literal. */
    public static final String ORGANIZATION = "organization";

    @Override
    public Organization save(Organization organization) throws Exception {
        Errors errors = validator.rejectIfNullEntity(ORGANIZATION, organization);
        errors = validator.validateEntity(organization, errors);
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            organization.setIsActive(true);
            return organizationRepo.save(organization);
        }
    }

    @Override
    public Organization update(Organization organization) throws Exception {
        return organizationRepo.save(organization);
    }

    @Override
    public void delete(Organization organization) throws Exception {
        organizationRepo.delete(organization);
    }

    @Override
    public void delete(Long id) throws Exception {
        organizationRepo.delete(id);
    }

    @Override
    public Page<Organization> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return organizationRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<Organization> findAll() throws Exception {
        return (List<Organization>) organizationRepo.findAll();
    }

    @Override
    public Organization find(Long id) throws Exception {
        return organizationRepo.findOne(id);
    }

    @Override
    public Organization findByIsActive(Boolean isActive) throws Exception {
        return organizationRepo.findByIsActive(true);
    }
}
