package ck.panda.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.Event;
import ck.panda.domain.entity.Event.EventType;
import ck.panda.domain.entity.Event.Status;
import ck.panda.domain.entity.User;
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

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    @Override
    public Event save(Event t) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Event update(Event t) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void delete(Event t) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(Long id) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public Event find(Long id) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Page<Event> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Event> findAll() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Event> findByJobId(String jobId) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Event> findByUserAndEventType(User owner, EventType eventType) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Event findByJobIdAndStatus(String jobId, Status status) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Event> findByUserAndEventAndStatus(User owner, String eventName, Status status) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Event> findByUserAndJobIdAndStatusAndType(User owner, Status status, EventType eventType)
            throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Event findByUserAndJobIdAndState(User owner, String jobId, Status status) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Event findByUserAndEventAndJobIdAndState(User owner, String eventName, String jobId, Status status)
            throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Event softDelete(Event event) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }


}