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

    /**
     * Dissociate IP address.
     *
     * @param ipAddress to be dissociated.
     * @return ip address.
     * @throws Exception if error occurs.
     */
    IpAddress dissocitateIpAddress(IpAddress ipAddress) throws Exception;

    /**
     * Acquire IP address.
     *
     * @param ipAddress to be associate with network.
     * @return ip address.
     * @throws Exception if error occurs.
     */
    List<IpAddress> acquireIP(Long networkId) throws Exception;

    /**
     * List by network acquired to ipaddress.
     *
     * @param networkId network id.
     * @param pagingAndSorting page request.
     * @return list of ipaddresses.
     * @throws Exception exception
     */
    Page<IpAddress> findByNetwork(Long networkId, PagingAndSorting pagingAndSorting) throws Exception;

}
