package ck.panda.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.FirewallRules;
import ck.panda.util.domain.CRUDService;

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
     * Soft delete for egressFirewallRule.
     *
     * @param egressFirewallRule object
     * @return egressFirewallRule
     * @throws Exception unhandled errors.
     */
    FirewallRules softDelete(FirewallRules egressFirewallRule) throws Exception;

}
