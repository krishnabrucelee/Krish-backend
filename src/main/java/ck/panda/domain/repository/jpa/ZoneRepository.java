package ck.panda.domain.repository.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.Zone;

/**
 * JPA repository for Zone entity.
 */
public interface ZoneRepository extends PagingAndSortingRepository<Zone, Long> {

	/** Get the zone based on the uuid */
    @Query(value = "select zone from Zone zone where zone.uuid = :uuid")
    Zone findByUUID(@Param("uuid") String uuid);
}
