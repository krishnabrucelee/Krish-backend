package ck.panda.domain.repository.jpa;

import ck.panda.domain.entity.Domain;
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
    @Query(value = "select domain from Domain domain where domain.uuid = :uuid")
    Domain findByUUID(@Param("uuid") String uuid);

    /**
     * Find domain name to avoid duplications.
     *
     * @param domainName for login check
     * @return domain object
     */
    @Query(value = "select domain from Domain domain where domain.name = :domainName")
    Domain findByName(@Param("domainName") String domainName);

    /**
     * Find all the active or inactive domains with pagination.
     *
     * @param pageable to get the list with pagination.
     * @param isActive get the Domain list based on active/inactive status.
     * @return list of Domains.
     */
    @Query(value = "select domain from Domain domain where domain.isActive =:isActive")
    Page<Domain> findAllByIsActive(Pageable pageable, @Param("isActive") Boolean isActive);
}
