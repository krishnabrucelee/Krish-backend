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
     * @param domainId domain id.
     * @param isActive of the Storage offer.
     * @return list of Storage offer
     */
    @Query(value = "SELECT storage FROM StorageOffering storage WHERE (storage.storageTags = :storageTags OR 'ALL' = :storageTags) AND (storage.domainId = :domainId OR storage.domainId IS NULL) AND storage.isActive = :isActive")
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

    /**
     * Method to find list of domain based storage offer list.
     *
     * @param domainId domain id
     * @param isActive true / false
     * @param pageable storage offer page
     * @return lists Active state storage offerings
     */
    @Query(value = "SELECT storage FROM StorageOffering storage WHERE storage.domainId = :domainId AND storage.isActive = :isActive")
    Page<StorageOffering> findAllByDomainIdAndIsActive(@Param("domainId") Long domainId, @Param("isActive") Boolean isActive, Pageable pageable);

    /**
     * Find all storage offerings by domain id and search text along with pagination.
     *
     * @param domainId of the storage offering.
     * @param pagingAndSorting for pagination.
     * @param searchText for storage offering.
     * @return storage offering.
     */
    @Query(value = "SELECT storage FROM StorageOffering storage WHERE (storage.domainId = :domainId OR 0L = :domainId) AND storage.isActive = :isActive AND (storage.name LIKE %:search% OR storage.diskSize LIKE %:search% "
            + "OR storage.storageType LIKE %:search% OR storage.isCustomDisk LIKE %:search% )")
    Page<StorageOffering> findAllByDomainIdAndIsActiveAndSearchText(@Param("domainId") Long domainId, @Param("isActive") Boolean isActive, Pageable pageable,@Param("search") String searchText);
}
