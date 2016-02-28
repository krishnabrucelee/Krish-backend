package ck.panda.domain.repository.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.LbStickinessPolicy;

/**
 * Jpa Repository for LbStickinessPolicy entity.
 *
 */
@Service
public interface LbStickinessPolicyRepository extends PagingAndSortingRepository<LbStickinessPolicy, Long> {

    /**
     * Get the pod based on the uuid.
     *
     * @param uuid of the zone
     * @return zone
     */
    @Query(value = "select lb from LbStickinessPolicy lb where lb.uuid = :uuid")
    LbStickinessPolicy findByUUID(@Param("uuid") String uuid);

}