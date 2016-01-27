package ck.panda.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Tax;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Service interface for Tax entity.
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
     * @param pagingAndSorting page request.
     * @return Tax list.
     * @throws Exception unhandled exception.
     */
    Page<Tax> findAllByActive(PagingAndSorting pagingAndSorting) throws Exception;


    /**
     * Find all the tax with isActive status.
     *
     * @param isActive active/inactive
     * @return tax list.
     * @throws Exception error occurs.
     */
    List<Tax> findAllByIsActive(Boolean isActive) throws Exception;
}
