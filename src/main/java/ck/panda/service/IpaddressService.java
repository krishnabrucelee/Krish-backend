package ck.panda.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.IpAddress;
import ck.panda.domain.entity.Network;
import ck.panda.domain.entity.IpAddress.State;
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
     * List by network ipaddress.
     *
     * @param networkId network id.
     * @return list of ipaddresses.
     * @throws Exception exception
     */
    List<IpAddress> findByNetwork(Long networkId) throws Exception;

    /**
     * List by state based ipaddress.
     *
     * @param state state of ipaddress.
     * @param isActive active/inactive.
     * @return list of ipaddresses.
     * @throws Exception exception
     */
    List<IpAddress> findByStateAndActive(State state, Boolean isActive) throws Exception;

    /**
     * Find all ipaddress from CloudStack.
     *
     * @return list of ipaddresses.
     * @throws Exception unhandled errors.
     */
    List<IpAddress> findAllFromCSServer() throws Exception;

    /**
     * Update ipaddress from CloudStack by network id.
     *
     * @param networkId network's id.
     * @return list of ipaddresses.
     * @throws Exception unhandled errors.
     */
    IpAddress UpdateIPByNetwork(String networkId) throws Exception;

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
     * @param ipUuid of the IP address.
     * @return ip address.
     * @throws Exception if error occurs.
     */
    IpAddress dissocitateIpAddress(String ipUuid) throws Exception;

    /**
     * Dissociate IP address.
     *
     * @param ipUuid of the IP address.
     * @return ip address.
     * @throws Exception if error occurs.
     */
    IpAddress dissocitateIpAddressForVPC(String ipUuid) throws Exception;

    /**
     * Enable static NAT for IP address.
     *
     * @param ipAddressId to be enable static nat.
     * @param vmId virtual machine id.
     * @param ipaddress guest ipaddress.
     * @return ip address.
     * @throws Exception if error occurs.
     */
    IpAddress enableStaticNat(Long ipAddressId, Long vmId, String ipaddress) throws Exception;

    /**
     * Disable static NAT for IP address.
     *
     * @param ipAddressId for network.
     * @return ip address.
     * @throws Exception if error occurs.
     */
    IpAddress disableStaticNat(Long ipAddressId) throws Exception;

    /**
     * Acquire IP address.
     *
     * @param networkId for network.
     * @return ip address.
     * @throws Exception if error occurs.
     */
    List<IpAddress> acquireIP(Long networkId) throws Exception;

    /**
     * Acquire IP address.
     *
     * @param vpcId for vpc.
     * @return ip address.
     * @throws Exception if error occurs.
     */
    List<IpAddress> acquireVPCIP(Long vpcId) throws Exception;

    /**
     * List by network acquired to ipaddress.
     *
     * @param networkId network id.
     * @param pagingAndSorting page request.
     * @return list of ipaddresses.
     * @throws Exception exception
     */
    Page<IpAddress> findByNetwork(Long networkId, PagingAndSorting pagingAndSorting) throws Exception;

    /**
     * List by vpc acquired to ipaddress.
     *
     * @param vpcId vpc id.
     * @param pagingAndSorting page request.
     * @return list of ipaddresses.
     * @throws Exception exception
     */
    Page<IpAddress> findAllByVpc(Long vpcId, PagingAndSorting pagingAndSorting) throws Exception;

    /**
     * Enable remote access VPN for IP address.
     *
     * @param uuid of the IP address.
     * @return IP address.
     * @throws Exception if error occurs.
     */
    IpAddress enableRemoteAccessVpn(String uuid) throws Exception;

    /**
     * Disable remote access VPN for IP address.
     *
     * @param uuid of the IP address.
     * @return IP address.
     * @throws Exception if error occurs.
     */
    IpAddress disableRemoteAccessVpn(String uuid) throws Exception;

    /**
     * Get the VPN pre-shared key.
     *
     * @param id for ip address.
     * @return ip address.
     * @throws Exception if error occurs.
     */
    IpAddress findByVpnKey(Long id) throws Exception;

    /**
     * Delete rule from ipAddress.
     *
     * @param ipaddress object
     * @return ipaddress
     * @throws Exception if error occurs.
     */
    IpAddress ruleDelete(IpAddress ipaddress) throws Exception;
}
