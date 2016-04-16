package ck.panda.domain.repository.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.ManualCloudSync;

/**
 * JPA Repository for Manual Cloud Sync entity.
 */
public interface ManualCloudSyncRepository extends PagingAndSortingRepository<ManualCloudSync, Long> {

    /**
     * Find manual sync data by key value.
     *
     * @param keyName sync key name
     * @return manual sync object.
     */
    @Query(value = "SELECT cloudSync FROM ManualCloudSync cloudSync WHERE cloudSync.keyName = :keyName")
    ManualCloudSync findBySyncName(@Param("keyName") String keyName);
}
