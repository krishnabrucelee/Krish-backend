package ck.panda.domain.repository.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.TemplateCost;

/**
 * JPA repository for Template cost entity.
 */
public interface TemplateCostRepository extends PagingAndSortingRepository<TemplateCost, Long> {

    /**
     * Find template cost using template id and updated cost.
     *
     * @param templateCostId of the template
     * @param cost of the template
     * @return template cost
     */
    @Query(value = "SELECT templateCost FROM TemplateCost templateCost WHERE templateCost.templateCostId = :templateCostId AND templateCost.cost = :cost")
    TemplateCost findByTemplateCost(@Param("templateCostId") Long templateCostId, @Param("cost") Integer cost);
}
