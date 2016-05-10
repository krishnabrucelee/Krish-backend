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
     * To get network offering from cloudstack server.
     *
     * @param uuid uuid of network offering.
     * @return network offering from server
     * @throws Exception unhandled errors.
     */
    NetworkOffering findByUUID(String uuid) throws Exception;

    /**
     * To get network offering from cloudstack server.
     *
     * @param id of network offering.
     * @return network offering from server
     * @throws Exception unhandled errors.
     */
    NetworkOffering findById(Long id) throws Exception;

    /**
     * To get list of Isolated network offering from cloudstack server.
     * @param csRequired Availability
     * @param csIsolated Isolated
     *
     * @return isolated network offering list from server
     * @throws Exception unhandled errors.
     */
    List<NetworkOffering> findByIsolatedAndRequired(String csIsolated, String csRequired) throws Exception;

    /**
     * To get list of VPC network offering from cloudstack server.
     *
     * @return VPC network offering list from server
     * @throws Exception unhandled errors.
     */
    List<NetworkOffering> findVpcList() throws Exception;

}
