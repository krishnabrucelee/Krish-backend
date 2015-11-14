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
   * @param pageable storage offer page
   * @return lists Active state storage offerings
   */
  @Query(value = "select storage from StorageOffering storage where storage.isActive IS TRUE")
  Page<StorageOffering> findAllByActive(Pageable pageable);


  /**
   * Get the Storage offer based on the uuid.
   *
   * @param uuid of the Storage offer.
   * @return Storage offer
   */
  @Query(value = "select storage from StorageOffering storage where storage.uuid = :uuid")
  StorageOffering findByUUID(@Param("uuid") String uuid);

  /**
   * Get the Storage offer Tags based on the uuid.
   *
   * @param isActive of the Storage offer.
   * @return Storage offer tag storageTags
   */
  @Query(value = "select distinct (storage.storageTags) from StorageOffering storage where storage.isActive = :isActive and (storage.storageTags) IS NOT NULL")
  List<String> findByTags(@Param("isActive") Boolean isActive);

  /**
   * Get the List of Storage offer based on the tags.
   *
   * @param tags of the Storage offer.
   * @return list of Storage offer
   */
  @Query(value = "select storage from StorageOffering storage where storage.storageTags = :storageTags")
  List<StorageOffering> findAllByTags(@Param("storageTags") String tags);

}
