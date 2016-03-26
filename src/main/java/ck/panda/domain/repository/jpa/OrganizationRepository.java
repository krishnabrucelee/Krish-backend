package ck.panda.domain.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.Organization;

/** Repository for organization. */
public interface OrganizationRepository extends PagingAndSortingRepository<Organization, Long> {

     /**
     * Find by is Active organization.
     *
     * @param isActive status of organization.
     * @return organization
     */
    @Query(value = "SELECT organization FROM Organization organization WHERE organization.isActive = :isActive")
    List<Organization> findByIsActive(@Param("isActive") Boolean isActive);
}
