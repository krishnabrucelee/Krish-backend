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

    /**
     * Find the Volume by volume Type and IsActive.
     *
     * @param vmInstanceId instance for each domain.
     * @param volumeType for each domain.
     * @param isActive get the volume list based on active/inactive status.
     * @return volume.
     */
    @Query(value = "select volume from Volume volume where volume.isActive =:isActive AND volume.volumeType=:volumeType AND volume.vmInstanceId=:vmInstanceId")
    List<Volume> findByInstanceAndVolumeTypeAndIsActive(@Param("vmInstanceId") Long vmInstanceId, @Param("volumeType") VolumeType volumeType, @Param("isActive") Boolean isActive);

    /**
     * Find the Volume by volume Type and IsActive.
     *
     * @param vmInstanceId instance for each domain.
     * @param volumeType for each domain.
     * @param isActive get the volume list based on active/inactive status.
     * @return volume.
     */
    @Query(value = "select volume from Volume volume where volume.isActive =:isActive AND volume.volumeType=:volumeType AND volume.vmInstanceId=:vmInstanceId")
    Volume findByInstanceAndVolumeType(@Param("vmInstanceId") Long vmInstanceId, @Param("volumeType") VolumeType volumeType, @Param("isActive") Boolean isActive);


    /**
     * Find all department from volume..
     *
     * @param departmentId department id.
       * @param isActive get the volume list based on active/inactive status.
     * @return department list.
     */
    @Query(value = "select volume from Volume volume where volume.departmentId=:id and volume.isActive =:isActive ")
    List<Volume> findByDepartmentAndIsActive(@Param("id") Long departmentId, @Param("isActive") Boolean isActive);

    /**
     * Get the volumes based on project.
     *
     * @param projectId project id.
     * @param volumeType volume type.
     * @param isActive true/false
     * @return project
     */
    @Query(value = "select volume from Volume volume where volume.projectId=:projectId and volume.volumeType in :volumeType and volume.isActive =:isActive and volume.vmInstanceId = NULL")
    List<Volume> findByProjectAndVolumeType(@Param("projectId") Long projectId, @Param("volumeType") List<VolumeType> volumeType, @Param("isActive") Boolean isActive);

    /**
     * Get the volumes based on department.
     *
     * @param departmentId department id.
     * @param isActive true/false
     * @param volumeType volume Type
     * @return department
     * @throws Exception error occurs.
     */
    @Query(value = "select volume from Volume volume where volume.departmentId=:departmentId and volume.volumeType in :volumeType and volume.isActive =:isActive and volume.projectId = NULL and volume.vmInstanceId = NULL")
    List<Volume> findByDepartmentAndVolumeType(@Param("departmentId") Long departmentId, @Param("volumeType") List<VolumeType> volumeType, @Param("isActive") Boolean isActive);

    /**
     * Get the volumes based on department and not project.
     *
     * @param departmentId department id.
     * @param projectId project id.
     * @param volumeType volume Type
     * @param isActive true/false
     * @return department
     * @throws Exception error occurs.
     */
    @Query(value = "select volume from Volume volume where volume.departmentId=:departmentId and volume.volumeType in :volumeType and volume.isActive =:isActive and volume.projectId<>:projectId")
    List<Volume> findByDepartmentAndNotProjectAndVolumeType(@Param("departmentId") Long departmentId, @Param("projectId") Long projectId, @Param("volumeType") List<VolumeType> volumeType, @Param("isActive") Boolean isActive);

    /**
     * Find all volumes by active state.
     *
     * @param isActive true/false
     * @return volumes
     */
    @Query(value = "select volume from Volume volume where volume.isActive =:isActive")
    List<Volume> findAllByActive(@Param("isActive") Boolean isActive);

    /**
     * Get volumes by Department and VolumeType.
     *
     * @param departmentId department id
     * @param volumeType volume Type
     * @param isActive true/false
     * @param pageable page
     * @return volumes
     */
    @Query(value = "select volume from Volume volume where volume.departmentId=:departmentId and volume.volumeType in :volumeType and volume.isActive =:isActive and volume.projectId = NULL")
    Page<Volume> findByDepartmentAndVolumeTypeAndPage(@Param("departmentId") Long departmentId, @Param("volumeType") List<VolumeType> volumeType,
            @Param("isActive") Boolean isActive, Pageable pageable);

    /**
     * Get the volumes based on project.
     *
     * @param projectId project id.
     * @param departmentId department id
     * @param volumeType volume type.
     * @param isActive true/false
     * @return project
     */
    @Query(value = "select volume from Volume volume where (volume.projectId=:projectId and volume.departmentId=:departmentId) or (volume.projectId = NULL and volume.departmentId=:departmentId) and volume.volumeType in :volumeType and volume.isActive =:isActive")
    List<Volume> findByProjectAndVolumeType(@Param("projectId") Long projectId, @Param("departmentId") Long departmentId, @Param("volumeType") List<VolumeType> volumeType, @Param("isActive") Boolean isActive);

    /**
     * Get Volume count from Domain and Instance.
     *
     * @param domainId domain id
     * @param vmInstanceId Instance id
     * @param datadisk volume type
     * @param isActive true/false
     * @return volume count
     */
    @Query(value = "select volume from Volume volume where volume.isActive =:isActive AND volume.volumeType=:volumeType AND volume.vmInstanceId=:vmInstanceId AND volume.domainId=:domainId")
    List<Volume> findVolumeCountByDomainAndInstanceId(@Param("domainId") Long domainId, @Param("vmInstanceId") Long vmInstanceId, @Param("volumeType") VolumeType datadisk, @Param("isActive") Boolean isActive);

    /**
     * Get Volume count from Instance.
     *
     * @param vmInstanceId Instance id
     * @param datadisk volume type
     * @param isActive true/false
     * @return volume count
     */
    @Query(value = "select volume from Volume volume where volume.isActive =:isActive AND volume.volumeType=:volumeType AND volume.vmInstanceId=:vmInstanceId")
    List<Volume> findVolumeCountByInstanceId(@Param("vmInstanceId") Long vmInstanceId, @Param("volumeType") VolumeType datadisk, @Param("isActive") Boolean isActive);

    /**
     * Get Project and Volume Type from volume.
     *
     * @param projectId project id
     * @param departmentId department id
     * @param volumeType volume type
     * @param isActive true/false
     * @return volume
     */
    @Query(value = "select volume from Volume volume where volume.projectId=:projectId and volume.departmentId=:departmentId and volume.volumeType=:volumeType and volume.isActive =:isActive")
    List<Volume> findByProjectAndVolumeTypeWithInstance(@Param("projectId") Long projectId, @Param("departmentId") Long departmentId, @Param("volumeType") List<VolumeType> volumeType, @Param("isActive") Boolean isActive);

    /**
     * Get Department and Volume Type Count.
     *
     * @param departmentId department id
     * @param volumeType volume type
     * @param isActive true/false
     * @return volume
     */
    @Query(value = "select volume from Volume volume where volume.departmentId=:departmentId and volume.volumeType in :volumeType and volume.isActive =:isActive and volume.projectId = NULL")
    List<Volume> findByDepartmentAndVolumeTypeCount(@Param("departmentId") Long departmentId, @Param("volumeType") List<VolumeType> volumeType,
            @Param("isActive") Boolean isActive);

}
