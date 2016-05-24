package ck.panda.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.NetworkServiceProvider;
import ck.panda.util.domain.CRUDService;

/**
 * Service interface for network service provider. This service provides basic list and save business actions.
 */
@Service
public interface NetworkServiceProviderService extends CRUDService<NetworkServiceProvider> {

    /**
     * To get list of network service provider from cloudstack server.
     *
     * @return network service provider list from server
     * @throws Exception unhandled errors.
     */
    List<NetworkServiceProvider> findAllFromCSServer() throws Exception;

    /**
     * To get network service provider from cloudstack server.
     *
     * @param uuid of network service provider.
     * @return network service provider from server
     * @throws Exception unhandled errors.
     */
    NetworkServiceProvider findByUuid(String uuid) throws Exception;

    /**
     * To get network service provider from cloudstack server.
     *
     * @param name of network service provider.
     * @param physicalNetworkId physical network id
     * @return network service provider from server
     * @throws Exception unhandled errors.
     */
    NetworkServiceProvider findByNameAndPhysicalNetworkId(String name, Long physicalNetworkId) throws Exception;

    /**
     * To get network service provider from cloudstack server.
     *
     * @param name of network service provider.
     * @return network service provider from server
     * @throws Exception unhandled errors.
     */
    List<NetworkServiceProvider> findByName(String name) throws Exception;

}
