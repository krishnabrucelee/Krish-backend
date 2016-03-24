package ck.panda.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.EventLiterals;
import ck.panda.domain.repository.jpa.EventLiteralsRepository;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * EventLiterals service implementation class.
 *
 */
@Service
public class EventLiteralServiceImpl implements EventLiteralsService {

    /** Email template repository reference. */
    @Autowired
    private EventLiteralsRepository eventRepo;


    @Override
    public EventLiterals save(EventLiterals email) throws Exception {
        return eventRepo.save(email);
    }

    @Override
    public EventLiterals update(EventLiterals email) throws Exception {
        return eventRepo.save(email);
    }

    @Override
    public void delete(EventLiterals email) throws Exception {
        eventRepo.delete(email);
    }

    @Override
    public void delete(Long id) throws Exception {
        eventRepo.delete(id);
    }

    @Override
    public EventLiterals find(Long id) throws Exception {
         return eventRepo.findOne(id);
    }

    @Override
    public Page<EventLiterals> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return eventRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<EventLiterals> findAll() throws Exception {
        return (List<EventLiterals>) eventRepo.findAll();
    }

    @Override
    public List<EventLiterals> findByType(String eventName) throws Exception {
        return (List<EventLiterals>) eventRepo.findByEventType(eventName);
    }

    @Override
    public List<EventLiterals> findByIsActive(Boolean isActive) throws Exception {
        return (List<EventLiterals>) eventRepo.findByIsActive(true);
    }
}
