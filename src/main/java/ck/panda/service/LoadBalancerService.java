package ck.panda.service;

import java.util.List;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.IpAddress;
import ck.panda.domain.entity.LoadBalancerRule;
import ck.panda.util.domain.CRUDService;

/**
 * Load Balancer Service.
 *
 */
@Service
public interface LoadBalancerService extends CRUDService<LoadBalancerRule> {

    /**
     * Find all LoadBalancer by ipAddress.
     *
     * @param id ipAddress id .
     * @param isActive ipAddress status Active/Inactive
     * @return list of ipAddress in LoadBalancer.
     * @throws Exception if error occurs.
     */
    List<LoadBalancerRule> findAllByIpAddressAndIsActive(Long id, Boolean isActive) throws Exception;

    /**
     * To get list of LoadBalancer from cloudstack server.
     *
     * @return LoadBalancer list from server
     * @throws Exception unhandled errors.
     */
    List<LoadBalancerRule> findAllFromCSServer() throws Exception;

    /**
     * IP address list for tier.
     *
     * @param networkId for vpc's tier.
     * @return ip address list.
     * @throws Exception if error occurs.
     */
    List<IpAddress> vpcLBList(Long networkId) throws Exception;

    /**
     * Soft delete for LoadBalancer.
     *
     * @param loadBalancer object
     * @return LoadBalancer
     * @throws Exception unhandled errors.
     */
    LoadBalancerRule softDelete(LoadBalancerRule loadBalancer) throws Exception;

    /**
     * To get LoadBalancer by uuid.
     *
     * @param uuid uuid of LoadBalancer.
     * @return LoadBalancer.
     * @throws Exception unhandled errors.
     */
    LoadBalancerRule findByUUID(String uuid);

    /**
     * List loadbalancer rule by isActive status.
     *
     * @param isActive status of the load balancer.
     * @return load balancer.
     */
    List<LoadBalancerRule> findByIsActive(Boolean isActive);

    /**
     * List load balancer by ip address.
     *
     * @param ipAddressId of the load balancer.
     * @param isActive of the load balancer.
     * @return load balancer.
     */
    List<LoadBalancerRule> findByIpaddress(Long ipAddressId, Boolean isActive);

    /**
     * List load balancer by network.
     *
     * @param networkId of the load balancer.
     * @param isActive of the load balancer.
     * @return load balancer.
     */
    List<LoadBalancerRule> findByNetwork(Long networkId, Boolean isActive);

    /**
     * Save load balancer with user id.
     *
     * @param loadBalancer for creating lb rule.
     * @param userId of the user from token.
     * @return loadBalancerRule.
     * @throws Exception if error occurs.
     */
    LoadBalancerRule save(LoadBalancerRule loadBalancer, Long userId) throws Exception;

    /**
     * Remove load balancer from IpAddress.
     *
     * @param loadbalancer object to be removed.
     * @return loadbalancer rule.
     * @throws Exception if error occurs.
     */
    LoadBalancerRule removeLoadBalancerRule(LoadBalancerRule loadbalancer) throws Exception;

    /**
     * List loadbalancer rule by LB policy id.
     *
     * @param lbPolicyId status of the load balancer.
     * @return load balancer.
     */
    LoadBalancerRule findByLbId(Long lbPolicyId);
}
