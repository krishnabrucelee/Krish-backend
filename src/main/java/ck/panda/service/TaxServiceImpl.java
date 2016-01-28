package ck.panda.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Tax;
import ck.panda.domain.repository.jpa.TaxRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;

/**
 * Service implementation for Tax entity.
 *
 */
@Service
public class TaxServiceImpl implements TaxService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TaxServiceImpl.class);

    /** Tax repository reference. */
    @Autowired
    private TaxRepository taxRepo;

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /**
     * Validate the tax.
     *
     * @param tax reference of the tax.
     * @throws Exception error occurs
     */
    private void validateTax(Tax tax) throws Exception {
        Errors errors = validator.rejectIfNullEntity("tax", tax);
        errors = validator.validateEntity(tax, errors);
        Tax t = taxRepo.findByNameAndIsActive(tax.getName(), true);
        if (t != null && tax.getId() != t.getId()) {
            errors.addFieldError("name", "tax.already.exist");
        }
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        }
    }

    @Override
    public Tax save(Tax tax) throws Exception {
        tax.setIsActive(true);
        this.validateTax(tax);
        return taxRepo.save(tax);
    }

    @Override
    public Tax update(Tax tax) throws Exception {
        this.validateTax(tax);
        return taxRepo.save(tax);
    }

    @Override
    public void delete(Tax tax) throws Exception {
        taxRepo.delete(tax);
    }

    @Override
    public void delete(Long id) throws Exception {
        taxRepo.delete(id);
    }

    @Override
    public Tax find(Long id) throws Exception {
        return taxRepo.findOne(id);
    }

    @Override
    public List<Tax> findAll() throws Exception {
        return (List<Tax>) taxRepo.findAll();
    }

    @Override
    public Page<Tax> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return taxRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<Tax> findAllByIsActive(Boolean isActive) throws Exception {
        return taxRepo.findAllByIsActive(isActive);
    }

    /**
     * Find all the tax with pagination.
     *
     * @throws Exception application errors.
     * @param pagingAndSorting do pagination with sorting for tax.
     * @return list of tax.
     */
    public Page<Tax> findAllByActive(PagingAndSorting pagingAndSorting) throws Exception {
        return taxRepo.findAllByIsActive(pagingAndSorting.toPageRequest(), true);
    }
}
