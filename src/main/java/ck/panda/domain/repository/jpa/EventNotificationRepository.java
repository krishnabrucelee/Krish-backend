package ck.panda.domain.repository.jpa;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Event;
import ck.panda.domain.entity.Event.EventType;
import ck.panda.domain.entity.Event.Status;

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
     * @return list of events.
     */
    @Query(value = "SELECT event FROM Event event WHERE event.jobId = :jobId ORDER BY event.eventDateTime DESC")
    List<Event> findByJobId(@Param("jobId") String jobId);

    /**
     * Get all events based on job id.
     *
     * @param jobId of the event.
     * @return list of events.
     */
    @Query(value = "SELECT event FROM Event event WHERE event.jobId = :jobId ORDER BY event.eventDateTime DESC")
    List<Event> findAllByEventType(@Param("jobId") String jobId, Pageable pageable);

    /**
     * Get all event based on user and eventType and active and not archive.
     *
     * @param ownerId of the event.
     * @param eventType type of the event.
     * @param isActive active status for event.
     * @param isArchive archive status for event .
     * @param pageable page request.
     * @return list of events.
     */
    @Query(value = "SELECT event FROM Event event WHERE event.eventOwnerId = :ownerId AND event.eventType = :eventType AND event.isArchive = :isArchive AND event.isActive = :isActive ORDER BY event.eventDateTime DESC")
    Page<Event> findAllByUserAndEventTypeAndActiveAndArchiveWithPageRequest(@Param("ownerId") Long ownerId, Pageable pageable, @Param("eventType") EventType eventType, @Param("isActive") Boolean isActive, @Param("isArchive") Boolean isArchive);

    /**
     * Get all event based on user and eventType and active and not archive.
     *
     * @param isActive active status for event.
     * @param isArchive archive status for event .
     * @param eventType type of the event.
     * @param pageable page request.
     * @return list of events.
     */
    @Query(value = "SELECT event FROM Event event WHERE event.eventType = :eventType AND event.isArchive = :isArchive AND event.isActive = :isActive ORDER BY event.eventDateTime DESC")
    Page<Event> findAllByEventTypeAndActiveAndArchiveWithPageRequest(Pageable pageable, @Param("eventType") EventType eventType, @Param("isActive") Boolean isActive, @Param("isArchive") Boolean isArchive);

    /**
     * Get event based on the jobId.
     *
     * @param ownerId of the event.
     * @param startDate of the event.
     * @param endDate of the event.
     * @param pageable page request.
     * @return list of events.
     */
    @Query(value = "SELECT event FROM Event event WHERE event.eventOwnerId = :ownerId AND  event.eventDateTime >= :startDate AND event.eventDateTime <= :endDate  ORDER BY event.eventDateTime DESC")
    Page<Event> findAllByUserAndDateRangeWithPageRequest(@Param("ownerId") Long ownerId, @Param("startDate") ZonedDateTime startDate, @Param("endDate") ZonedDateTime endDate, Pageable pageable);

    /**
     * Get event based on the event type and user  of the event.
     *
     * @param eventType of the event.
     * @param ownerId event owner.
     * @return list of events.
     */
    @Query(value = "SELECT event FROM Event event WHERE event.eventType = :eventType AND event.eventOwnerId = :ownerId ORDER BY event.eventDateTime DESC")
    List<Event> findAllByEventTypeAndOwner(@Param("eventType") Event.EventType eventType, @Param("ownerId") Long ownerId);

    /**
     * Get event based on the event type and user  of the event.
     *
     * @param eventDate of the event.
     * @param ownerId event owner.
     * @return list of events.
     */
    @Query(value = "SELECT event FROM Event event WHERE event.eventDateTime = :eventDate AND event.eventOwnerId = :ownerId ORDER BY event.eventDateTime DESC")
    List<Event> findAllByOwnerAndEventDate(@Param("eventDate") Date eventDate, @Param("ownerId") Long ownerId);

    /**
     * Get event based on the jobId and status.
     *
     * @param jobId of the event.
     * @param status of the event.
     * @return event.
     */
    @Query(value = "SELECT event FROM Event event WHERE event.status = :status AND event.jobId = :jobId")
    Event findAllByStatusAndJobId(@Param("status") Event.Status status, @Param("jobId") String jobId);

    /**
     * Get event based on the event name and owner and status.
     *
     * @param ownerId event owner id.
     * @param eventName name of the event.
     * @param status status of the event.
     * @return event.
     */
    @Query(value = "SELECT event FROM Event event WHERE event.eventOwnerId = :ownerId AND event.status = :status AND event.event = :eventName ORDER BY event.eventDateTime DESC")
    List<Event> findByUserAndEventAndStatus(@Param("ownerId") Long ownerId, @Param("eventName") String eventName, @Param("status") Event.Status status);

    /**
     * Get event based on the event job id and owner and status.
     *
     * @param ownerId event owner id.
     * @param jobId name of the event.
     * @param status status of the event.
     * @param eventType type of the event.
     * @param uuid resource's uuid.
     * @return event.
     */
    @Query(value = "SELECT event FROM Event event WHERE event.eventOwnerId = :ownerId AND event.status = :status AND event.jobId = :jobId AND event.eventType = :eventType AND event.resourceUuid = :uuid")
    Event findByUserAndJobIdAndStatusAndType(@Param("ownerId") Long ownerId, @Param("jobId") String jobId, @Param("status") Event.Status status, @Param("eventType") Event.EventType eventType, @Param("uuid") String uuid);

    /**
     * To get events from cloudstack server by jobid and Type.
     *
     * @param status status id of the event.
     * @param Id id of the event.
     * @param eventType type of the event.
     * @param uuid resource's uuid.
     * @return events.
     * @throws Exception unhandled errors.
     */
    @Query(value = "SELECT event FROM Event event WHERE event.status = :status AND event.resourceUuid = :resourceUuid AND event.eventType = :eventType AND event.resourceUuid = :resourceUuid AND event.id < :Id ORDER BY event.id DESC ")
    List<Event> findByUuidAndStatusAndType(@Param("Id") Long Id, @Param("status") Status status, @Param("eventType") EventType eventType, @Param("resourceUuid") String uuid) throws Exception;

    /**
     * To get events from cloudstack server by jobid and Type.
     *
     * @param status status id of the event.
     * @param Id id of the event.
     * @param eventType type of the event.
     * @param uuid resource's uuid.
     * @return events.
     * @throws Exception unhandled errors.
     */
    @Query(value = "SELECT event FROM Event event WHERE event.status = :status AND event.resourceUuid = :resourceUuid AND event.eventType = :eventType AND event.resourceUuid = :resourceUuid AND event.message = :message AND event.eventDateTime = :eventDate ")
    List<Event> findByUuidAndStatusAndTypeAndMessage(@Param("message") String message, @Param("status") Status status, @Param("eventType") EventType eventType, @Param("resourceUuid") String uuid, @Param("eventDate") ZonedDateTime zonedDateTime) throws Exception;

    /**
     * Get event based on the event jobid and owner and status.
     *
     * @param ownerId event owner id.
     * @param jobId name of the event.
     * @param status status of the event.
     * @return event.
     */
    @Query(value = "SELECT event FROM Event event WHERE event.eventOwnerId = :ownerId AND event.status = :status AND event.jobId = :jobId")
    Event findByUserAndJobIdAndState(@Param("ownerId") Long ownerId, @Param("jobId") String jobId, @Param("status") Event.Status status);


    /**
     * Get all event based on user and eventType and active and not archive.
     *
     * @param ownerId of the event.
     * @param eventType type of the event.
     * @param isActive active status for event.
     * @param isArchive archive status for event .
     * @param pageable page request.
     * @return list of events.
     */
    @Query(value = "SELECT event  FROM Event event LEFT JOIN event.eventOwner WHERE event.eventOwnerId != NULL AND event.eventType = :eventType AND event.isArchive = :isArchive AND event.isActive = :isActive ORDER BY event.eventDateTime DESC")
    Page<Event> findAllByRootAdmin(Pageable pageable, @Param("eventType") EventType eventType, @Param("isActive") Boolean isActive, @Param("isArchive") Boolean isArchive);

    /**
     * Get all event based on user and eventType and active and not archive.
     *
     * @param ownerId of the event.
     * @param eventType type of the event.
     * @param isActive active status for event.
     * @param isArchive archive status for event .
     * @param pageable page request.
     * @return list of events.
     */
    @Query(value = "SELECT event  FROM Event event LEFT JOIN event.eventOwner WHERE event.eventOwnerId != NULL AND event.eventType = :eventType AND event.isArchive = :isArchive AND event.isActive = :isActive ORDER BY event.eventDateTime DESC")
    List<Event> findAllCountByRootAdmin(@Param("eventType") EventType eventType, @Param("isActive") Boolean isActive, @Param("isArchive") Boolean isArchive);

    /**
     * Get all event based on user and eventType and active and not archive.
     *
     * @param ownerId of the event.
     * @param eventType type of the event.
     * @param isActive active status for event.
     * @param isArchive archive status for event .
     * @param pageable page request.
     * @return list of events.
     */
    @Query(value = "SELECT event FROM Event event WHERE event.eventOwnerId = :ownerId AND event.eventType = :eventType AND event.isArchive = :isArchive AND event.isActive = :isActive ORDER BY event.eventDateTime DESC")
    List<Event> findAllByUserAndEventTypeCount(@Param("ownerId") Long ownerId, @Param("eventType") EventType eventType, @Param("isActive") Boolean isActive, @Param("isArchive") Boolean isArchive);

}
