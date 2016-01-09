package ck.panda.domain.repository.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.FirewallRules;

/**
 * Jpa Repository for FirewallRules entity.
 *
 */
@Service
public interface EgressRuleRepository extends PagingAndSortingRepository<FirewallRules, Long> {

    /**
     * Find egress rules by uuid.
     *
     * @param uuid uuid of egress.
     * @return egress object.
     */
    @Query(value = "select egress from FirewallRules egress where egress.uuid = :uuid")
    FirewallRules findByUUID(@Param("uuid") String uuid);

    /**
     * Find all the active or inactive snapshots with pagination.
     *
     * @param pageable to get the list with pagination.
     * @param isActive get the snapshot list based on active/inactive status.
     * @return list of snapshots.
     */
    @Query(value = "select egress from FirewallRules egress where egress.isActive =:isActive")
    Page<FirewallRules> findAllByIsActive(Pageable pageable, @Param("isActive") Boolean isActive);

    /**
     * Find all by network and status active or inactive with pagination.
     *
     * @param pageable to get the list with pagination.
     * @param networkid network's id.
     * @param trafficType traffic type.
     * @param isActive get the snapshot list based on active/inactive status.
     * @return list of firewall rule.
     */
    @Query(value = "select egress from FirewallRules egress where egress.isActive =:isActive and egress.networkId =:networkid and egress.trafficType =:trafficType ")
    Page<FirewallRules> findAllByTraffictypeAndNetworkAndIsActive(Pageable pageable, @Param("trafficType") FirewallRules.TrafficType trafficType, @Param("networkid") Long networkid, @Param("isActive") Boolean isActive);

}
