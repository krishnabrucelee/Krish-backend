package ck.panda.service;

import java.util.List;
<<<<<<< Updated upstream
=======

import org.springframework.data.domain.Page;
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
    * To get list of networks from cloudstack server.
    *
    * @return domain list from server
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
    * To get list of networks from department.
    *
    * @param department department.
    * @return network list from server.
    * @throws Exception unhandled errors.
    */
   List<Network> findByDepartment(Long department) throws Exception;

   /**
    * Method to soft delete network.
    *
    * @param network object.
    * @return network.
    * @throws Exception if error occurs.
    */
   Network softDelete(Network network) throws Exception;
=======
     * To get list of networks from cloudstack server.
     *
     * @return domain list from server
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
     * To get list of networks from department.
     *
     * @param department department.
     * @return network list from server.
     * @throws Exception unhandled errors.
     */
    List<Network> findByDepartment(Long department) throws Exception;

    /**
     * Soft delete for Network.
     *
     * @param network get Network id.
     * @return network
     * @throws Exception exception
     */
    Network softDelete(Network network) throws Exception;

    /**
     * Paging and Sorting for displaying more number of elements in list which
     * are active.
     *
     * @param page pagination
     * @return sorted values.
     * @throws Exception unhandled errors.
     */
    Page<Network> findAllByActive(PagingAndSorting page) throws Exception;
>>>>>>> Stashed changes
}
