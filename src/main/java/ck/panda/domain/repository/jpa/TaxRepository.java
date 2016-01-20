package ck.panda.domain.repository.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.Tax;

/**
 * JPA repository for Tax entity.
 */
public interface TaxRepository extends PagingAndSortingRepository<Tax, Long> {
    /**
     * Find all the active or inactive tax with pagination.
     *
     * @param pageable to get the list with pagination.
     * @param isActive get the tax list based on active/inactive status.
     * @return list of tax.
     */
    @Query(value = "select t from Tax t where t.isActive =:isActive")
    Page<Tax> findAllByIsActive(Pageable pageable, @Param("isActive") Boolean isActive);

    /**
     * Find tax by name and is active.
     *
     * @param name tax name
     * @param isActive get the tax based on active/inactive status.
     * @return tax
     */
    Tax findByNameAndIsActive(@Param("name") String name, @Param("isActive") Boolean isActive);
}
