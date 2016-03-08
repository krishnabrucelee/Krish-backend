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
    @Query(value = "SELECT domain FROM Domain domain WHERE domain.isActive = :isActive")
    List<Domain> findAllByDomainAndIsActive(@Param("isActive") Boolean isActive);
}
