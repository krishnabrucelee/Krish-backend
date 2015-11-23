package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.OsType;

/**
 * JPA repository for OS type entity.
 */
public interface OsTypeRepository extends PagingAndSortingRepository<OsType, Long> {

    /**
     * Get the OS type based on the uuid.
     *
     * @param uuid of the OS type
     * @return OS type
     */
    @Query(value = "select ost from OsType ost where ost.uuid = :uuid")
    OsType findByUUID(@Param("uuid") String uuid);

    /**
     * Get the OS type based on the OS category name.
     *
     * @param categoryName of the OS type
     * @return OS type
     */
    @Query(value = "select ost from OsType ost where ost.osCategoryId IN (select osc.id from OsCategory osc where osc.name = :categoryName) order by ost.description ASC")
    List<OsType> findByCategoryName(@Param("categoryName") String categoryName);
}
