package ck.panda.service;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.Role;
import ck.panda.domain.entity.Role.Status;
import ck.panda.domain.repository.jpa.RoleReposiory;
import ck.panda.util.AppValidator;
import ck.panda.util.TokenDetails;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;
import ck.panda.util.error.exception.EntityNotFoundException;

/**
 * Role service implementation class.
 */
@Service
public class RoleServiceImpl implements RoleService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleServiceImpl.class);

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** Role repository reference. */
    @Autowired
    private RoleReposiory roleRepo;

    /** Department repository reference. */
    @Autowired
    private DepartmentService departmentService;

    /** Domain Service reference. */
    @Autowired
    private DomainService domainService;

    /** Department repository reference. */
    @Autowired
    private UserService userService;

    /** Message source attribute. */
    @Autowired
    private MessageSource messageSource;

    /** Autowired TokenDetails. */
    @Autowired
    private TokenDetails tokenDetails;

    @Override
    @PreAuthorize("hasPermission(#role.getSyncFlag(), 'CREATE_ROLE')")
    public Role save(Role role) throws Exception {
        LOGGER.debug("Sample Debug Message");
        Errors errors = validator.rejectIfNullEntity("role", role);
        errors = validator.validateEntity(role, errors);
        validateName(errors, role.getName(), role.getDepartment());

        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            role.setStatus(Status.ENABLED);
            role.setDepartmentId(role.getDepartment().getId());
            role.setDomainId(role.getDepartment().getDomainId());
            return roleRepo.save(role);
        }
    }

    @Override
    @PreAuthorize("hasPermission(#role.getSyncFlag(), 'EDIT_ROLE')")
    public Role update(Role role) throws Exception {
        Errors errors = validator.rejectIfNullEntity("role", role);
        errors = validator.validateEntity(role, errors);
        if(role.getDepartmentId() != role.getDepartment().getId()){
             validateName(errors, role.getName(), role.getDepartment());
        }
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            role.setDepartmentId(role.getDepartment().getId());
            return roleRepo.save(role);
        }
    }

    @Override
    @PreAuthorize("hasPermission(#role.getSyncFlag(), 'DELETE_ROLE')")
    public Role softDelete(Role role) throws Exception {
        Errors errors = new Errors(messageSource);
        if(userService.findByRole(role.getId(), true).size() > 0)  {
            errors.addGlobalError("role.assigned.to.user.cannot.be.deleted");
            throw new ApplicationException(errors);
        } else {
            role.setIsActive(false);
            role.setStatus(Status.DISABLED);
            return roleRepo.save(role);
        }
    }
    @Override
    public void delete(Role role) throws Exception {
        roleRepo.delete(role);
    }

    @Override
    public void delete(Long id) throws Exception {
        roleRepo.delete(id);
    }

    @Override
    public Role find(Long id) throws Exception {
        Role role = roleRepo.findOne(id);

        LOGGER.debug("Sample Debug Message");
        LOGGER.trace("Sample Trace Message");

        if (role == null) {
            throw new EntityNotFoundException("role.not.found");
        }
        return role;
    }

    @Override
  public Page<Role> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        Domain domain = domainService.find(Long.valueOf(tokenDetails.getTokenDetails("domainid")));
        if (domain != null && !domain.getName().equals("ROOT")) {
            return roleRepo.findByDomainAndIsActive(domain.getId(), true, pagingAndSorting.toPageRequest());
        }else {
            return findAllRolesWithoutFullPermissionAndActive(pagingAndSorting);
        }
    }

    @Override
    public List<Role> findAll() throws Exception {
        return (List<Role>) roleRepo.findAll();
    }

    @Override
    public Role findByName(String name, Department department) throws Exception {
        return roleRepo.findUniqueness(name, department);
    }

    @Override
    public List<Role> getRolesByDepartment(Department department) throws Exception {
        return (List<Role>) roleRepo.getRolesByDepartment(department);
    }

    @Override
    public Page<Role> findAllRolesWithoutFullPermissionAndActive(PagingAndSorting pagingAndSorting) throws Exception {
        return roleRepo.findAllRolesWithoutFullPermissionAndActive(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<Role> findByDepartment(Department department) throws Exception {
        return roleRepo.findByDepartment(department);
    }

    /**
     * Validates the name and department field for roles.
     *
     * @param errors an error object
     * @param name which is to be validated.
     * @param department which is to be validated.
     * @return error is present,else new error object is returned.
     * @throws Exception if error is present.
     */
    public Errors validateName(Errors errors, String name, Department department) throws Exception {

        if (findByName(name, department) != null) {
//            errors.addFieldError("name", "role.name.unique.error");
            errors.addGlobalError("role.name.unique.error");
        }
        return errors;
    }

    @Override
    public Role findByNameAndDepartmentIdAndIsActive(String name, Long departmentId, Boolean isActive) throws Exception {
        return roleRepo.findByNameAndDepartmentIdAndIsActive(name, departmentId, isActive);
    }

}
