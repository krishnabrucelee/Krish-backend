package ck.panda.service;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Event;
import ck.panda.domain.entity.Event.EventType;
import ck.panda.domain.entity.Event.Status;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Service interface for Event notification.
 */
@Service
public interface EventNotificationService extends CRUDService<Event> {

    /**
     * Find event by job id.
     *
     * @param jobId job id of event.
     * @return event.
     * @throws Exception if error occurs.
     */
    List<Event> findByJobId(String jobId) throws Exception;

    /**
     * Find all event by event type.
     *
     * @param eventType type of event.
     * @param isActive active status for event.
     * @param isArchive archive status for event .
     * @return list of event.
     * @throws Exception if error occurs.
     */
    Page<Event> findAllByEventTypeAndActiveAndExceptArchive(EventType eventType, PagingAndSorting pagingAndSorting, Boolean isActive, Boolean isArchive) throws Exception;

    /**
     * Find all event by owner id.
     *
     * @param ownerId id of event owner.
     * @param eventType type of event type.
     * @param isActive active status for event.
     * @param isArchive archive status for event .
     * @param pagingAndSorting page request.
     * @return event list.
     * @throws Exception if error occurs.
     */
    Page<Event> findAllByOwnerIdAndEventTypeAndActiveAndExceptArchive(Long ownerId, PagingAndSorting pagingAndSorting, EventType eventType, Boolean isActive, Boolean isArchive) throws Exception;

    /**
     * Find event by user and event type.
     *
     * @param owner owner of event.
     * @return event.
     * @throws Exception if error occurs.
     */
    List<Event> findByUserAndEventType(Long owner, EventType eventType) throws Exception;

    /**
     * Find event by user and date.
     *
     * @param ownerId owner of event.
     * @param eventDate date of event.
     * @return event list.
     * @throws Exception if error occurs.
     */
    List<Event> findAllByUserAndEventDate(Long ownerId, Date eventDate) throws Exception;

    /**
     * Find event by job id.
     *
     * @param jobId job id of event.
     * @param status status of event.
     * @return events.
     * @throws Exception if error occurs.
     */
    Event findByJobIdAndStatus(String jobId, Status status) throws Exception;

    /**
     * To get events from cloudstack server by jobid.
     *
     * @param ownerId owner of event.
     * @param eventName name of event
     * @param status status of event.
     * @return events.
     * @throws Exception unhandled errors.
     */
    List<Event> findByUserAndEventAndStatus(Long ownerId, String eventName, Status status) throws Exception;

    /**
     * To get events from cloudstack server by uuid and Type and status.
     *
     * @param status status id of the event.
     * @param Id event id.
     * @param eventType type of the event.
     * @param uuid resource's uuid.
     * @return events.
     * @throws Exception unhandled errors.
     */
    Event findByUuidAndStatusAndType(Status status, EventType eventType, String uuid, Long Id) throws Exception;

    /**
     * To get events from cloudstack server by jobid and Type.
     *
     * @param ownerId owner of the event.
     * @param status status id of the event.
     * @param jobId job id of the event.
     * @param eventType type of the event.
     * @param uuid resource's uuid.
     * @return events.
     * @throws Exception unhandled errors.
     */
    List<Event> findByUuidAndStatusAndTypeAndMessage(Status status, EventType eventType, String uuid, String message, ZonedDateTime zonedDateTime) throws Exception;

    /**
     * To get event from cloudstack server by jobid and status.
     *
     * @param owner owner of event.
     * @param jobId job id of event.
     * @param status job id of event.
     * @return event.
     * @throws Exception unhandled errors.
     */
    Event findByUserAndJobIdAndState(Long ownerId, String jobId, Status status) throws Exception;
    /**
     * Find all event by owner id with date range.
     *
     * @param ownerId id of event owner.
     * @param pagingAndSorting page request.
     * @param startEventDate start event date.
     * @param endEventDate end event date.
     * @return event list.
     * @throws Exception if error occurs.
     */
    Page<Event> findAllByUserAndInBetweenEventDates(Long ownerId, ZonedDateTime startEventDate, ZonedDateTime endEventDate, PagingAndSorting pagingAndSorting) throws Exception;
}
