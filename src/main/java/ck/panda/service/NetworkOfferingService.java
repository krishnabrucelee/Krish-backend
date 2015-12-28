package ck.panda.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.NetworkOffering;
import ck.panda.util.domain.CRUDService;

/**
 * Service interface for NetworkOffering entity.
 *
 */
@Service
public interface NetworkOfferingService extends CRUDService<NetworkOffering> {

    /**
     * To get list of network offering from cloudstack server.
     *
     * @return network offering list from server
     * @throws Exception unhandled errors.
     */
    List<NetworkOffering> findAllFromCSServer() throws Exception;

    /**
     * To get zone from cloudstack server.
     *
     * @param uuid uuid of zone.
     * @return zone from server
     * @throws Exception unhandled errors.
     */
    NetworkOffering findByUUID(String uuid) throws Exception;

    /**
     * To get zone from cloudstack server.
     *
     * @param uuid uuid of zone.
     * @return zone from server
     * @throws Exception unhandled errors.
     */
    NetworkOffering findById(Long id) throws Exception;
    
    /**
     * To get list of Isolated network offering from cloudstack server.
     *
     * @return isolated network offering list from server
     * @throws Exception unhandled errors.
     */
    List<NetworkOffering> findIsolated() throws Exception;

}
