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
     * To get list of hosts from cloudstack server.
     *
     * @return host list from server
     * @throws Exception unhandled errors.
     */
    List<Host> findAllFromCSServer() throws Exception;

    /**
     * To get host from cloudstack server.
     *
     * @param uuid uuid of host.
     * @return Host from server
     * @throws Exception unhandled errors.
     */
    Host findByUUID(String uuid) throws Exception;

    /**
     * Soft delete for host
     *
     * @param host object
     * @return host
     * @throws Exception unhandled errors.
     */
	Host softDelete(Host host) throws Exception;
}
