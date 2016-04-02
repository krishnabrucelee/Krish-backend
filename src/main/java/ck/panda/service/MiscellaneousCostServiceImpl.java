package ck.panda.service;

import java.util.List;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.constants.PingConstants;
import ck.panda.domain.entity.MiscellaneousCost;
import ck.panda.domain.entity.MiscellaneousCost.CostTypes;
import ck.panda.domain.entity.MiscellaneousCost.UnitType;
import ck.panda.domain.repository.jpa.MiscellaneousCostRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.PingService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;

/**
 * Miscellaneous cost for template, snapshot, ipaddress and other features..
 *
 */
@Service
public class MiscellaneousCostServiceImpl implements MiscellaneousCostService {

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** Department repository reference. */
    @Autowired
    private MiscellaneousCostRepository costRepo;

    /** Mr.ping service reference. */
    @Autowired
    private PingService pingService;

    @Override
    public MiscellaneousCost save(MiscellaneousCost cost) throws Exception {

        Errors errors = validator.rejectIfNullEntity("miscellaneouscost", cost);
        errors = validator.validateEntity(cost, errors);

        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
                if (cost.getCostType() == CostTypes.TEMPLATE) {
                    List<MiscellaneousCost> oldCost = costRepo.findByIsActiveAndCostType(true, CostTypes.TEMPLATE);
                    if (oldCost.size() != 0) {
                        for (MiscellaneousCost miscellaneous: oldCost) {
                            miscellaneous.setIsActive(false);
                        }
                    }
                    cost.setCostType(CostTypes.TEMPLATE);
                    cost.setUnitType(UnitType.GB);
                    cost.setIsActive(true);
                }
                if (cost.getCostType() == CostTypes.VMSNAPSHOT) {
                    List<MiscellaneousCost> oldCost = costRepo.findByIsActiveAndCostType(true, CostTypes.VMSNAPSHOT);
                    if (oldCost.size() != 0) {
                        for (MiscellaneousCost miscellaneous: oldCost) {
                            miscellaneous.setIsActive(false);
                        }
                    }
                    cost.setCostType(CostTypes.VMSNAPSHOT);
                    cost.setUnitType(UnitType.GB);
                    cost.setIsActive(true);
                }
                if (cost.getCostType() == CostTypes.IPADDRESS) {
                    List<MiscellaneousCost> oldCost = costRepo.findByIsActiveAndCostType(true, CostTypes.IPADDRESS);
                    if (oldCost.size() != 0) {
                        for (MiscellaneousCost miscellaneous: oldCost) {
                            miscellaneous.setIsActive(false);
                        }
                    }
                    cost.setCostType(CostTypes.IPADDRESS);
                    cost.setUnitType(UnitType.IP);
                    cost.setIsActive(true);
                }
                if (cost.getCostType() == CostTypes.VOLUMESNAPSHOT) {
                    List<MiscellaneousCost> oldCost = costRepo.findByIsActiveAndCostType(true, CostTypes.VOLUMESNAPSHOT);
                    if (oldCost.size() != 0) {
                        for (MiscellaneousCost miscellaneous: oldCost) {
                            miscellaneous.setIsActive(false);
                        }
                    }
                    cost.setCostType(CostTypes.VOLUMESNAPSHOT);
                    cost.setUnitType(UnitType.GB);
                    cost.setIsActive(true);
                }

                if (pingService.apiConnectionCheck(errors)) {
                    cost = costRepo.save(cost);
                    savePingProject(cost);
                }
                return cost;
        }
    }

    @Override
    public MiscellaneousCost update(MiscellaneousCost cost) throws Exception {
        Errors errors = validator.rejectIfNullEntity("miscellaneouscost", cost);
        errors = validator.validateEntity(cost, errors);

        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            return costRepo.save(cost);
        }
    }

    @Override
    public void delete(MiscellaneousCost cost) throws Exception {
        costRepo.delete(cost);
    }

    @Override
    public void delete(Long id) throws Exception {
        costRepo.delete(id);
    }

    @Override
    public MiscellaneousCost find(Long id) throws Exception {
        return costRepo.findOne(id);
    }

    @Override
    public Page<MiscellaneousCost> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return costRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<MiscellaneousCost> findAll() throws Exception {
        return (List<MiscellaneousCost>) costRepo.findAll();
    }

    @Override
    public List<MiscellaneousCost>  findAllByIsActive(Boolean isActive) throws Exception {
        return (List<MiscellaneousCost>)costRepo.findAllByIsActive(true);
    }

    @Override
    public List<MiscellaneousCost>  findAllByVmsnapshotType(CostTypes type) throws Exception {
        return (List<MiscellaneousCost>)costRepo.findByIsActiveAndCostType(true, CostTypes.VMSNAPSHOT);
    }

    @Override
    public List<MiscellaneousCost>  findAllByVolumeSnapshotType(CostTypes type) throws Exception {
        return (List<MiscellaneousCost>)costRepo.findByIsActiveAndCostType(true, CostTypes.VOLUMESNAPSHOT);
    }

    @Override
    public List<MiscellaneousCost>  findAllByIpCostType(CostTypes type) throws Exception {
        return (List<MiscellaneousCost>)costRepo.findByIsActiveAndCostType(true, CostTypes.IPADDRESS);
    }

    @Override
    public List<MiscellaneousCost>  findAllByTemplateCost(CostTypes type) throws Exception {
        return (List<MiscellaneousCost>)costRepo.findByIsActiveAndCostType(true, CostTypes.TEMPLATE);
    }

    /**
     * Set optional value for MR.ping api call.
     *
     * @param cost miscellaneous cost
     * @return status
     * @throws Exception raise if error
     */
    public Boolean savePingProject(MiscellaneousCost cost) throws Exception {
        JSONObject optional = new JSONObject();
        optional.put(PingConstants.PLAN_UUID, cost.getId());
        optional.put(PingConstants.NAME, cost.getCostType());
        optional.put(PingConstants.REFERENCE_NAME, cost.getCostType().toString());
        if (optional.get(PingConstants.REFERENCE_NAME).equals(PingConstants.TEMPLATE)) {
            optional.put(PingConstants.GROUP_NAME, PingConstants.TEMPLATE);
        }
        if (optional.get(PingConstants.REFERENCE_NAME).equals(PingConstants.IP_ADDRESS)) {
            optional.put(PingConstants.GROUP_NAME, PingConstants.IP_ADDRESS);
        }
        if (optional.get(PingConstants.REFERENCE_NAME).equals(PingConstants.VM_SNAPSHOT)
                || optional.get(PingConstants.REFERENCE_NAME).equals(PingConstants.VOLUME_SNAPSHOT)) {
            optional.put(PingConstants.GROUP_NAME, PingConstants.SNAPSHOT);
        }
        optional.put(PingConstants.TOTAL_COST, cost.getCostperGB());
        optional.put(PingConstants.ZONE_ID, cost.getZone().getUuid());
        pingService.addPlanCost(optional);
        return true;
    }
}
