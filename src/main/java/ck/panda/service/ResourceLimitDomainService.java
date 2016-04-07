/**
 *
 */
package ck.panda.service;

import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Service;
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
     * To get list of Volume from cloud stack server.
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
     * @param domainId domain id
     * @param isActive true/false
     * @param resourceType resource type.
     * @return domain resource count.
     * @throws Exception error
     */
    ResourceLimitDomain findByDomainAndResourceType(Long domainId, ResourceLimitDomain.ResourceType resourceType,
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
     * Get Resource count of domain.
     *
     * @param domainId domain id
     * @param resource resource type count
     * @param isActive true/false
     * @return resource count
     */
    ResourceLimitDomain findByDomainAndResourceCount(Long domainId, ResourceType resource, Boolean isActive);

    /**
     * List current domain for resource count.
     *
     * @return Resource limit domain
     * @throws Exception error
     */
    List<ResourceLimitDomain> findCurrentLoginDomain() throws Exception;

    /**
     * Update resource limit for domain quota.
     *
     * @throws Exception unhandled error
     */
    void asyncResourceDomain(Long domainId) throws Exception;

    /**
     * Get resource limits of domain.
     *
     * @param departmentId department id
     * @return max values of resources
     */
    HashMap<String, String> getResourceLimitsOfDomain(Long departmentId);

    /**
     * @param projectId
     * @return
     */
    HashMap<String, String> getResourceLimitsOfProject(Long projectId);

    /**
     * Find all the domain based quota list.
     *
     * @param domainId domain id of the volume
     * @return list of quota's with pagination.
     * @throws Exception error occurs
     */
    List<ResourceLimitDomain> findAllByDomainId(Long domainId) throws Exception;

    /**
     * List all department max resource limits by domain.
     *
     * @param id domain id
     * @return resource service
     * @throws Exception error
     */
    HashMap<String, Long> getSumOfDomainMin(Long id) throws Exception;


}
