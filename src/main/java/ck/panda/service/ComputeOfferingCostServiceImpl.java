package ck.panda.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.ComputeOfferingCost;
import ck.panda.domain.repository.jpa.ComputeOfferingCostRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;
import ck.panda.util.error.exception.EntityNotFoundException;

/**
 * Cost for each compute offering is calculated using compute offering cost service.
 *
 */
@Service
public class ComputeOfferingCostServiceImpl implements ComputeOfferingCostService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** Department repository reference. */
    @Autowired
    private ComputeOfferingCostRepository costRepo;

    @Override
    public ComputeOfferingCost save(ComputeOfferingCost cost) throws Exception {

        Errors errors = validator.rejectIfNullEntity("computecost", cost);
        errors = validator.validateEntity(cost, errors);

        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            return costRepo.save(cost);
        }
    }

    @Override
    public ComputeOfferingCost update(ComputeOfferingCost cost) throws Exception {
        Errors errors = validator.rejectIfNullEntity("computecost", cost);
        errors = validator.validateEntity(cost, errors);

        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            return costRepo.save(cost);
        }
    }

    @Override
    public void delete(ComputeOfferingCost cost) throws Exception {
        costRepo.delete(cost);
    }

    @Override
    public void delete(Long id) throws Exception {
        costRepo.delete(id);
    }

    @Override
    public ComputeOfferingCost find(Long id) throws Exception {
        ComputeOfferingCost cost = costRepo.findOne(id);

        LOGGER.debug("Sample Debug Message");
        LOGGER.trace("Sample Trace Message");

        if (cost == null) {
            throw new EntityNotFoundException("department.not.found");
        }
        return cost;

    }

    @Override
    public Page<ComputeOfferingCost> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        System.out.println(pagingAndSorting.toPageRequest());
        return costRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<ComputeOfferingCost> findAll() throws Exception {
        return null;
    }
}
