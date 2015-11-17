/**
 *
 */
package ck.panda.service;

import java.util.List;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.Pod;
import ck.panda.domain.entity.Volume;
import ck.panda.util.domain.CRUDService;

/**
 * Volume service.
 */
@Service
public interface VolumeService extends CRUDService<Volume> {

    /**
    * To get list of Volume from cloudstack server.
    *
    * @return Volume list from server
    * @throws Exception unhandled errors.
    */
   List<Volume> findAllFromCSServer() throws Exception;

   /**
    * To get volume from cloudstack server.
    *
    * @param uuid uuid of pod.
    * @return zone from server
    * @throws Exception unhandled errors.
    */
   Volume findByUUID(String uuid) throws Exception;
}
