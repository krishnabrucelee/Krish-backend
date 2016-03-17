package ck.panda.service;

import java.util.List;

import org.springframework.stereotype.Service;
import ck.panda.domain.entity.MiscellaneousCost;
import ck.panda.domain.entity.MiscellaneousCost.CostTypes;
import ck.panda.util.domain.CRUDService;

/**
 * Service class for miscellaneous cost.
 *
 */
@Service
public interface MiscellaneousCostService extends CRUDService<MiscellaneousCost> {

    List<MiscellaneousCost> findAllByIsActive(Boolean isActive) throws Exception;

    List<MiscellaneousCost> findAllByVolumeSnapshotType(CostTypes type) throws Exception;

    List<MiscellaneousCost> findAllByVmsnapshotType(CostTypes type) throws Exception;

    List<MiscellaneousCost> findAllByIpCostType(CostTypes type) throws Exception;

    List<MiscellaneousCost> findAllByTemplateCost(CostTypes type) throws Exception;
}