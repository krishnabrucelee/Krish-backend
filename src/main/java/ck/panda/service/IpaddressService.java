package ck.panda.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.IpAddress;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Service interface for ipaddress entity.
 */
@Service
public interface IpaddressService extends CRUDService<IpAddress> {

    /**
     * Find ipaddress by uuid.
     *
     * @param uuid of ipaddress.
     * @return ipaddress object.
     * @throws Exception unhandled errors.
     */
    IpAddress findbyUUID(String uuid) throws Exception;

    /**
     * Soft delete method for ipaddress.
     *
     * @param ipaddress for network
     * @return ipaddress.
     * @throws Exception unhandled errors.
     */
    IpAddress softDelete(IpAddress ipaddress) throws Exception;

    /**
     * List by network acquired to ipaddress.
     *
     * @param networkId network id.
     * @return list of ipaddresses.
     * @throws Exception exception
     */
    List<IpAddress> findByNetwork(Long networkId) throws Exception;

    /**
     * Find all ipaddress from CloudStack.
     *
     * @return list of ipaddresses.
     * @throws Exception unhandled errors.
     */
    List<IpAddress> findAllFromCSServer() throws Exception;

    /**
     * Paging and Sorting for displaying more number of elements in list.
     *
     * @param pagingAndSorting sortable method.
     * @return sorted values of ipaddresses.
     * @throws Exception unhandled errors.
     */
    Page<IpAddress> findAllByActive(PagingAndSorting pagingAndSorting) throws Exception;

}
