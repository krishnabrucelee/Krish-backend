package ck.panda.domain.repository.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Pod;

/**
 * Jpa Repository for Pod entity.
 *
 */
@Service
public interface PodRepository extends PagingAndSortingRepository<Pod, Long> {

     /**
     * Get the pod based on the uuid.
     *
     * @param uuid of the zone
     * @return zone
     */
    @Query(value = "select pod from Pod pod where pod.uuid = :uuid")
    Pod findByUUID(@Param("uuid") String uuid);
}
