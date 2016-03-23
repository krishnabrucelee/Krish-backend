package ck.panda.service;

import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.EmailConfiguration;
import ck.panda.email.util.*;
import ck.panda.util.error.exception.CustomGenericException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.lang.reflect.Field;

/**
 * Email service for send mail.
 */
@Service
public class EmailServiceImpl implements EmailService {

	@Autowired
	private EmailConfigurationService emailServiceConfig;

	@Autowired
	private JavaMailSenderImpl javaMailService;

	@Override
	public Boolean sendMail(Email email) throws Exception {
		try {
			String errorMessage = this.validateEmail(email);
			if (!errorMessage.equals("")) {
				throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, errorMessage);
			}
			EmailConfiguration emailConfiguration = emailServiceConfig.findByIsActive(true);
	        javaMailService.setHost(emailConfiguration.getHost());
	        javaMailService.setPort(emailConfiguration.getPort());
	        javaMailService.setUsername(emailConfiguration.getUserName());
	        javaMailService.setPassword(emailConfiguration.getPassword());
			MimeMessage message = javaMailService.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message);
			helper.setFrom(email.getFrom());
			helper.setTo(email.getTo());
			helper.setSubject(email.getSubject());
			String content = email.getBody();
			helper.setText(content, true);
			if (email.getAttachments() != null) {
				for (String attachmentFile : email.getAttachments().keySet()) {
					// Add the attachment
					final InputStreamSource attachmentSource = new ByteArrayResource(
							email.getAttachments().get(attachmentFile));
					helper.addAttachment(attachmentFile, attachmentSource);
				}
			}
			javaMailService.send(message);
			return true;
		} catch (MailException me) {
			return false;
		}
	}

	/**
     * Validate the email.
     *
     * @param email reference of the email.
     * @return error message.
     * @throws Exception error occurs
     */
	private String validateEmail(Email email) throws Exception {
		Boolean hasNullValues = false;
		for (Field field : email.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			Object value = field.get(email);
			if (value == null && (field.getName().equalsIgnoreCase("from") || field.getName().equalsIgnoreCase("to")
					|| field.getName().equalsIgnoreCase("body"))) {
				hasNullValues = true;
				break;
			}
			if (hasNullValues) {
				return field.getName() + "value cannot be null";
			}
		}
		return "";
	}
}