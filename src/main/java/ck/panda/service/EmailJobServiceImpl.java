package ck.panda.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import ck.panda.domain.entity.EmailConfiguration;
import ck.panda.domain.entity.User;
import ck.panda.email.util.Email;
import ck.panda.rabbitmq.util.EmailEvent;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Service
public class EmailJobServiceImpl implements EmailJobService {
	/** Template encoding. */
	private static final String DEFAULT_ENCODING = "utf-8";

	/** EmailConfigurationService reference to get email settings. */
	@Autowired
	private EmailConfigurationService emailServiceConfig;

	/** RabbitTemplate for send message to queue. */
	@Autowired
	private RabbitTemplate emailTemplate;

	/** Configuration service reference . */
	@Autowired
	private Configuration freemarkerConfiguration;

	/** Email service reference . */
	@Autowired
	private EmailService emailService;

	/** User service reference . */
	@Autowired
	private UserService userService;

	/** Server email pattern. */
    @Value(value = "${spring.rabbit.server.email.pattern}")
    private String routingKey;

    /** Email event entity. */
    private EmailEvent eventResponse = null;

	@Override
	public void sendEmail(String eventObject) throws Exception {
		EmailConfiguration emailConfiguration = emailServiceConfig.findByIsActive(true);
		// Event record from email listener call.
		ObjectMapper eventmapper = new ObjectMapper();
		eventResponse = eventmapper.readValue(eventObject, EmailEvent.class);
		Email mimeEmail = new Email();
		User user = userService.find(Long.parseLong(eventResponse.getUser()));
		mimeEmail.setFrom(emailConfiguration.getEmailFrom());
		mimeEmail.setTo(user.getEmail());
		mimeEmail.setBody(generateCountContent(eventResponse));
		mimeEmail.setSubject(eventResponse.getSubject());
		emailService.sendMail(mimeEmail);
	}

	@Override
	public void sendMessageToQueue(EmailEvent emailEvent) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String message = mapper.writeValueAsString(emailEvent);
		// send message to email queue.
		emailTemplate.convertAndSend(routingKey, MessageBuilder.withBody(message.getBytes()).build());
	}

	/**
	 * Get the email content after dynamic variables applied in html template.
	 *
	 * @param email email template dynamic variables.
	 * @return email content.
	 * @throws MessagingException unhandled exception.
	 */
	private String generateCountContent(EmailEvent email) throws MessagingException {
		//Defining the model object for the given Freemarker template
		Map<String, String> context = new HashMap<String, String>();
		context.put("name", email.getEvent());
		String templateName = "email_template.ftl";
		return generateContent(context, templateName);
	}
	/**
	 * Apply dynamic content from context.
	 *
	 * @param context Hashmap variable for dynamic content.
	 * @param templateName template name.
	 * @return email content.
	 * @throws MessagingException unhandled exception.
	 */
	private String generateContent(Map context, String templateName) throws MessagingException {
		try {
			// free marker template for dynamic content.
			Template template = freemarkerConfiguration.getTemplate(templateName, DEFAULT_ENCODING);
			return FreeMarkerTemplateUtils.processTemplateIntoString(template, context);
		} catch (IOException e) {
			throw new MessagingException("FreeMarker", e);
		} catch (TemplateException e) {
			throw new MessagingException("FreeMarker", e);
		}
	}

}
