package ck.panda.service;

import java.util.List;
import ck.panda.domain.entity.Permission;
import ck.panda.util.domain.CRUDService;
/**
 * Permission service interface.
 *
 */
public interface PermissionService  extends CRUDService<Permission> {

    /**
     * List the permission.
     * @return list of permission.
     * @throws Exception error occurs.
     */
    List<Permission> getPermissionList() throws Exception;

}
