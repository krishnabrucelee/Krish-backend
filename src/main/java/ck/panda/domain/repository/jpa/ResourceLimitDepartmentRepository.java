package ck.panda.domain.repository.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import ck.panda.domain.entity.ResourceLimitDepartment;

/**
 * Jpa Repository for ResourceLimit department entity.
 */
public interface ResourceLimitDepartmentRepository extends PagingAndSortingRepository<ResourceLimitDepartment, Long>  {

    /**
     * method to find list of entities having active status.
     *
     * @param pageable volume list page
     * @return lists Active state ResourceLimit
     */
    @Query(value = "select resource from ResourceLimitDepartment resource where resource.isActive IS TRUE")
    Page<ResourceLimitDepartment> findAllByActive(Pageable pageable);
}
