package ck.panda.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.SupportedNetwork;
import ck.panda.util.domain.CRUDService;

/**
 * Service interface for supported network. This service provides basic list and save business actions.
 */
@Service
public interface SupportedNetworkService extends CRUDService<SupportedNetwork> {

    /**
     * To get list of supported network from cloudstack server.
     *
     * @return supported network list from server
     * @throws Exception unhandled errors.
     */
    List<SupportedNetwork> findAllFromCSServer() throws Exception;

    /**
     * To get supported network from cloudstack server.
     *
     * @param name supported network.
     * @return supported network from server
     * @throws Exception unhandled errors.
     */
    SupportedNetwork findByName(String name) throws Exception;

}
