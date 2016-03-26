package ck.panda.service;

import java.util.List;

import org.springframework.stereotype.Service;
import ck.panda.domain.entity.EmailTemplate;
import ck.panda.util.domain.CRUDService;

/**
 * Service interface for email template entity.
 *
 */
@Service
public interface EmailTypeTemplateService extends CRUDService<EmailTemplate> {

    /**
     * Find all email templates  by active status.
     *
     * @return email templates.
     * @throws Exception if error occurs.
     */
    List<EmailTemplate> findAllByActive() throws Exception;

    /**
     * Find template by isactive status and language.
     *
     * @param isActive status of the template.
     * @param eventName of the template.
     * @return email template.
     */
    List<EmailTemplate> findByEventName(String eventName) throws Exception;

    /**
     * Find email template by event name.
     *
     * @param isActive status of the event.
     * @param eventName of the email template.
     * @return email template.
     */
    EmailTemplate findByEventAndIsActive(String eventName, Boolean isActive) throws Exception;
}