package ck.panda.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Role;
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

    /** Department repository reference. */
    @Autowired
    private DomainService domainService;

    /** Autowired TokenDetails */
    @Autowired
    TokenDetails tokenDetails;

    @Override
    public Role save(Role role) throws Exception {
        LOGGER.debug("Sample Debug Message");
        Errors errors = validator.rejectIfNullEntity("role", role);
        errors = validator.validateEntity(role, errors);
        errors = validator.validateName(errors, role.getName(), role.getDepartment());

        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            return roleRepo.save(role);
        }
    }

    @Override
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
        List<Department> departments = departmentService.findAll();
        if (departments.size() > 0) {
            List<Role> roles = new ArrayList<Role>();
            if (!departments.get(0).getDomain().getName().equals("ROOT")) {
                for (Department department : departments) {
                    for (Role role : roleRepo.getRolesByDepartment(department)) {
                        roles.add(role);
                    }
                }
                Page<Role> roleList = new PageImpl<Role>(roles, pagingAndSorting.toPageRequest(), roles.size());
                return roleList;
            }
        }
        return roleRepo.findAllByActive(pagingAndSorting.toPageRequest());
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

}
