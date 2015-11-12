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
     * To get list of OS types from cloudstack server.
     *
     * @return OS types list from server
     * @throws Exception unhandled errors.
     */
    List<OsType> findAllFromCSServer() throws Exception;

    /**
     * To get list of OS types based on OS category name selection from cloudstack server.
     *
     * @param categoryName name of the OS category
     * @return OS types list from server
     * @throws Exception unhandled errors.
     */
    List<OsType> findByCategoryName(String categoryName) throws Exception;

    /**
     * Get the OS type based on the uuid.
     *
     * @param uuid of the OS type
     * @return OS type
     */
    OsType findByUUID(String uuid);

}

