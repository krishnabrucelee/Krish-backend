package ck.panda.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Role;
import ck.panda.domain.entity.Role.Status;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.User.UserType;
import ck.panda.domain.repository.jpa.RoleRepository;
import ck.panda.util.AppValidator;
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
    private RoleRepository roleRepo;

    /** Domain Service reference. */
    @Autowired
    private DomainService domainService;

    /** User service reference. */
    @Autowired
    private UserService userService;

    /** Message source attribute. */
    @Autowired
    private MessageSource messageSource;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    @Override
    @PreAuthorize("hasPermission(#role.getSyncFlag(), 'CREATE_ROLE')")
    public Role save(Role role) throws Exception {
        Errors errors = validator.rejectIfNullEntity("role", role);
        errors = validator.validateEntity(role, errors);
        validateName(errors, role.getName(), role.getDepartmentId(), true);
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            role.setStatus(Status.ENABLED);
            return roleRepo.save(role);
        }
    }

    @Override
    @PreAuthorize("hasPermission(#role.getSyncFlag(), 'EDIT_ROLE')")
    public Role update(Role role) throws Exception {
        Errors errors = validator.rejectIfNullEntity("role", role);
        errors = validator.validateEntity(role, errors);
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            return roleRepo.save(role);
        }
    }

    @Override
    @PreAuthorize("hasPermission(#role.getSyncFlag(), 'DELETE_ROLE')")
    public Role softDelete(Role role) throws Exception {
        Errors errors = new Errors(messageSource);
        if (userService.findByRole(role.getId(), true).size() > 0) {
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
        if (role == null) {
            throw new EntityNotFoundException("role.not.found");
        }
        return role;
    }

    @Override
    public Page<Role> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return findAllRolesWithoutFullPermissionAndActive(pagingAndSorting);
    }

    @Override
    public Page<Role> findAllByUserId(PagingAndSorting pagingAndSorting, Long userId) throws Exception {
        User user = convertEntityService.getOwnerById(userId);
        if (user != null && !user.getType().equals(UserType.ROOT_ADMIN)) {
            return roleRepo.findByDomainAndIsActive(user.getDomainId(), true, pagingAndSorting.toPageRequest());
        } else {
            return findAllRolesWithoutFullPermissionAndActive(pagingAndSorting);
        }
    }

    @Override
    public List<Role> findAll() throws Exception {
        return (List<Role>) roleRepo.findAll();
    }

    @Override
    public Role findWithPermissionsByNameDepartmentAndIsActive(String name, Long departmentId, Boolean isActive) throws Exception {
        return roleRepo.findWithPermissionsByNameDepartmentAndIsActive(name, departmentId, isActive);
    }

    @Override
    public List<Role> findAllByDepartmentAndIsActiveExceptName(Department department, Boolean isActive, String name) throws Exception {
        return (List<Role>) roleRepo.findAllByDepartmentAndIsActiveExceptName(department, isActive, name);
    }

    @Override
    public Page<Role> findAllRolesWithoutFullPermissionAndActive(PagingAndSorting pagingAndSorting) throws Exception {
        return roleRepo.findAllRolesWithoutFullPermissionAndActive(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<Role> findByDepartmentAndIsActive(Long id, Boolean isActive) throws Exception {
        return roleRepo.findByDepartmentAndIsActive(id, true);
    }

    /**
     * Validates the role.
     *
     * @param errors an error object
     * @param name of the role.
     * @param departmentId id of the department.
     * @param isActive role state either active/inactive.
     * @return error is present,else new error object is returned.
     * @throws Exception if error is present.
     */
    public Errors validateName(Errors errors, String name, Long departmentId, Boolean isActive) throws Exception {
        if (findByNameAndDepartmentIdAndIsActive(name, departmentId, isActive) != null) {
            errors.addGlobalError("role.name.unique.error");
        }
        return errors;
    }

    @Override
    public Role findByNameAndDepartmentIdAndIsActive(String name, Long departmentId, Boolean isActive)
            throws Exception {
        return roleRepo.findByNameAndDepartmentIdAndIsActive(name, departmentId, isActive);
    }

}
