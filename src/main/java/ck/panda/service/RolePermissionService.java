package ck.panda.service;

import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Role;
import ck.panda.domain.repository.jpa.DepartmentReposiory;
import ck.panda.domain.repository.jpa.RoleReposiory;
import ck.panda.util.TokenDetails;

/**
 * A role permission implementation that uses a Map to
 * check whether a domain Object and access level exists for a particular user.
 *
 */
@Component
public class RolePermissionService implements PermissionEvaluator {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(VirtualMachineServiceImpl.class);

    /** Role repository reference. */
    @Autowired
    private RoleReposiory roleReposiory;

    /** Department repository reference. */
    @Autowired
    private DepartmentReposiory departmentReposiory;

    /** Token details repository reference. */
    @Autowired
    private TokenDetails tokenDetails;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        try {
            if (authentication == null) {
                return false;
            }
            if (targetDomainObject != null) {
                Boolean syncFlag = (Boolean) targetDomainObject;
                if (!syncFlag) {
                    return true;
                }
            }
            Role role = roleReposiory.findUniqueness(tokenDetails.getTokenDetails("rolename"), Long.parseLong(tokenDetails.getTokenDetails("departmentid")));
            for (int i = 0; i < role.getPermissionList().size(); i++) {
                if (role.getPermissionList().get(i).getActionKey().equals(permission.toString())) {
                    return true;
                }
            }
        } catch (NumberFormatException e) {
            LOGGER.error("ERROR AT PERMISSION CHECK", e);
        } catch (Exception e) {
            LOGGER.error("ERROR AT PERMISSION CHECK", e);
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        throw new UnsupportedOperationException();
    }
}
