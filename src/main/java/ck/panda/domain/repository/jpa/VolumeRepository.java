/**
 *
 */
package ck.panda.domain.repository.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import ck.panda.domain.entity.StorageOffering;
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
    Page<StorageOffering> findAllByActive(Pageable pageable);

}
