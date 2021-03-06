package ck.panda.service;

import java.util.List;

import org.springframework.stereotype.Service;
import ck.panda.domain.entity.PrimaryStorage;
import ck.panda.util.domain.CRUDService;

/**
 * Service interface for secondary storage entity.
 *
 */
@Service
public interface PrimaryStorageService extends CRUDService<PrimaryStorage> {

    /**
     * To get list of secondary storage from cloudstack server.
     *
     * @return secondary storage list from server
     * @throws Exception unhandled errors.
     */
    List<PrimaryStorage> findAllFromCSServer() throws Exception;

}