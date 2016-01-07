package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ck.panda.domain.entity.Permission;

/**
 * Permission repository for get data from database.
 *
 */
@Repository
public interface PermissionRepository extends PagingAndSortingRepository<Permission, Long> {

    /**
     * List the permission.
     * 
     * @return list of permission.
     */
    @Query(value = "select permission from Permission permission where permission.isActive IS TRUE")
    List<Permission> getPermissionList();

}
