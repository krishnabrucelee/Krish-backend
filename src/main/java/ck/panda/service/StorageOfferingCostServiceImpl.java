package ck.panda.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.StorageOfferingCost;
import ck.panda.domain.repository.jpa.StorageOfferingCostRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.JsonUtil;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;
import ck.panda.util.error.exception.EntityNotFoundException;

/**
 * Cost for each storage offering is calculated using storage offering cost service.
 *
 */
@Service
public class StorageOfferingCostServiceImpl implements StorageOfferingCostService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** Department repository reference. */
    @Autowired
    private StorageOfferingCostRepository costRepo;

    @Override
    public StorageOfferingCost save(StorageOfferingCost cost) throws Exception {

        Errors errors = validator.rejectIfNullEntity("computecost", cost);
        errors = validator.validateEntity(cost, errors);

        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            return costRepo.save(cost);
        }
    }

    @Override
    public StorageOfferingCost update(StorageOfferingCost cost) throws Exception {
        Errors errors = validator.rejectIfNullEntity("computecost", cost);
        errors = validator.validateEntity(cost, errors);

        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            return costRepo.save(cost);
        }
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
        StorageOfferingCost cost = costRepo.findOne(id);

        LOGGER.debug("Sample Debug Message");
        LOGGER.trace("Sample Trace Message");

        if (cost == null) {
            throw new EntityNotFoundException("department.not.found");
        }
        return cost;

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

            Double instanceStoppageCostIops = JsonUtil.getDoubleValue(storageCost.getCostPerIops());
            Double instanceStoppageCostPerIops = JsonUtil.getDoubleValue(storageCost.getCostPerMonth());
            Double instanceStoppageCostforGB = JsonUtil.getDoubleValue(storageCost.getCostGbPerMonth());
            Double instanceStoppageCostperGB = JsonUtil.getDoubleValue(storageCost.getCostPerMonth());
            Double total = instanceStoppageCostIops + instanceStoppageCostPerIops + instanceStoppageCostforGB + instanceStoppageCostperGB;
            return total;
    }

    @Override
    public StorageOfferingCost findByCostAndId(Long storageId, Double totalCost) {
        return costRepo.findByStorageAndTotalCost(storageId, totalCost);
    }
}
