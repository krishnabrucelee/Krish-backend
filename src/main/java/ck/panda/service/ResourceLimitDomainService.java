/**
 *
 */
package ck.panda.service;

import java.util.List;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.ResourceLimitDomain;
import ck.panda.domain.entity.ResourceLimitDomain.ResourceType;
import ck.panda.util.domain.CRUDService;

/**
 * Resource Limit Service.
 *
 */
@Service
public interface ResourceLimitDomainService extends CRUDService<ResourceLimitDomain> {

    /**
     * To get list of Volume from cloudstack server.
     *
     * @param domainId domain id.
     * @return ResourceLimit list from server
     * @throws Exception unhandled errors.
     */
    List<ResourceLimitDomain> findAllFromCSServerDomain(String domainId) throws Exception;

    /**
     * Create the quota limit for Domain.
     *
     * @param resourceLimits resource limits
     * @return created resource limits
     * @throws Exception error.
     */
    List<ResourceLimitDomain> createResourceLimits(List<ResourceLimitDomain> resourceLimits) throws Exception;

    /**
     * Find all resource limits by domain id.
     *
     * @param id domain id.
     * @param isActive true/false
     * @return domain
     * @throws Exception error
     */
    List<ResourceLimitDomain> findAllByDomainIdAndIsActive(Long id, Boolean isActive) throws Exception;

    /**
     * Find all resource type by domain id.
     *
     * @param id domain
     * @param isActive true/false
     * @param resourceType resource type.
     * @return domain resource count.
     * @throws Exception error
     */
    ResourceLimitDomain findByDomainAndResourceType(Long id, ResourceLimitDomain.ResourceType resourceType,
            Boolean isActive) throws Exception;

    /**
     * Delete Resource Limit By Domain.
     *
     * @param domainId domain id.
     */
    void deleteResourceLimitByDomain(Long domainId);

    /**
     * Find all resource type from doamin id.
     *
     * @param domainId domain id.
     * @return resource type.
     */
    ResourceLimitDomain findAllByResourceType(Long domainId);

    /**
     * Get ResourceCount.
     * @param domainId domain id
     * @param resource resource type count
     * @param isActive true/false
     * @return resource count
     */
    ResourceLimitDomain findByDomainAndResourceCount(Long domainId, List<ResourceType> resource, Boolean isActive);

}
