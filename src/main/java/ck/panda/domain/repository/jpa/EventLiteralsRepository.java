package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.EventLiterals;

/**
 * Jpa Repository for Event Literals entity.
 *
 */
@Service
public interface EventLiteralsRepository extends PagingAndSortingRepository<EventLiterals, Long> {

     /**
     * List event literals by event Type.
     *
     * @param eventName of the template.
     * @return event literals.
     * @throws Exception if error occurs.
     */
    @Query(value = "SELECT event FROM EventLiterals event WHERE event.eventName = :type")
    List<EventLiterals> findByEventType(@Param("type") String type)throws Exception;

    /**
     * List all template by active status.
     *
     * @param isActive status of the template.
     * @return event literals.
     * @throws Exception if error occurs.
     */
    @Query(value = "SELECT DISTINCT new map(event.eventName as eventName) FROM EventLiterals event WHERE event.isActive = :isActive")
    List<EventLiterals> findByIsActive(@Param("isActive") Boolean isActive);

    @Query(value = "SELECT event FROM EventLiterals event WHERE event.isActive = :isActive")
    List<EventLiterals> findAllByIsActive(@Param("isActive") Boolean isActive);
}
