package ck.panda.domain.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.MiscellaneousCost;

/**
 * MiscellaneousCost interface that extends PagingAndSortingRepository along with sorting and pagination.
 */
public interface MiscellaneousCostRepository extends PagingAndSortingRepository<MiscellaneousCost, Long> {

    @Query(value = "SELECT cost FROM MiscellaneousCost cost WHERE cost.isActive = :isActive")
    MiscellaneousCost findByIsActive(@Param("isActive") Boolean isActive);

    @Query(value = "SELECT cost FROM MiscellaneousCost cost WHERE cost.isActive = :isActive")
    List<MiscellaneousCost> findAllByIsActive(@Param("isActive") Boolean isActive);
}


