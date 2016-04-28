package ck.panda.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.StorageOfferingCost;
import ck.panda.domain.repository.jpa.StorageOfferingCostRepository;
import ck.panda.util.JsonUtil;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Cost for each storage offering is calculated using storage offering cost service.
 *
 */
@Service
public class StorageOfferingCostServiceImpl implements StorageOfferingCostService {

    /** Storage offering cost repository reference. */
    @Autowired
    private StorageOfferingCostRepository costRepo;

    @Override
    public StorageOfferingCost save(StorageOfferingCost cost) throws Exception {
            return costRepo.save(cost);
    }

    @Override
    public StorageOfferingCost update(StorageOfferingCost cost) throws Exception {
            return costRepo.save(cost);
    }

    @Override
    public void delete(StorageOfferingCost cost) throws Exception {
        costRepo.delete(cost);
    }

    @Override
    public void delete(Long id) throws Exception {
        costRepo.delete(id);
    }

    @Override
    public StorageOfferingCost find(Long id) throws Exception {
        return costRepo.findOne(id);
    }

    @Override
    public Page<StorageOfferingCost> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        System.out.println(pagingAndSorting.toPageRequest());
        return costRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<StorageOfferingCost> findAll() throws Exception {
        return (List<StorageOfferingCost>) costRepo.findAll();
    }

    @Override
    public Double totalcost(StorageOfferingCost storageCost) throws Exception {
            Double instanceStoppageCostforGB = JsonUtil.getDoubleValue(storageCost.getCostGbPerMonth());
            Double instanceStoppageCostperGB = JsonUtil.getDoubleValue(storageCost.getCostPerMonth());
            Double total = instanceStoppageCostforGB + instanceStoppageCostperGB;
            return total;
    }

    @Override
    public StorageOfferingCost findByCostAndId(Long storageId, Double totalCost) {
        return costRepo.findByStorageAndTotalCost(storageId, totalCost);
    }

    @Override
    public List<StorageOfferingCost> findByStorageId(Long storageId) {
        return costRepo.findByStorageId(storageId);
    }
}
