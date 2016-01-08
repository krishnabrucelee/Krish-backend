package ck.panda.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Tax;
import ck.panda.domain.repository.jpa.TaxRepository;
import ck.panda.util.domain.vo.PagingAndSorting;

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

    @Override
    public Tax save(Tax tax) throws Exception {
        tax.setIsActive(true);
        return taxRepo.save(tax);
    }

    @Override
    public Tax update(Tax tax) throws Exception {
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
    public Tax softDelete(Tax tax) throws Exception {
        tax.setIsActive(false);
        tax.setStatus(Tax.Status.DELETED);
        return taxRepo.save(tax);
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
