package ck.panda.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Tax;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Service interface for Tax entity.
 *
 */
@Service
public interface TaxService extends CRUDService<Tax> {

    /**
     * Delete the tax.
     *
     * @param tax Tax entity.
     * @return Tax.
     * @throws Exception error occurs
     */
    Tax softDelete(Tax tax) throws Exception;

    /**
     * Find all the tax with status.
     *
     * @param pagingAndSorting
     * @return
     * @throws Exception
     */
    Page<Tax> findAllByActive(PagingAndSorting pagingAndSorting) throws Exception;
}