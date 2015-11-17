package ck.panda.domain.repository.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Iso;

/**
 * Jpa Repository for Iso entity.
 */
@Service
public interface IsoRepository extends PagingAndSortingRepository<Iso, Long> {

    /**
     * Find iso by uuid.
     *
     * @param uuid of iso.
     * @return iso object.
     */
    @Query(value = "select iso from Iso iso where iso.uuid = :uuid")
    Iso findByUUID(@Param("uuid") String uuid);

}
