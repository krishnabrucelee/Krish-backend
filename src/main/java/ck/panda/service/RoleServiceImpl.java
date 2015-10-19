package ck.panda.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.Role;
import ck.panda.domain.repository.jpa.RoleReposiory;
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
    private RoleReposiory roleRepo;

    @Override
    @PreAuthorize("hasAuthority('ROLE_DOMAIN_USER')")
    public Role save(Role role) throws Exception {
        LOGGER.debug("Sample Debug Message");
        Errors errors = validator.rejectIfNullEntity("role", role);
        errors = validator.validateEntity(role, errors);

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
    @PreAuthorize("hasAuthority('ROLE_DOMAIN_USER')")
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
        return roleRepo.findAll(pagingAndSorting.toPageRequest());
    }


    @Override
    public List<Role> findAll() throws Exception {
               return null;
    }

}
