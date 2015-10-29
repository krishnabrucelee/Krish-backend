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

	/** Get the OS type based on the uuid */
    @Query(value = "select ost from OsType ost where ost.uuid = :uuid")
    OsType findByUUID(@Param("uuid") String uuid);

    /** Get the OS type based on the OS category name */
    @Query(value = "select ost from OsType ost where ost.osCategoryUuid IN (select osc.uuid from OsCategory osc where osc.name = :categoryName)")
    List<OsType> findByCategoryName(@Param("categoryName") String categoryName);
}
