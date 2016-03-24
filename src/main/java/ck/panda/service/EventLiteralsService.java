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

    /**
     * List event literals by event Type.
     *
     * @param eventName of the template.
     * @return event literals.
     * @throws Exception if error occurs.
     */
    List<EventLiterals> findByType(String eventName) throws Exception;

    /**
     * List all template by active status.
     *
     * @param isActive status of the template.
     * @return event literals.
     * @throws Exception if error occurs.
     */
    List<EventLiterals> findByIsActive(Boolean isActive) throws Exception;

}
