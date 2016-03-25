package ck.panda.service;

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
     * Get email template by event name.
     * @param eventName event name
     * @return EmailTemplate
     */
    EmailTemplate findByEventName(String eventName);

}