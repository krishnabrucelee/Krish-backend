package ck.panda.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.OsType;
import ck.panda.util.domain.CRUDService;

/**
 * Service interface for OS type entity.
 *
 */
@Service
public interface OsTypeService extends CRUDService<OsType> {

     /**
     * to get list of os types from cloudstack server.
     *
     * @return os types list from server
     * @throws Exception unhandled errors.
     */
    List<OsType> findAllFromCSServer() throws Exception;
}

