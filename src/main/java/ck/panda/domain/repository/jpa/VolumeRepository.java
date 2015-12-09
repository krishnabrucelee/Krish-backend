/**
 *
 */
package ck.panda.domain.repository.jpa;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.Volume;
import ck.panda.domain.entity.Volume.VolumeType;

/**
 * Jpa Repository for Volume entity.
 */
public interface VolumeRepository extends PagingAndSortingRepository<Volume, Long> {

    /**
     * method to find list of entities having active status.
     *
     * @param pageable volume list page
     * @return lists Active state volume
     */
    @Query(value = "select volume from Volume volume where volume.isActive IS TRUE")
    Page<Volume> findAllByActive(Pageable pageable);

    /**
     * Get the volume based on the uuid.
     *
     * @param uuid of the volume
     * @return volume
     */
    @Query(value = "select volume from Volume volume where volume.uuid = :uuid")
    Volume findByUUID(@Param("uuid") String uuid);


    /**
     * Get the volume based on the name.
     *
     * @param name of the volume
     * @param domainId domain id
     * @param departmentId department id
     * @param isActive of the volume
     * @return volume
     */
    @Query(value = "select volume from Volume volume where volume.isActive =:isActive AND volume.name =:name AND volume.domainId =:domainId AND volume.departmentId =:departmentId")
    Volume findByNameAndIsActive(@Param("name") String name, @Param("domainId") Long domainId, @Param("departmentId") Long departmentId, @Param("isActive")  Boolean isActive);

    /**
     * Find the Volume by Domain Id and IsActive.
     *
     * @param domainId for each domain.
     * @param isActive get the volume list based on active/inactive status.
     * @param pageable page
     * @return volume.
     */
    @Query(value = "select volume from Volume volume where volume.isActive =:isActive AND volume.domainId=:domainId")
    Page<Volume> findByDomainAndIsActive(@Param("domainId") Long domainId, @Param("isActive") Boolean isActive, Pageable pageable);

    /**
     * Find all the active or inactive departments with pagination.
     *
     * @param pageable to get the list with pagination.
     * @param isActive get the department list based on active/inactive status.
     * @return list of departments.
     */
    @Query(value = "select volume from Volume volume where volume.isActive =:isActive")
    Page<Volume> findAllByIsActive(Pageable pageable, @Param("isActive") Boolean isActive);

    /**
     * Find the Volume by instance Id and IsActive.
     *
     * @param domainId for each domain.
     * @param isActive get the volume list based on active/inactive status.
     * @param vmInstanceId Instance id
     * @return volume.
     */
    @Query(value = "select volume from Volume volume where volume.isActive =:isActive AND volume.vmInstanceId=:vmInstanceId AND volume.domainId=:domainId")
    List<Volume> findByInstanceAndDomainIsActive(@Param("domainId") Long domainId, @Param("vmInstanceId") Long vmInstanceId, @Param("isActive") Boolean isActive);

    /**
     * Find the Volume by instance Id and IsActive.
     *
     * @param vmInstanceId instance for each domain.
     * @param isActive get the volume list based on active/inactive status.
     * @return volume.
     */
    @Query(value = "select volume from Volume volume where volume.isActive =:isActive AND volume.vmInstanceId=:vmInstanceId")
    List<Volume> findByInstanceAndIsActive(@Param("vmInstanceId") Long vmInstanceId, @Param("isActive") Boolean isActive);

    /**
     * Find the Volume by volume Type and IsActive.
     *
     * @param domainId for each domain.
     * @param isActive get the volume list based on active/inactive status.
     * @param volumeType volume Type
     * @return volume.
     */
    @Query(value = "select volume from Volume volume where volume.isActive =:isActive AND volume.volumeType=:volumeType AND volume.domainId=:domainId")
    List<Volume> findByVolumeTypeAndIsActive(@Param("domainId") Long domainId, @Param("volumeType") VolumeType volumeType, @Param("isActive") Boolean isActive);

    /**
     * Find the Volume by volume Type and IsActive.
     *
     * @param volumeType for each domain.
     * @param isActive get the volume list based on active/inactive status.
     * @return volume.
     */
    @Query(value = "select volume from Volume volume where volume.isActive =:isActive AND volume.volumeType=:volumeType")
    List<Volume> findByVolumeTypeAndIsActive(@Param("volumeType") VolumeType volumeType, @Param("isActive") Boolean isActive);

}
