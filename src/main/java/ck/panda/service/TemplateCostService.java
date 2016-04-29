package ck.panda.service;

import java.util.List;

import org.springframework.stereotype.Service;
import ck.panda.domain.entity.TemplateCost;
import ck.panda.util.domain.CRUDService;

/**
 * Service interface for Template cost entity.
 *
 */
@Service
public interface TemplateCostService extends CRUDService<TemplateCost> {

    /**
     * Find template cost using template id and updated cost.
     *
     * @param templateId id of the template
     * @param cost of the template
     * @return template cost
     */
    TemplateCost findByTemplateCost(Long templateId, Double cost);

    /**
     * Find template cost using template id.
     *
     * @param templateId id of the template
     * @return template cost
     */
    List<TemplateCost> findAllByTemplateCost(Long templateId);

}
