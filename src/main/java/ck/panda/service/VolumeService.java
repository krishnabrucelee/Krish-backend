/**
 *
 */
package ck.panda.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.Snapshot;
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
     * @param userId user details
     * @return volume
     * @throws Exception Exception
     */
    Volume uploadVolume(Volume volume, Long userId) throws Exception;

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
     * @param userId user details
     * @return list of volumes with pagination.
     * @throws Exception error occurs
     */
    Page<Volume> findAllByIsActive(PagingAndSorting page, Long userId) throws Exception;

    /**
     * list by instance attached to volume.
     *
     * @param volume Volume
     * @param userId user details
     * @return volume Volumes from instance.
     * @throws Exception exception
     */
    List<Volume> findByInstanceAndIsActive(Long volume, Long userId) throws Exception;

    /**
     * list by volumes by its volume type.
     *
     * @param userId user details
     * @return Upload Volume
     * @throws Exception exception
     */
    List<Volume> findByVolumeTypeAndIsActive(Long userId) throws Exception;

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
     * @param isActive -true
     * @return vmInstance list.
     */
    List<Volume> findByDepartmentAndIsActive(@Param("id") Long departmentId, Boolean isActive);

    /**
     * Get the volumes based on project.
     *
     * @param projectId project id.
     * @param volumeType volume type.
     * @return volume
     */
    List<Volume> findByProjectAndVolumeType(Long projectId, List<VolumeType> volumeType);

    /**
     * Get the volumes based on department.
     *
     * @param departmentId department id.
     * @return volume
     * @param volumeType volume type.
     * @throws Exception error occurs.
     */
    List<Volume> findByDepartmentAndVolumeType(Long departmentId, List<VolumeType> volumeType);

    /**
     * Get the volumes based on department not project.
     *
     * @param departmentId department id.
     * @param projectId project id.
     * @param volumeType volume type.
     * @return volume
     * @throws Exception error occurs.
     */
    List<Volume> findByDepartmentAndNotProjectAndVolumeType(Long departmentId, Long projectId,
            List<VolumeType> volumeType);

    /**
     * Get the count of the volume based on the attached.
     *
     * @param userId user details
     * @return volume count
     * @throws NumberFormatException Number format
     * @throws Exception error occurs
     */
    Integer findAttachedCount(Long userId) throws NumberFormatException, Exception;

    /**
     * Get the count of the volume based on the detached.
     *
     * @param userId user details
     * @return volume count
     * @throws NumberFormatException Number format
     * @throws Exception error occurs
     */
    Integer findDetachedCount(Long userId) throws NumberFormatException, Exception;

    /**
     * list by instance attached to volume.
     *
     * @param volume Volume
     * @return volume Volumes from instance.
     * @throws Exception exception
     */
    List<Volume> findByInstanceForResourceState(Long volume) throws Exception;

    /**
     * Save method for volume creation.
     *
     * @param volume Volume
     * @param userId user details
     * @return volume
     * @throws Exception exception
     */
    Volume saveVolume(Volume volume, Long userId) throws Exception;

    /**
     * Find volume by name and is Active status.
     *
     * @param volume object.
     * @param domainId of the domain.
     * @param userId of the user.
     * @param isActive status of the volume.
     * @return volume
     */
    Volume findByNameAndIsActive(String volume, Long domainId, Long userId, Boolean isActive);

    /**
     * Find all volumes by isActive.
     *
     * @param isActive status of the volume.
     * @return volume.
     * @throws Exception error occurs
     */
    List<Volume> findAllByIsActive(Boolean isActive) throws Exception;

    /**
     * Find all the domain based Volume list.
     *
     * @param domainId domain id of the volume
     * @param page pagination and sorting values.
     * @return list of volumes with pagination.
     * @throws Exception error occurs
     */
    Page<Volume> findAllByDomainId(Long domainId, PagingAndSorting page) throws Exception;

    /**
     * Get the count of the volume based on the attached.
     *
     * @param domainId user details
     * @return volume count
     * @throws NumberFormatException Number format
     * @throws Exception error occurs
     */
    Integer findAttachedCountByDomain(Long domainId) throws NumberFormatException, Exception;

}
