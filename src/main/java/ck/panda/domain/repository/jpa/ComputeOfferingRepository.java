package ck.panda.domain.repository.jpa;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.ComputeOffering;

/**
 * Compute Offering Repository interface that extends PagingAndSortingRepository along with sorting AND pagination.
 */
public interface ComputeOfferingRepository extends PagingAndSortingRepository<ComputeOffering, Long> {
    /**
     * Get the compute offer based on the uuid.
     *
     * @param uuid of the compute offer.
     * @return compute offer
     */
    @Query(value = "SELECT compute FROM ComputeOffering compute WHERE compute.uuid = :uuid")
    ComputeOffering findByUUID(@Param("uuid") String uuid);

    /**
     * Find all the active or inactive compute offering with pagination.
     *
     * @param pageable to get the list with pagination.
     * @param isActive get the compute offering list based on active/inactive status.
     * @return list of compute offerings.
     */
    @Query(value = "SELECT compute FROM ComputeOffering compute WHERE compute.isActive = :isActive")
    Page<ComputeOffering> findAllByIsActive(Pageable pageable, @Param("isActive") Boolean isActive);

    /**
     * Find by name of the offering.
     *
     * @param name of compute offering.
     * @param isActive get the compute offering list based on active/inactive status.
     * @return compute offering.
     */
    @Query(value = "SELECT compute FROM ComputeOffering compute WHERE compute.name = :name AND compute.isActive = :isActive")
    ComputeOffering findNameAndIsActive(@Param("name") String name, @Param("isActive") Boolean isActive);

    /**
     * Find by is Active in Compute Offering.
     *
     * @param isActive offer.
     * @return compute offering.
     */
    @Query(value = "SELECT compute FROM ComputeOffering compute WHERE compute.isActive = :isActive")
    List<ComputeOffering> findByIsActive(@Param("isActive") Boolean isActive);

    /**
     * List compute offering by domain.
     *
     * @param domainId of the domain.
     * @param isActive status of the domain.
     * @return compute offering.
     */
    @Query(value = "SELECT compute FROM ComputeOffering compute WHERE compute.isActive = :isActive AND ( compute.domainId = :domainId OR compute.domainId IS NULL )")
    List<ComputeOffering> findByDomainAndIsActive(@Param("domainId") Long domainId,@Param("isActive") Boolean isActive);

    /**
     * Find all the domain based active or inactive compute offering with pagination.
     *
     * @param domainId of the domain.
     * @param isActive get the compute offering list based on active/inactive status.
     * @param pageable to get the list with pagination.
     * @return list of compute offerings.
     */
    @Query(value = "SELECT compute FROM ComputeOffering compute WHERE compute.domainId = :domainId AND compute.isActive = :isActive")
    Page<ComputeOffering> findAllByDomainIdAndIsActive(@Param("domainId") Long domainId, @Param("isActive") Boolean isActive, Pageable pageable);

    /**
     * Find all the domain based active or inactive compute offering with pagination.
     *
     * @param domainId of the domain.
     * @param search text for type based search.
     * @param isActive get the compute offering list based on active/inactive status.
     * @param pageable to get the list with pagination.
     * @return list of compute offerings.
     */
    @Query(value = "SELECT compute FROM ComputeOffering compute WHERE(compute.domainId = :domainId OR 0L = :domainId) AND compute.isActive IS :isActive AND (compute.name LIKE %:search% OR compute.numberOfCores LIKE %:search% "
            + "OR compute.clockSpeed LIKE %:search% OR compute.memory LIKE %:search% OR compute.storageType LIKE %:search%)")
    Page<ComputeOffering> findAllByDomainIdAndIsActiveAndSearchText(@Param("domainId") Long domainId, @Param("isActive") Boolean isActive, Pageable pageable,@Param("search") String searchText);
}
