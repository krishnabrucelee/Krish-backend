package ck.panda.service;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.EmailConfiguration;
import ck.panda.util.domain.CRUDService;

/**
 * Email configuration service.
 */
@Service
public interface EmailConfigurationService extends CRUDService<EmailConfiguration> {

    /**
     * Send email to desired person.
     *
     * @param emailTo email destination address
     * @param emailSubject subject of the email
     * @param emailBody body of the email
     * @throws MessagingException exception handles if occurs
     * @throws Exception
     */
    public void sendEmailTo(String emailTo, String emailSubject, String emailBody) throws MessagingException, Exception;

    /**
     * Get the email configuration details if it is active.
     *
     * @param isActive true/false
     * @return Email Configuration
     */
    public EmailConfiguration findByIsActive(Boolean isActive) throws Exception;

}
