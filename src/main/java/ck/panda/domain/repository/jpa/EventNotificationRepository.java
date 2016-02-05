package ck.panda.domain.repository.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Event;

/**
 * Jpa Repository for Event entity.
 */
@Service
public interface EventNotificationRepository extends PagingAndSortingRepository<Event, Long> {

    /**
     * Find Event by id.
     *
     * @param id event id.
     * @return id
     */
    @Query(value = "SELECT event FROM Event event WHERE event.id LIKE :id ")
    Event findById(@Param("id") Long id);

    /**
     * Get event based on the jobId.
     *
     * @param jobId of the event.
     * @return event.
     */
    @Query(value = "SELECT event FROM Event event WHERE event.jobId = :jobId")
    Event findByJobId(@Param("jobId") String jobId);
}
