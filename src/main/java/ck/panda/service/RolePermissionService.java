package ck.panda.service;

import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ck.panda.domain.entity.Role;
import ck.panda.domain.repository.jpa.RoleRepository;
import ck.panda.util.TokenDetails;

/**
 * A role permission implementation that uses a Map to check whether a domain Object and access level exists for a
 * particular user.
 *
 */
@Component
public class RolePermissionService implements PermissionEvaluator {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(VirtualMachineServiceImpl.class);

    /** Role repository reference. */
    @Autowired
    private RoleRepository roleRepository;

    /** Token details repository reference. */
    @Autowired
    private TokenDetails tokenDetails;

    /** Role name constant for token details. */
    public static final String ROLE_NAME = "rolename";

    /** Department constant for token details. */
    public static final String DEPARTMENT_ID = "departmentid";

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

            //TODO : We have to remove the token details from here
            Role role = roleRepository.findWithPermissionsByNameDepartmentAndIsActive(
                    tokenDetails.getTokenDetails(ROLE_NAME),
                    Long.parseLong(tokenDetails.getTokenDetails(DEPARTMENT_ID)), true);
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
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
            Object permission) {
        throw new UnsupportedOperationException();
    }
}
