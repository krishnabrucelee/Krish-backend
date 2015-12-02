package ck.panda.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Network;
import ck.panda.domain.entity.User;
import ck.panda.util.domain.CRUDService;

/**
 * Service interface for GuestNetwork entity.
 *
 */
@Service
public interface NetworkService extends CRUDService<Network> {

    /**
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
}
