package ck.panda.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.ComputeOffering;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Service class for Compute Offering.
 *
 */
@Service
public interface ComputeOfferingService  extends CRUDService<ComputeOffering>  {

    /**
     * To get list of compute offerings from cloudstack server.
     *
     * @return domain list from server
     * @throws Exception unhandled errors.
     */
    List<ComputeOffering> findAllFromCSServer() throws Exception;

    /**
     * Get the compute offer based on the uuid.
     *
     * @param uuid of the compute offer.
     * @return compute offer
     */
    ComputeOffering findByUUID(String uuid);

    /**
     * Paging and Sorting for displaying more number of elements in list.
     *
     * @param pagingAndSorting sortable method.
     * @return sorted values.
     * @throws Exception unhandled errors.
     */
    Page<ComputeOffering> findAllByActive(PagingAndSorting pagingAndSorting) throws Exception;

    /**
     * Soft delete for compute Offering.
     *
     * @param compute get compute offering id.
     * @return deleted compute id.
     * @throws Exception unhandled errors.
     */
    ComputeOffering softDelete(ComputeOffering compute) throws Exception;

}

