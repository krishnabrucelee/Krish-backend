package ck.panda.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.OsCategory;
import ck.panda.util.domain.CRUDService;

/**
 * Service interface for OS category entity.
 */

@Service
public interface OsCategoryService extends CRUDService<OsCategory> {

    /**
     * To get list of OS category from cloudstack server.
     *
     * @return OS list from server.
     * @throws Exception unhandled errors.
     */
    List<OsCategory> findAllFromCSServer() throws Exception;

    /**
     * Find OS category by uuid.
     *
     * @param uuid uuid of OS category.
     * @return OS category.
     * @throws Exception unhandled errors.
     */
    OsCategory findbyUUID(String uuid) throws Exception;

    /**
     * Find the list of OS categories in templates by type.
     *
     * @param type of the OS category
     * @return OS categories list from server.
     * @throws Exception unhandled errors.
     */
    List<OsCategory> findByOsCategoryFilters(String type) throws Exception;
}
