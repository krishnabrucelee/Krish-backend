/**
 *
 */
package ck.panda.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Volume;
import ck.panda.domain.entity.Volume.VolumeType;
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
   Page<Volume> findAllByIsActive(PagingAndSorting page) throws Exception;

   /**
    * list by instance attached to volume.
    *
    * @param volume Volume
    * @return volume Volumes from instance.
    * @throws Exception exception
    */
   List<Volume> findByInstanceAndIsActive(Long volume) throws Exception;

   /**
    * list by volumes by its volume type.
    *
    * @return Upload Volume
    * @throws Exception exception
    */
   List<Volume> findByVolumeTypeAndIsActive() throws Exception;


   /**
    * List the volume with its instanceId, volume type and active status.
    *
    * @param volume Volume
    * @return volume list
    * @throws Exception if error occurs
    */
   List<Volume> findByInstanceAndVolumeTypeAndIsActive(Long volume) throws Exception;

   /**
    * Volume with its instanceId, volume type and active status.
    *
    * @param volume Volume
    * @return volume list
    * @throws Exception if error occurs
    */
   Volume findByInstanceAndVolumeType(Long volume) throws Exception;

   /**
    * Find all vmInstance from department.
    *
    * @param departmentId department id.
    * @param isActive get the department list based on active/inactive status.
    * @return vmInstance list.
    */
   @Query(value = "select vm from VmInstance vm where vm.departmentId=:id ")
   List<Volume> findByDepartment(@Param("id") Long departmentId);

   /**
    * Get the volumes based on project.
    *
    * @param projectId project id.
    * @param volumeType volume type.
    * @return project
    */
   List<Volume> findByProjectAndVolumeType(Long projectId, List<VolumeType> volumeType);

   /**
    * Get the volumes based on department.
    *
    * @param departmentId department id.
    * @return department
    * @throws Exception error occurs.
    */
   List<Volume> findByDepartmentAndVolumeType(Long departmentId, List<VolumeType> volumeType);

   /**
    * Get the volumes based on department and not project.
    *
    * @param departmentId department id.
    * @param projectId project id.
    * @return department
    * @throws Exception error occurs.
    */
   List<Volume> findByDepartmentAndProjectAndVolumeType(Long departmentId, Long projectId, List<VolumeType> volumeType);

//   /**
//    * Get the count of the volume based on the attached/detached.
//    *
//    * @return count
//    */
//   Integer findCountByStatus() throws NumberFormatException, Exception;

}
