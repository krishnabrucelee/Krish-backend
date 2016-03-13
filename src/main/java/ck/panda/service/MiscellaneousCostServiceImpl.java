package ck.panda.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.MiscellaneousCost;
import ck.panda.domain.entity.MiscellaneousCost.CostTypes;
import ck.panda.domain.repository.jpa.MiscellaneousCostRepository;
import ck.panda.util.AppValidator;
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

    @Override
    public MiscellaneousCost save(MiscellaneousCost cost) throws Exception {

        Errors errors = validator.rejectIfNullEntity("miscellaneouscost", cost);
        errors = validator.validateEntity(cost, errors);

        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {

                MiscellaneousCost oldCost = costRepo.findByIsActive(true);
                if(oldCost.getIsActive()!= null) {
                 oldCost.setIsActive(false);
            }
                cost.setCostType(CostTypes.TEMPLATE);
                cost.setIsActive(true);
            return costRepo.save(cost);
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
}

