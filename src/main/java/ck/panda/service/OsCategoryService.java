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
     * to get list of operating systems from cloudstack server.
     *
     * @return os list from server
     * @throws Exception unhandled errors.
     */
    List<OsCategory> findAllFromCSServer() throws Exception;
}
