/**
 *
 */
package ck.panda.domain.repository.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
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
}
