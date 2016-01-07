package ck.panda.service;

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


}
