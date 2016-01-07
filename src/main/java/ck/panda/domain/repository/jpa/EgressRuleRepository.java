package ck.panda.domain.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.FirewallRules;

/**
 * Jpa Repository for FirewallRules entity.
 *
 */
@Service
public interface EgressRuleRepository extends PagingAndSortingRepository<FirewallRules, Long> {

}
