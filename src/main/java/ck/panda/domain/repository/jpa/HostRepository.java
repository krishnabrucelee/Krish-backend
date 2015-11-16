package ck.panda.domain.repository.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Host;

/**
 * Jpa Repository for Host entity.
 *
 */
@Service
public interface HostRepository extends PagingAndSortingRepository<Host, Long> {
    /**
     * Get the host based on the uuid.
     *
     * @param uuid of the host
     * @return host
     */
    @Query(value = "select host from Host host where host.uuid = :uuid")
    Host findByUUID(@Param("uuid") String uuid);
}
