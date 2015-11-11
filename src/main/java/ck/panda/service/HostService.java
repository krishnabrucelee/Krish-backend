package ck.panda.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Host;
import ck.panda.util.domain.CRUDService;

/**
 * Service class for host.
 * This service provides basic CRUD and essential api's for host actions.
 *
 */
@Service
public interface HostService extends CRUDService<Host> {

    /**
     * To get list of domains from cloudstack server.
     *
     * @return domain list from server
     * @throws Exception unhandled errors.
     */
    List<Host> findAllFromCSServer() throws Exception;
}