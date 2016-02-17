package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ck.panda.domain.entity.StorageOffering;

/**
 * Jpa Repository for Storage Offering entity.
 */
@Repository
public interface StorageOfferingRepository extends PagingAndSortingRepository<StorageOffering, Long> {

    /**
     * method to find list of entities having active status.
     *
     * @param pageable storage offer page
     * @param isActive true / false
     * @return lists Active state storage offerings
     */
    @Query(value = "SELECT storage FROM StorageOffering storage WHERE storage.isActive = :isActive")
    Page<StorageOffering> findAllByActive(Pageable pageable, @Param("isActive") Boolean isActive);

    /**
     * Get the Storage offer based on the uuid.
     *
     * @param uuid of the Storage offer.
     * @return Storage offer
     */
    @Query(value = "SELECT storage FROM StorageOffering storage WHERE storage.uuid = :uuid")
    StorageOffering findByUUID(@Param("uuid") String uuid);

    /**
     * Get the Storage offer Tags based on the uuid.
     *
     * @param isActive of the Storage offer.
     * @return Storage offer tag storageTags
     */
    @Query(value = "SELECT DISTINCT (storage.storageTags) FROM StorageOffering storage WHERE storage.isActive = :isActive AND (storage.storageTags) IS NOT NULL")
    List<String> findByTags(@Param("isActive") Boolean isActive);

    /**
     * Get the List of Storage offer based on the tags.
     *
     * @param tags of the Storage offer.
     * @param domainId domain id
     * @param isActive of the Storage offer.
     * @return list of Storage offer
     */
    @Query(value = "SELECT storage FROM StorageOffering storage WHERE (storage.storageTags = :storageTags OR 'ALL' = :storageTags) AND storage.isActive = :isActive AND (storage.domainId = :domainId OR storage.domainId IS NULL)")
    List<StorageOffering> findAllByTags(@Param("storageTags") String tags, @Param("domainId") Long domainId, @Param("isActive") Boolean isActive);

    /**
     * Get the Storage offering based on the name.
     *
     * @param name of the Storage
     * @param isActive of the Storage
     * @return Storage offering
     */
    @Query(value = "SELECT storage FROM StorageOffering storage WHERE storage.isActive = :isActive AND storage.name = :name")
    StorageOffering findByNameAndIsActive(@Param("name") String name, @Param("isActive") Boolean isActive);

    /**
     * Get the Storage offer Tags based on the domain.
     *
     * @param isActive of the Storage offer.
     * @param domainId domain id
     * @return Storage offer tag storageTags
     */
    @Query(value = "SELECT DISTINCT (storage.storageTags) FROM StorageOffering storage WHERE storage.isActive = :isActive AND (storage.storageTags) IS NOT NULL AND (storage.domainId = :domainId OR storage.domainId IS NULL)")
    List<String> findTagsByDomain(@Param("domainId") Long domainId, @Param("isActive") Boolean isActive);
}
