package ck.panda.service;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Event;
import ck.panda.domain.entity.Event.EventType;
import ck.panda.domain.entity.Event.Status;
import ck.panda.domain.repository.jpa.EventNotificationRepository;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Event notification service implementation class.
 */
@Service
public class EventNotificationServiceImpl implements EventNotificationService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EventNotificationServiceImpl.class);

    /** event repository reference. */
    @Autowired
    private EventNotificationRepository eventRepo;

    @Override
    public Event save(Event event) throws Exception {
        if (findByUuidAndStatusAndTypeAndMessage(event.getStatus(), event.getEventType(), event.getMessage(),
                event.getResourceUuid(), event.getEventDateTime()).size() > 0) {
            return null;
        }
        LOGGER.debug("event record");
        return eventRepo.save(event);
    }

    @Override
    public Event update(Event event) throws Exception {
        return eventRepo.save(event);
    }

    @Override
    public void delete(Event event) throws Exception {
        eventRepo.delete(event);
    }

    @Override
    public void delete(Long id) throws Exception {
        eventRepo.delete(id);
    }

    @Override
    public Event find(Long id) throws Exception {
        return eventRepo.findOne(id);
    }

    @Override
    public Page<Event> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return eventRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<Event> findAll() throws Exception {
        return (List<Event>) eventRepo.findAll();
    }

    @Override
    public List<Event> findByJobId(String jobId) throws Exception {
        return eventRepo.findByJobId(jobId);
    }

    @Override
    public List<Event> findByUserAndEventType(Long owner, EventType eventType) throws Exception {
        return eventRepo.findAllByEventTypeAndOwner(eventType, owner);
    }

    @Override
    public Event findByJobIdAndStatus(String jobId, Status status) throws Exception {
        return eventRepo.findAllByStatusAndJobId(status, jobId);
    }

    @Override
    public List<Event> findByUserAndEventAndStatus(Long ownerId, String eventName, Status status) throws Exception {
        return eventRepo.findByUserAndEventAndStatus(ownerId, eventName, status);
    }

    @Override
    public List<Event> findByUuidAndStatusAndTypeAndMessage(Status status, EventType eventType, String uuid,
            String message, ZonedDateTime zonedDateTime) throws Exception {
        return eventRepo.findByUuidAndStatusAndTypeAndMessage(message, status, eventType, uuid, zonedDateTime);
    }

    @Override
    public Event findByUserAndJobIdAndState(Long ownerId, String jobId, Status status) throws Exception {
        return eventRepo.findByUserAndJobIdAndState(ownerId, jobId, status);
    }

    @Override
    public Page<Event> findAllByOwnerIdAndEventTypeAndActiveAndExceptArchive(Long ownerId, PagingAndSorting pagingAndSorting, EventType eventType, Boolean isActive, Boolean isArchive) throws Exception {
        return eventRepo.findAllByUserAndEventTypeAndActiveAndArchiveWithPageRequest(ownerId, pagingAndSorting.toPageRequest(), eventType, isActive, isArchive );
    }

    @Override
    public List<Event> findAllByUserAndEventDate(Long ownerId, Date eventDate) throws Exception {
        return eventRepo.findAllByOwnerAndEventDate(eventDate, ownerId);
    }

    @Override
    public Page<Event> findAllByUserAndInBetweenEventDates(Long ownerId, ZonedDateTime startEventDate,
            ZonedDateTime endEventDate, PagingAndSorting pagingAndSorting) throws Exception {
        return eventRepo.findAllByUserAndDateRangeWithPageRequest(ownerId, startEventDate, endEventDate,
                pagingAndSorting.toPageRequest());
    }

    @Override
    public Event findByUuidAndStatusAndType(Status status, EventType eventType, String uuid, Long Id) throws Exception {
        List<Event> events = eventRepo.findByUuidAndStatusAndType(Id, status, eventType, uuid);
        if (events != null) {
            return events.get(0);
        }
        return null;
    }

    @Override
    public Page<Event> findAllByEventTypeAndActiveAndExceptArchive(EventType eventType,
            PagingAndSorting pagingAndSorting, Boolean isActive, Boolean isArchive) throws Exception {
        return eventRepo.findAllByEventTypeAndActiveAndArchiveWithPageRequest(pagingAndSorting.toPageRequest(), eventType, isActive, isArchive);
    }

    @Override
    public Page<Event> findEventListByRootAdmin(PagingAndSorting page, EventType eventType, boolean isActive, boolean isArchive) throws Exception {
        return eventRepo.findAllByRootAdmin(page.toPageRequest(), eventType, isActive, isArchive );

    }

    @Override
    public List<Event> findEventListCountByRootAdmin(EventType eventType, Boolean isActive, Boolean isArchive) throws Exception {
        return eventRepo.findAllCountByRootAdmin(eventType, isActive, isArchive );

    }

    @Override
    public List<Event> findAllByOwnerIdAndEventCount(Long ownerId, EventType eventType, Boolean isActive, Boolean isArchive) throws Exception {
        return eventRepo.findAllByUserAndEventTypeCount(ownerId,eventType, isActive, isArchive );
    }


}
