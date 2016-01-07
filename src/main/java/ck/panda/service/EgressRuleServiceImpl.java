package ck.panda.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.FirewallRules;
import ck.panda.domain.repository.jpa.EgressRuleRepository;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Egress Firewall Rule service Implemetation.
 *
 */
@Service
public class EgressRuleServiceImpl implements EgressRuleService {

  /** Logger attribute. */
  private static final Logger LOGGER = LoggerFactory.getLogger(DomainServiceImpl.class);

  /** Egress Rule repository reference. */
  @Autowired
  private EgressRuleRepository egressRepo;

  @Override
  public FirewallRules save(FirewallRules egressFirewallRule) throws Exception {
      return egressRepo.save(egressFirewallRule);
  }

  @Override
  public FirewallRules update(FirewallRules egressFirewallRule) throws Exception {
    return egressRepo.save(egressFirewallRule);
  }

  @Override
  public void delete(FirewallRules egressFirewallRule) throws Exception {
      egressRepo.delete(egressFirewallRule);
  }

  @Override
  public void delete(Long id) throws Exception {
      egressRepo.delete(id);
  }

  @Override
  public FirewallRules find(Long id) throws Exception {
      FirewallRules egressFirewallRule = egressRepo.findOne(id);
      return egressFirewallRule;
  }

  @Override
  public Page<FirewallRules> findAll(PagingAndSorting pagingAndSorting) throws Exception {
      return egressRepo.findAll(pagingAndSorting.toPageRequest());
  }

  @Override
  public List<FirewallRules> findAll() throws Exception {
      return (List<FirewallRules>) egressRepo.findAll();
  }

}

