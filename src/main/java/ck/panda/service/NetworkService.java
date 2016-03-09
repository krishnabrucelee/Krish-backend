package ck.panda.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Network;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Service interface for GuestNetwork entity.
 *
 */
@Service
public interface NetworkService extends CRUDService<Network> {

    /**
     * To get list of networks for sync.
     *
     * @return network list from server.
     * @throws Exception unhandled errors.
     */
    List<Network> findAllFromCSServerByDomain() throws Exception;

    /**
     * To get network from cloudstack server.
     *
     * @param uuid network uuid.
     * @return zone from server
     * @throws Exception unhandled errors.
     */
    Network findByUUID(String uuid) throws Exception;

    /**
     * To get network from cloudstack server.
     *
     * @param id network id.
     * @return network from server
     * @throws Exception unhandled errors.
     */
    Network findById(Long id) throws Exception;

    /**
     * To get list of networks from department.
     *
     * @param department department.
     * @param isActive true/false.
     * @return network list from server.
     * @throws Exception unhandled errors.
     */
    List<Network> findByDepartmentAndNetworkIsActive(Long department, Boolean isActive) throws Exception;

    /**
     * Soft delete for Network.
     *
     * @param network get Network id.
     * @return network
     * @throws Exception exception
     */
    Network softDelete(Network network) throws Exception;

    /**
     * Paging and Sorting for displaying more number of elements in list which are active.
     *
     * @param page pagination
     * @param userId id of the user
     * @return sorted values.
     * @throws Exception unhandled errors.
     */
    Page<Network> findAllByActive(PagingAndSorting page, Long userId) throws Exception;

    /**
     * To get list of networks from project.
     *
     * @param projectId project id.
     * @param isActive true/false.
     * @return network list from server.
     * @throws Exception unhandled errors.
     */
    List<Network> findByProjectAndNetworkIsActive(Long projectId, Boolean isActive) throws Exception;

    /**
     * To get active networks list.
     *
     * @param isActive status of the network
     * @return network
     * @throws Exception if error occurs.
     */
    List<Network> findAllByActive(Boolean isActive) throws Exception;

    /**
     * Save method in which userId is passed for tokenDetails.
     *
     * @param network network
     * @param userId id of the user
     * @return network
     * @throws Exception unHandled errors
     */
    Network save(Network network, Long userId) throws Exception;

    /**
     * Restart network for reapplying all port forwarding, lb rules and ip addresses.
     *
     * @param network to be restarted.
     * @return network.
     * @throws Exception if error occurs.
     */
    Network restartNetwork(Network network) throws Exception;

    /**
     * Release ip from Network
     *
     * @param network object
     * @return network
     * @throws Exception if error occurs.
     */
    Network ipRelease(Network network) throws Exception;

}
