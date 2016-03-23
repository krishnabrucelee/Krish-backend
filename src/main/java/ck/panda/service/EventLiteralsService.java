package ck.panda.service;

import java.util.List;

import org.springframework.stereotype.Service;
import ck.panda.domain.entity.EventLiterals;
import ck.panda.util.domain.CRUDService;

/**
 * Service interface for event literals entity.
 *
 */
@Service
public interface EventLiteralsService extends CRUDService<EventLiterals> {

    List<EventLiterals> findByType(String eventName) throws Exception;

    List<EventLiterals> findByIsActive(Boolean isActive) throws Exception;

}
