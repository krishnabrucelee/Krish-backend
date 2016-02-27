package ck.panda.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.LbStickinessPolicy;
import ck.panda.util.domain.CRUDService;

/**
 * Service interface for LbStickinessPolicy entity.
 *
 */
@Service
public interface LbStickinessPolicyService extends CRUDService<LbStickinessPolicy> {

    /**
     * To get list of stickiness policy from cloudstack server.
     *
     * @return pod list from server
     * @throws Exception unhandled errors.
     */
    List<LbStickinessPolicy> findAllFromCSServer() throws Exception;

    /**
     * To get pod from cloudstack server.
     *
     * @param uuid of stickiness policy .
     * @return stickiness policy  from server
     * @throws Exception unhandled errors.
     */
    LbStickinessPolicy findByUUID(String uuid) throws Exception;

    /**
     * Soft delete for stickiness policy .
     *
     * @param stickiness policy  object
     * @return stickiness policy.
     * @throws Exception unhandled errors.
     */
    LbStickinessPolicy softDelete(LbStickinessPolicy lbStickinessPolicy) throws Exception;

    /**
     * Create Stickiness policy for Load balancer.
     *
     * @param lbStickinessPolicy object to create policy.
     * @param loadbalancer passing uuid of the loadbalancer to apply policy.
     * @return lbStickiness policy
     * @throws Exception if error occurs.
     */
    LbStickinessPolicy save(LbStickinessPolicy lbStickinessPolicy, String loadbalancer) throws Exception;
}
