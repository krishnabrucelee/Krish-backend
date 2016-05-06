package ck.panda.service;

import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.Project;
import ck.panda.rabbitmq.util.ResponseEvent;
import ck.panda.util.CloudStackServer;
import ck.panda.util.error.exception.ApplicationException;

/**
 * Synchronization of zone,domain, region , template with cloudStack. *
 */
@Service
public interface SyncService {

    /**
     * Sync initialize method used to set CS server api,secret Key.
     *
     * @param server inject cloudstack server.
     * @throws Exception handles unhandled errors.
     */
    void init(CloudStackServer server) throws Exception;

    /**
     * Sync method consists of method to be called.
     *
     * @throws Exception handles unhandled errors.
     */
    void sync() throws Exception;

    /**
     * Sync with CloudStack server Domain.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors
     */
    void syncDomain() throws ApplicationException, Exception;

    /**
     * Sync with Cloud Server Zone.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    void syncZone() throws ApplicationException, Exception;

    /**
     * Sync with Cloud Server Region.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    void syncRegion() throws ApplicationException, Exception;

    /**
     * Sync with Cloud Server Hypervisor.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    void syncHypervisor() throws ApplicationException, Exception;

    /**
     * Sync with Cloud Server OSCategory.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    void syncOsCategory() throws ApplicationException, Exception;

    /**
     * Sync with CloudStack server osType.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors
     */
    void syncOsTypes() throws ApplicationException, Exception;

    /**
     * Sync with CloudStack server Storage Offering.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors
     */
    void syncStorageOffering() throws ApplicationException, Exception;

    /**
     * Sync with Cloud Server Account.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    void syncUser() throws ApplicationException, Exception;

    /**
     * Sync with CloudStack server Network offering.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors
     */
    void syncNetworkOffering() throws ApplicationException, Exception;

    /**
     * Sync with CloudStack server Network.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors
     */
    void syncNetwork() throws ApplicationException, Exception;

    /**
     * Sync with CloudStack server Compute Offering.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors
     */
    void syncComputeOffering() throws ApplicationException, Exception;

    /**
     * Sync with CloudStack server Templates.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors
     */
    void syncTemplates() throws ApplicationException, Exception;

    /**
     * Sync with CloudStack server Templates.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors
     */
    void syncDepartment() throws ApplicationException, Exception;

    /**
     * Sync with Cloud Server volume.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    void syncVolume() throws ApplicationException, Exception;

    /**
     * Sync with CloudStack server Instance list.
     *
     * @throws Exception cloudstack unhandled errors
     */
    void syncInstances() throws Exception;

    /**
     * Sync with CloudStack server Instance snapshot list.
     *
     * @throws Exception cloudstack unhandled errors
     */
    void syncVmSnapshots() throws Exception;

    /**
     * Sync with CloudStack server Instance snapshot list.
     *
     * @param object response json object.
     * @throws Exception cloudstack unhandled errors
     */
    void syncResourceStatus(String object) throws Exception;

    /**
     * Sync with CloudStack server ResourceLimit list.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors
     */
    void syncResourceLimit() throws ApplicationException, Exception;

    /**
     * Sync with CloudStack server ResourceLimit max and used limit.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors
     */
    void syncResourceUpdate() throws ApplicationException, Exception;

    /**
     * Sync with Cloud Server Account.
     *
     * @param domain domain
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    void syncResourceLimitDomain(Domain domain) throws ApplicationException, Exception;

    /**
     * Sync with Cloud Server Account.
     *
     * @param project for resource limit.
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    void syncResourceLimitProject(Project project) throws ApplicationException, Exception;

    /**
     * Sync with Cloud Server Account.
     *
     * @param project for resource limit.
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    void syncResourceLimitForProject(Project project) throws ApplicationException, Exception;

    /**
     * Sync with Cloud Server Account.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    void syncProject() throws ApplicationException, Exception;

    /**
     * Sync with Cloud Server Account.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    void syncSSHKey() throws ApplicationException, Exception;

    /**
     *
     * Sync with Cloud Server Account.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception unhandled errors.
     */
    void syncNic() throws ApplicationException, Exception;

   /**
    *
    * Sync with Cloud Server fire wall rules.
    *
    * @throws ApplicationException unhandled application errors.
    * @throws Exception unhandled errors.
    */
    void syncEgressFirewallRules() throws ApplicationException, Exception;

    /**
    *
    * Sync with Cloud Server ingress fire wall rules.
    *
    * @throws ApplicationException unhandled application errors.
    * @throws Exception unhandled errors.
    */
    void syncIngressFirewallRules() throws ApplicationException, Exception;

    /**
    *
    * Sync with Cloud Server IP Address list.
    *
    * @throws ApplicationException unhandled application errors.
    * @throws Exception unhandled errors.
    */
    void syncIpAddress() throws ApplicationException, Exception;

    /**
    *
    * Sync with Cloud Server Port Forwarding list.
    *
    * @throws ApplicationException unhandled application errors.
    * @throws Exception unhandled errors.
    */
    void syncPortForwarding() throws ApplicationException, Exception;

    /**
    *
    * Sync with Cloud Server Load Balancer list.
    *
    * @throws ApplicationException unhandled application errors.
    * @throws Exception unhandled errors.
    */
    void syncLoadBalancer() throws ApplicationException, Exception;

    /**
    *
    * Updated user role for root and domain admin.
    *
    * @throws ApplicationException unhandled application errors.
    * @throws Exception unhandled errors.
    */
    void syncUpdateUserRole() throws ApplicationException, Exception;

    /**
     * Sync with CloudStack server ResourceLimit Action event.
     *
     * @param eventObject for resource limit.
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors
     */
    void syncResourceLimitActionEvent(ResponseEvent eventObject) throws ApplicationException, Exception;

    /**
     * Sync with CloudStack server snapshot policy.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    void syncSnapshotPolicy() throws ApplicationException, Exception;

    /**
     *
     * Sync with Cloud Server VPN user list.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception unhandled errors.
     */
    void syncVpnUser() throws ApplicationException, Exception;

    /**
     * Sync with CloudStack server ResourceLimit Action event Project.
     *
     * @param eventResponse for resource limit.
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors
     */
    void syncResourceLimitActionEventProject(ResponseEvent eventResponse) throws ApplicationException, Exception;

    /**
     * Sync event list.
     *
     * @throws Exception if error occurs.
     */
    void syncEventList() throws Exception;

    /**
     * Sync snapshot.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception if error occurs.
     */
    void syncSnapshot() throws ApplicationException, Exception;

    /**
     * Sync load balancer sticky policy.
     *
     * @throws Exception if error occurs.
     */
    void syncLoadBalancerStickyPolicy() throws Exception;

    /**
     * Sync with CloudStack server affinity group type.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors
     */
    void syncAffinityGroupType() throws ApplicationException, Exception;

    /**
     * Sync with CloudStack server affinity group.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors
     */
    void syncAffinityGroup() throws ApplicationException, Exception;

    /**
     * Sync with Cloud Server VPC offering.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    void syncVpcOffering() throws ApplicationException, Exception;

    /**
     * Sync with Cloud Server VPC ACL.
     *
     * @throws ApplicationException unhandled application errors.
     * @throws Exception cloudstack unhandled errors.
     */
    void syncVpcAcl() throws ApplicationException, Exception;
}
