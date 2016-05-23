package ck.panda.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.PhysicalNetwork;
import ck.panda.util.domain.CRUDService;

/**
 * Service interface for physical network. This service provides basic list and save business actions.
 */
@Service
public interface PhysicalNetworkService extends CRUDService<PhysicalNetwork> {

    /**
     * To get list of physical network from cloudstack server.
     *
     * @return physical network list from server
     * @throws Exception unhandled errors.
     */
    List<PhysicalNetwork> findAllFromCSServer() throws Exception;

    /**
     * To get physical network from cloudstack server.
     *
     * @param uuid of physical network.
     * @return physical network from server
     * @throws Exception unhandled errors.
     */
    PhysicalNetwork findByUuid(String uuid) throws Exception;

}
