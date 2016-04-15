package ck.panda.domain.repository.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.GeneralConfiguration;

/** Repository for general configuration. */
public interface GeneralConfigurationRepository extends PagingAndSortingRepository<GeneralConfiguration, Long> {

     /**
     * Find by is Active general configuration.
     *
     * @param isActive status of general configuration.
     * @return general configuration
     */
    @Query(value = "SELECT configuration FROM GeneralConfiguration configuration WHERE configuration.isActive = :isActive")
    GeneralConfiguration findByIsActive(@Param("isActive") Boolean isActive);
}
