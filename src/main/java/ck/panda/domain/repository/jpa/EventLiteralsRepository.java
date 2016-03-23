package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.ComputeOffering;
import ck.panda.domain.entity.EventLiterals;

/**
 * Jpa Repository for Event Literals entity.
 *
 */
@Service
public interface EventLiteralsRepository extends PagingAndSortingRepository<EventLiterals, Long> {

    @Query(value = "SELECT DISTINCT event.eventName FROM EventLiterals event WHERE event.eventName = :type")
    List<EventLiterals> findByEventType(@Param("type") String type)throws Exception;

    @Query(value = "SELECT DISTINCT new map(event.eventName as eventName) FROM EventLiterals event WHERE event.isActive = :isActive")
    List<EventLiterals> findByIsActive(@Param("isActive") Boolean isActive);
}
