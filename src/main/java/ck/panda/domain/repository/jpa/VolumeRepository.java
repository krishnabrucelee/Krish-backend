/**
 *
 */
package ck.panda.domain.repository.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.Volume;

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
     * @param isActive of the volume
     * @return volume
     */
    @Query(value = "select volume from Volume volume where volume.isActive =:isActive AND volume.name =:name")
    Volume findByNameAndIsActive(@Param("name") String name, @Param("isActive")  Boolean isActive);
}
