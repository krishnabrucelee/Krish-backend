package ck.panda.domain.repository.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import ck.panda.domain.entity.StorageOffering;

/**
 * Jpa Repository for Storage Offering entity.
 */
public interface StorageOfferingRepository extends PagingAndSortingRepository<StorageOffering, Long> {

  /**
   * method to find list of entities having active status.
   * @param pageable storage offer page
   * @return lists Active state storage offerings
   */
  @Query(value = "select storage from StorageOffering storage where storage.isActive IS TRUE")
  Page<StorageOffering> findAllByActive(Pageable pageable);

}
