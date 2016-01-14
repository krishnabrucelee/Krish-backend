package ck.panda.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.LoadBalancerRule;
import ck.panda.util.domain.CRUDService;

/**
 * Load Balancer Service.
 *
 */
@Service
public interface LoadBalancerService extends CRUDService<LoadBalancerRule> {

    /**
     * To get list of LoadBalancer from cloudstack server.
     *
     * @return LoadBalancer list from server
     * @throws Exception unhandled errors.
     */
    List<LoadBalancerRule> findAllFromCSServer() throws Exception;

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
     * List load balancer by ip address.
     *
     * @param ipAddressId of the load balancer.
     * @param isActive of the load balancer.
     * @return load balancer.
     */
    List<LoadBalancerRule> findByIpaddress(Long ipAddressId, Boolean isActive);

}
