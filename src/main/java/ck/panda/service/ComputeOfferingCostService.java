package ck.panda.service;

import org.springframework.stereotype.Service;

import ck.panda.domain.entity.ComputeOfferingCost;
import ck.panda.util.domain.CRUDService;

/**
 * Service class for Compute Offering cost.
 *
 */
@Service
public interface ComputeOfferingCostService extends CRUDService<ComputeOfferingCost> {

    /**
     * Find compute cost and id.
     *
     * @param computeId of the offering.
     * @param totalCost for the plan
     * @return computeoffering cost.
     */
    ComputeOfferingCost findByCostAndId(Long computeId, Double totalCost);

    /**
     * Calculate total cost of the compute offering.
     *
     * @param computecost for the particular offering
     * @return compute offering cost.
     * @throws Exception if error occurs.
     */
    Double totalcost(ComputeOfferingCost computecost) throws Exception;
}
