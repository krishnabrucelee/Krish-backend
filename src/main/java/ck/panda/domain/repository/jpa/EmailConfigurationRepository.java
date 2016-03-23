package ck.panda.domain.repository.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.EmailConfiguration;

/**
 * Email configuration repository.
 */
public interface EmailConfigurationRepository extends PagingAndSortingRepository<EmailConfiguration, Long>{

    /**
     * Find the Email Configrations by IsActive.
     *
     * @param isActive get the email configuration list based on active/inactive status
     * @return email configuration
     */
    @Query(value = "SELECT email FROM EmailConfiguration email WHERE email.isActive = :isActive")
    EmailConfiguration findByIsActive(@Param("isActive") Boolean isActive);

}
