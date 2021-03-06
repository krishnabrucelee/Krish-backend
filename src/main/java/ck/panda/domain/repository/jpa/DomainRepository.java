package ck.panda.domain.repository.jpa;

import ck.panda.domain.entity.Domain;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

/**
 * Jpa Repository for Domain entity.
 */
@Service
public interface DomainRepository extends PagingAndSortingRepository<Domain, Long> {
    /**
     * Find domain by uuid.
     *
     * @param uuid uuid of domain.
     * @return domain object.
     */
    @Query(value = "SELECT domain FROM Domain domain WHERE domain.uuid = :uuid")
    Domain findByUUID(@Param("uuid") String uuid);

    /**
     * Find domain by uuid.
     *
     * @param uuid uuid of domain.
     * @param isActive get the Domain list based on active/inactive status.
     * @return domain object.
     */
    @Query(value = "SELECT domain FROM Domain domain WHERE domain.uuid = :uuid AND domain.isActive = :isActive")
    Domain findByUUIDAndIsActive(@Param("uuid") String uuid, @Param("isActive") Boolean isActive);

    /**
     * Find domain name to avoid duplications.
     *
     * @param domainName for login check
     * @param isActive get the Domain list based on active/inactive status.
     * @return domain object
     */
    @Query(value = "SELECT domain FROM Domain domain WHERE domain.companyNameAbbreviation = :domainName AND domain.isActive =:isActive")
    Domain findByName(@Param("domainName") String domainName, @Param("isActive") Boolean isActive);

    /**
     * Find all the active or inactive domains with pagination.
     *
     * @param pageable to get the list with pagination.
     * @param isActive get the Domain list based on active/inactive status.
     * @return list of Domains.
     */
    @Query(value = "SELECT domain FROM Domain domain WHERE domain.isActive = :isActive")
    Page<Domain> findAllByIsActive(Pageable pageable, @Param("isActive") Boolean isActive);

    /**
     * Find all the active or inactive domains.
     *
     * @param isActive get the Domain list based on active/inactive status.
     * @return list of Domains.
     */
    @Query(value = "SELECT domain FROM Domain domain WHERE domain.isActive = :isActive ORDER BY domain.name ASC")
    List<Domain> findAllByDomainAndIsActive(@Param("isActive") Boolean isActive);

    /**
     * Find all the active or inactive domains using quick search.
     *
     * @param pageable to get the list with pagination.
     * @param search search text.
     * @param isActive get the Domain list based on active/inactive status.
     * @return list of Domains.
     */
    @Query(value = "SELECT domain FROM Domain domain WHERE domain.isActive = :isActive AND (domain.name LIKE %:search% OR domain.companyNameAbbreviation LIKE %:search% "
            + "OR domain.portalUserName LIKE %:search% OR domain.cityHeadquarter LIKE %:search% OR domain.email LIKE %:search% OR domain.phone LIKE %:search%)")
    Page<Domain> findDomainBySearchText(Pageable pageable, @Param("search") String search, @Param("isActive") Boolean isActive);

}
