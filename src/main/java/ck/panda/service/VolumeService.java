/**
 *
 */
package ck.panda.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Volume;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.domain.vo.PagingAndSorting;

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
    * @param uuid uuid of volume.
    * @return zone from server
    * @throws Exception unhandled errors.
    */
   Volume findByUUID(String uuid) throws Exception;


   /**
    * Attach volume to the Instance.
    *
    * @param volume volume.
    * @return volume
    * @throws Exception Exception
    */
   Volume attachVolume(Volume volume) throws Exception;


   /**
    * Detach volume to the Instance.
    *
    * @param volume volume
    * @return volume
    * @throws Exception Exception
    */
   Volume detachVolume(Volume volume) throws Exception;

   /**
    * Resize volume from created volume.
    *
    * @param volume volume
    * @return volume
    * @throws Exception Exception
    */
   Volume resizeVolume(Volume volume) throws Exception;

   /**
    * Upload volume from Url.
    *
    * @param volume volume
    * @return volume
    * @throws Exception Exception
    */
   Volume uploadVolume(Volume volume) throws Exception;

   /**
    * SOft delete for volume.
    *
    * @param volume object
    * @return volume
    * @throws Exception unhandled errors.
    */
   Volume softDelete(Volume volume) throws Exception;

   /**
    * Find all the Volumes with active status.
    *
    * @param page pagination and sorting values.
    * @return list of volumes with pagination.
    * @throws Exception error occurs
    */
   Page<Volume> findAllByActive(PagingAndSorting page) throws Exception;


}
