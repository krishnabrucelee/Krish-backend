package ck.panda.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.FirewallRules;
import ck.panda.domain.entity.FirewallRules.TrafficType;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Service class for Firewall rules.
 * This service provides basic CRUD and essential api's for host actions.
 *
 */
@Service
public interface EgressRuleService extends CRUDService<FirewallRules> {

    /**
     * Find all firewall rules by ipAddress.
     *
     * @param id ipAddress id .
     * @param isActive ipAddress status Active/Inactive
     * @return list of ipAddress in firewall rules.
     * @throws Exception if error occurs.
     */
    List<FirewallRules> findAllByIpAddressAndIsActive(Long id, Boolean isActive) throws Exception;

    /**
     * To get list of egressFirewallRule from cloudstack server.
     *
     * @return egressFirewallRule list from server
     * @throws Exception unhandled errors.
     */
    List<FirewallRules> findAllFromCSServer() throws Exception;

    /**
     * Add new rule for ingress firewall.
     *
     * @param firewallRules object for add firewall
     * @return ingress firewall.
     * @throws Exception unhandled errors.
     */
    FirewallRules createFirewallIngressRule(FirewallRules firewallRules) throws Exception;

    /**
     * Remove new rule for ingress firewall.
     *
     * @param firewallRules object for add firewall
     * @return ingress firewall.
     * @throws Exception unhandled errors.
     */
    FirewallRules deleteFirewallIngressRule(FirewallRules firewallRules) throws Exception;

    /**
     * To get list of FirewallRule for Ingress from cloudstack server.
     *
     * @return ingressFirewallRule list from server
     * @throws Exception unhandled errors.
     */
    List<FirewallRules> findAllFromCSServerForIngress() throws Exception;

    /**
     * To get list of FirewallRule by Traffictype.
     *
     * @return trafficType firewall rules trafficType.
     * @throws Exception unhandled errors.
     */
    List<FirewallRules> findAllByTrafficType(TrafficType trafficType) throws Exception;

    /**
     * Firewall Rules by uuid.
     *
     * @param uuid uuid of firewall
     * @return FirewallRules
     * @throws Exception unhandled errors.
     */
    FirewallRules findByUUID(String uuid) throws Exception;

    /**
     * Soft delete for egressFirewallRule.
     *
     * @param egressFirewallRule object
     * @return egressFirewallRule
     * @throws Exception unhandled errors.
     */
    FirewallRules softDelete(FirewallRules egressFirewallRule) throws Exception;

    /**
     * Paging and Sorting for displaying more number of elements in list.
     *
     * @param pagingAndSorting sortable method.
     * @return sorted values.
     * @throws Exception unhandled errors.
     */
    Page<FirewallRules> findAllByActive(PagingAndSorting pagingAndSorting) throws Exception;

    /**
     * Paging and Sorting for displaying more number of elements in list.
     *
     * @param pagingAndSorting sortable method.
     * @param networkId network's id.
     * @param trafficType traffic type.
     * @return sorted values.
     * @throws Exception unhandled errors.
     */
    Page<FirewallRules> findAllByTraffictypeAndNetwork(PagingAndSorting pagingAndSorting, Long networkId, TrafficType trafficType) throws Exception;

    /**
     * Paging and Sorting for displaying more number of elements in list.
     *
     * @param pagingAndSorting sortable method.
     * @param ipadddressId network's id.
     * @param trafficType traffic type.
     * @return sorted values.
     * @throws Exception unhandled errors.
     */
    Page<FirewallRules> findAllByTraffictypeAndIpAddress(PagingAndSorting pagingAndSorting, Long ipadddressId, TrafficType trafficType) throws Exception;
}
