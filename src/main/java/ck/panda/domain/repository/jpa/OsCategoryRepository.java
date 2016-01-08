package ck.panda.domain.repository.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.OsCategory;

/**
 * JPA repository for OS category entity.
 */
public interface OsCategoryRepository extends PagingAndSortingRepository<OsCategory, Long> {

    /**
     * Find osCategory by uuid.
     *
     * @param uuid uuid of osCategory.
     * @return osCategory object.
     */
    @Query(value = "select oscategory from OsCategory oscategory where oscategory.uuid = :uuid")
    OsCategory findByUUID(@Param("uuid") String uuid);

}
