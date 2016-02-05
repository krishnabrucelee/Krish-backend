package ck.panda.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Event;
import ck.panda.domain.entity.Event.EventType;
import ck.panda.domain.entity.Event.Status;
import ck.panda.domain.entity.User;
import ck.panda.util.domain.CRUDService;

/**
 * Service interface for VmIpaddress entity. *
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
     * Find event by user and event type.
     *
     * @param owner owner of event.
     * @return event.
     * @throws Exception if error occurs.
     */
    List<Event> findByUserAndEventType(User owner, EventType eventType) throws Exception;

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
     * @param owner owner of event.
     * @param eventName name of event
     * @param status status of event.
     * @return events.
     * @throws Exception unhandled errors.
     */
    List<Event> findByUserAndEventAndStatus(User owner, String eventName, Status status) throws Exception;

    /**
     * To get events from cloudstack server by jobid and Type.
     *
     * @param owner owner of the event.
     * @param status status id of the event.
     * @param eventType type of the event.
     * @return events.
     * @throws Exception unhandled errors.
     */
    List<Event> findByUserAndJobIdAndStatusAndType(User owner, Status status, EventType eventType) throws Exception;

    /**
     * To get event from cloudstack server by jobid and status.
     *
     * @param owner owner of event.
     * @param jobId job id of event.
     * @param status job id of event.
     * @return event.
     * @throws Exception unhandled errors.
     */
    Event findByUserAndJobIdAndState(User owner, String jobId, Status status) throws Exception;

    /**
     * To get event from cloudstack server by jobid and status and event name.
     *
     * @param owner owner of event.
     * @param eventName event name.
     * @param jobId job id of event.
     * @param status job id of event.
     * @return event.
     * @throws Exception unhandled errors.
     */
    Event findByUserAndEventAndJobIdAndState(User owner, String eventName, String jobId, Status status)
            throws Exception;

    /**
     * Soft delete for vm Ip address.
     *
     * @param vmIpaddress object.
     * @return vm Ip address.
     * @throws Exception if error occurs.
     */
    Event softDelete(Event event) throws Exception;
}
