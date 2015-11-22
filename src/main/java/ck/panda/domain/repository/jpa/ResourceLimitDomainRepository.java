/**
 *
 */
package ck.panda.domain.repository.jpa;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import ck.panda.domain.entity.ResourceLimitDomain;

/**
 * Jpa Repository for ResourceLimit entity.
 */
public interface ResourceLimitDomainRepository extends PagingAndSortingRepository<ResourceLimitDomain, Long> {

    /**
     * method to find list of entities having active status.
     *
     * @param pageable volume list page
     * @return lists Active state ResourceLimit
     */
    @Query(value = "select resource from ResourceLimitDomain resource where resource.isActive IS TRUE")
    Page<ResourceLimitDomain> findAllByActive(Pageable pageable);

    /**
     * Find all the active resource limits based on the domain id.
     *
     * @param domainId domain id.
     * @param isActive true/false
     * @return domain
     */
    @Query(value = "select resource from ResourceLimitDomain resource where resource.isActive =:isActive AND resource.domainId =:domainId")
    List<ResourceLimitDomain> findAllByDomainIdAndIsActive(@Param("domainId") Long domainId, @Param("isActive") Boolean isActive);

    /**
     * Find all the active resource limits based on the domain id.
     *
     * @param id domain id.
     * @param isActive true/false
     * @param resourceType resource type
     * @return domain resource type.
     */
    @Query(value = "select resource from ResourceLimitDomain resource where resource.isActive =:isActive AND resource.domainId =:domainId AND resource.resourceType =:resourceType")
    ResourceLimitDomain findByDomainAndResourceType(@Param("domainId") Long id, @Param("resourceType") ResourceLimitDomain.ResourceType resourceType, @Param("isActive") Boolean isActive);

    /**
     * Delete all the resource limits based on the domain.
     *
     * @param domainId
     * @return
     */
    @Query(value = "delete from ResourceLimitDomain resource where resource.isActive =:isActive AND resource.domainId =:domainId")
    ResourceLimitDomain deleteByDomainAndIsActive(@Param("domainId") Long domainId, @Param("isActive") Boolean isActive);
}
