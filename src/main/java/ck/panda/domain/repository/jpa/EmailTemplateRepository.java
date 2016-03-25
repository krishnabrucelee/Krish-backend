package ck.panda.domain.repository.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.EmailTemplate;

/**
 * Jpa Repository for Email template entity.
 *
 */
@Service
public interface EmailTemplateRepository extends PagingAndSortingRepository<EmailTemplate, Long> {

    /**
     * Get email template by event name.
     * @param eventName event name
     * @return EmailTemplate
     */
    @Query(value = "SELECT email FROM EmailTemplate email WHERE email.eventName = :eventName")
    EmailTemplate findByEventName(@Param("eventName") String eventName);

}
