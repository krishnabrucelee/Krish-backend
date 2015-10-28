package ck.panda.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    public Department save(Department department) throws Exception {

        Errors errors = validator.rejectIfNullEntity("department", department);
        errors = validator.validateEntity(department, errors);
        errors = this.validateName(errors, department.getName(), department.getDomain(), (long) 0);

        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            department.setIsActive(true);
            department.setStatus(Department.Status.ENABLED);
            return departmentRepo.save(department);
        }
    }

    /**
     * Check the department name already exist or not for same domain.
     *
     * @param errors already existing error list.
     * @param name name of the department.
     * @param domain domain object.
     * @return errors.
     * @throws Exception
     */
    private Errors validateName(Errors errors, String name, Domain domain, Long departmentId) throws Exception {
        if (departmentRepo.findByNameAndDomain(name, domain, departmentId) != null) {
            errors.addFieldError("name", "department.already.exist.for.same.domain");
        }
        return errors;
    }

    @Override
    public Department update(Department department) throws Exception {

        Errors errors = validator.rejectIfNullEntity("department", department);
        errors = validator.validateEntity(department, errors);
        errors = this.validateName(errors, department.getName(), department.getDomain(), department.getId());
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

    public Page<Department> findAllByActive(PagingAndSorting pagingAndSorting) throws Exception {
        return departmentRepo.findAllByActive(pagingAndSorting.toPageRequest());
    }


    @Override
    public Department softDelete(Department department) throws Exception {
        department.setIsActive(false);
        department.setStatus(Department.Status.DELETED);
        return departmentRepo.save(department);
    }

}
