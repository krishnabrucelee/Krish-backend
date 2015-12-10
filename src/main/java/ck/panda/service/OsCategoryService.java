package ck.panda.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.OsCategory;
import ck.panda.domain.entity.Template;
import ck.panda.util.domain.CRUDService;

/**
 * Service interface for OS category entity.
 */

@Service
public interface OsCategoryService extends CRUDService<OsCategory> {

    /**
     * To get list of operating systems from cloudstack server.
     *
     * @return os list from server
     * @throws Exception unhandled errors.
     */
    List<OsCategory> findAllFromCSServer() throws Exception;

    /**
     * Find osCategory by uuid.
     *
     * @param uuid uuid of osCategory.
     * @return osCategory object.
     * @throws Exception unhandled errors.
     */
    OsCategory findbyUUID(String uuid) throws Exception;

    /**
     * Find the list of Os categories in templates by filters.
     *
     * @return Os categories list from server
     * @throws Exception raise if error
     */
    List<OsCategory> findByOsCategoryFilters();
}
