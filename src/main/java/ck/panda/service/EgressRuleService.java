package ck.panda.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.FirewallRules;
import ck.panda.domain.entity.Nic;
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
     * To get list of egressFirewallRule from cloudstack server.
     *
     * @return egressFirewallRule list from server
     * @throws Exception unhandled errors.
     */
    List<FirewallRules> findAllFromCSServer() throws Exception;

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
     * @return sorted values.
     * @throws Exception unhandled errors.
     */
    Page<FirewallRules> findAllByTraffictypeAndNetwork(PagingAndSorting pagingAndSorting,  Long networkId) throws Exception;
}
