package ck.panda.domain.repository.jpa;

import ck.panda.domain.entity.Domain;
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
     * @param domainName for login check
     * @return domain object
     */
    @Query(value = "select domain from Domain domain where domain.name = :domainName")
    Domain findByName(@Param("domainName") String domainName);
}
