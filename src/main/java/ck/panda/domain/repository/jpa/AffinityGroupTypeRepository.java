package ck.panda.domain.repository.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.AffinityGroupType;

/**
 * JPA repository for affinity group type entity.
 */
public interface AffinityGroupTypeRepository extends PagingAndSortingRepository<AffinityGroupType, Long> {

    /**
     * Get the affinity group type based on the name.
     *
     * @param type of the affinity group type
     * @return affinity group
     */
    @Query(value = "select affinityGroupType from AffinityGroupType affinityGroupType where affinityGroupType.type = :type")
    AffinityGroupType findByType(@Param("type") String type);
}
