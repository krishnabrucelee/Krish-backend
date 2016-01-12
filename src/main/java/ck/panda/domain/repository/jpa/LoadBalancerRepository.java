package ck.panda.domain.repository.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.LoadBalancerRule;

/**
 * Load Balancer Repository.
 *
 */
public interface LoadBalancerRepository extends PagingAndSortingRepository<LoadBalancerRule, Long> {

    /**
     * Get the load balancer based on the uuid.
     *
     * @param uuid of the load balancer
     * @param isActive get the load balancer list based on active/inactive status.
     * @return load balancer
     */
    @Query(value = "select lb from LoadBalancerRule lb where lb.uuid = :uuid AND lb.isActive =:isActive")
    LoadBalancerRule findByUUID(@Param("uuid") String uuid, @Param("isActive") Boolean isActive);

    /**
     * Find all the active or inactive load balancer with pagination.
     *
     * @param pageable to get the list with pagination.
     * @param isActive get the load balancer list based on active/inactive status.
     * @return list of load balancer.
     */
    @Query(value = "select lb from LoadBalancerRule lb where lb.isActive =:isActive")
    Page<LoadBalancerRule> findAllByIsActive(Pageable pageable, @Param("isActive") Boolean isActive);

    /**
     * Find all by network and status active or inactive with pagination.
     *
     * @param pageable to get the list with pagination.
     * @param ipAddressId IP Address id.
     * @param isActive get the load balancer list based on active/inactive status.
     * @return LoadBalancer.
     */
    @Query(value = "select lb from LoadBalancerRule lb where lb.ipAddressId =:ipAddressId AND lb.isActive =:isActive")
    Page<LoadBalancerRule> findAllByIpaddressAndIsActive(Pageable pageable, @Param("ipAddressId") Long ipAddressId, @Param("isActive") Boolean isActive);

}
