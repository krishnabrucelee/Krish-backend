package ck.panda.service;

import java.util.List;

import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Network;
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
   List<Network> findAllFromCSServer() throws Exception;

   /**
    * To get network from cloudstack server.
    *
    * @param uuid network uuid.
    * @return zone from server
    * @throws Exception unhandled errors.
    */
   Network findByUUID(String uuid) throws Exception;
}
