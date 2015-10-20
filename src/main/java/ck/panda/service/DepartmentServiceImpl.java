package ck.panda.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.repository.jpa.DepartmentReposiory;
import ck.panda.util.AppValidator;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;
import ck.panda.util.error.exception.EntityNotFoundException;

/**
 * Department service implementation class.
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** Department repository reference. */
    @Autowired
    private DepartmentReposiory departmentRepo;

    @Override
    @PreAuthorize("hasAuthority('ROLE_DOMAIN_USER')")
    public Department save(Department department) throws Exception {

        Errors errors = validator.rejectIfNullEntity("department", department);
        errors = validator.validateEntity(department, errors);
        errors = this.validateName(errors, department.getName(), department.getDomain());

        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            return departmentRepo.save(department);
        }
    }

    /**
     * Check the department name already exist or not for same domain.
     *
     * @param errors,an error object
     *
     * @param name,which is to be validated.
     *
     * @param domainId, to check the department is unique or not for the domain.
     *
     * @return error is present,else new error object is returned.
     * @throws Exception if error is present.
     */
    private Errors validateName(Errors errors, String name, Domain domain) throws Exception {

        if (this.findByNameAndDomain(name, domain) != null) {
            errors.addFieldError("name", "department.already.exist");
        }
        return errors;
    }

    @Override
    public Department update(Department department) throws Exception {

        Errors errors = validator.rejectIfNullEntity("department", department);
        errors = validator.validateEntity(department, errors);
        errors = this.validateName(errors, department.getName(), department.getDomain());
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            return departmentRepo.save(department);
        }
    }

    @Override
    public void delete(Department department) throws Exception {
        departmentRepo.delete(department);
    }

    @Override
    public void delete(Long id) throws Exception {
        departmentRepo.delete(id);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_DOMAIN_USER')")
    public Department find(Long id) throws Exception {
        Department department = departmentRepo.findOne(id);

        LOGGER.debug("Sample Debug Message");
        LOGGER.trace("Sample Trace Message");

        if (department == null) {
            throw new EntityNotFoundException("department.not.found");
        }
        return department;
    }

    @Override
    public Page<Department> findAll(PagingAndSorting pagingAndSorting) throws Exception {
           return departmentRepo.findAll(pagingAndSorting.toPageRequest());
    }


    @Override
    public List<Department> findAll() throws Exception {
            return null;
    }

    @Override
    public Department findByNameAndDomain(String name, Domain domain) throws Exception {
        return	departmentRepo.findByNameAndDomain(name, domain);

    }

}
