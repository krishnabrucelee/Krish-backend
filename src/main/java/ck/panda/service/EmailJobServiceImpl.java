package ck.panda.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
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

import ck.panda.constants.CloudStackConstants;
import ck.panda.constants.EmailConstants;
import ck.panda.constants.EventTypes;
import ck.panda.domain.entity.EmailConfiguration;
import ck.panda.domain.entity.EmailTemplate;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.User.UserType;
import ck.panda.email.util.Account;
import ck.panda.email.util.Email;
import ck.panda.rabbitmq.util.EmailEvent;
import ck.panda.util.EncryptionUtil;
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

    /** Constant for generic UTF. */
    public static final String CS_UTF = "utf-8";

    /** Constant for generic AES. */
    public static final String CS_AES = "AES";

    /** Secret key value is append. */
    @Value(value = "${aes.salt.secretKey}")
    private String secretKey;

    /** Email event entity. */
    private EmailEvent eventResponse = null;

    /** User service reference . */
    @Autowired
    private EmailTypeTemplateService emailTypeTemplateService;

    @Override
    public void sendEmail(String eventObject) throws Exception {
        EmailConfiguration emailConfiguration = emailServiceConfig.findByIsActive(true);
        // Event record from email listener call.
        ObjectMapper eventmapper = new ObjectMapper();
        eventResponse = eventmapper.readValue(eventObject, EmailEvent.class);
        Email mimeEmail = new Email();
        EmailTemplate templateName = new EmailTemplate();
        if (eventResponse.getEventType().equals("CAPACITY")) {
            User user = userService.findAllByUserTypeAndIsActive(true, UserType.ROOT_ADMIN);
            mimeEmail.setFrom(emailConfiguration.getEmailFrom());
            mimeEmail.setTo(user.getEmail());
            mimeEmail.setBody(generateCountContent(eventResponse, user, emailConfiguration, templateName, mimeEmail));
            emailService.sendMail(mimeEmail);
        } else {
            User user = userService.find(Long.parseLong(eventResponse.getUser()));
            mimeEmail.setFrom(emailConfiguration.getEmailFrom());
            mimeEmail.setTo(user.getEmail());
            mimeEmail.setBody(generateCountContent(eventResponse, user, emailConfiguration, templateName, mimeEmail));
            if (mimeEmail.getRecipientType().equals(user.getType().toString())) {
                emailService.sendMail(mimeEmail);
            }
        }
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
     * @throws Exception unhandled error
     */
    private String generateCountContent(EmailEvent email, User user, EmailConfiguration emailConfiguration,
            EmailTemplate templateName, Email mimeEmail) throws Exception {
        // Defining the model object for the given Freemarker template
        Map<String, Object> context = new HashMap<String, Object>();
        Account account = new Account();

        if (email.getEvent().equals(EventTypes.EVENT_USER_CREATE)) {
            context.clear();
            account.setUserName(user.getUserName());
            if (user.getPassword() != null) {
                String strEncoded = Base64.getEncoder().encodeToString(secretKey.getBytes(CS_UTF));
                byte[] decodedKey = Base64.getDecoder().decode(strEncoded);
                SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, CS_AES);
                String decryptedPassword = new String(EncryptionUtil.decrypt(user.getPassword(), originalKey));
                account.setPassword(decryptedPassword);
            } else {
                account.setPassword(user.getPassword());
            }
            account.setFirstName(user.getFirstName());
            account.setLastName(user.getLastName());
            account.setUuid(user.getUuid());
            account.setStatus(user.getStatus().toString());
            account.setType(user.getType().toString());
            account.setRole(user.getRole().getName());
            account.setClientName(user.getDomain().getName());
            account.setClientEmail(user.getDomain().getEmail());
            account.setClientPhone(user.getDomain().getPhone());
            account.setClientCompanyNameAbbreviation(user.getDomain().getCompanyNameAbbreviation());
            account.setPandaUrl(emailConfiguration.getApplicationUrl());
            account.setCreatedDateTime(user.getCreatedDateTime().toString());
            account.setCreatedBy(user.getCreatedBy().toString());
            context.put("user", account);
            // sample template.
            templateName = emailTypeTemplateService.findByEventName("ACCOUNT SIGNUP");
            if (templateName != null) {
                mimeEmail.setRecipientType(templateName.getRecipientType().toString());
                mimeEmail.setSubject(templateName.getSubject());
                return validateTemplate(user, templateName, context, emailConfiguration);
            }
        }
        if (email.getEvent().equals(EventTypes.EVENT_USER_DELETE)) {
            context.clear();
            account.setUserName(user.getUserName());
            account.setFirstName(user.getFirstName());
            account.setLastName(user.getLastName());
            account.setStatus(user.getStatus().toString());
            account.setUpdatedDateTime(user.getUpdatedDateTime().toString());
            account.setUpdatedBy(user.getUpdatedBy().toString());
            account.setClientName(user.getDomain().getName());
            account.setClientEmail(user.getDomain().getEmail());
            account.setClientPhone(user.getDomain().getPhone());
            account.setClientCompanyNameAbbreviation(user.getDomain().getCompanyNameAbbreviation());
            account.setPandaUrl(emailConfiguration.getApplicationUrl());
            context.put("user", account);
            templateName = emailTypeTemplateService.findByEventName("ACCOUNT REMOVAL");
            if (templateName != null) {
                mimeEmail.setRecipientType(templateName.getRecipientType().toString());
                mimeEmail.setSubject(templateName.getSubject());
                return validateTemplate(user, templateName, context, emailConfiguration);
            }
        }
        if (email.getEvent().equals(EventTypes.EVENT_USER_UPDATE)) {
            context.clear();
            account.setUserName(user.getUserName());
            if (user.getPassword() != null) {
                String strEncoded = Base64.getEncoder().encodeToString(secretKey.getBytes(CS_UTF));
                byte[] decodedKey = Base64.getDecoder().decode(strEncoded);
                SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, CS_AES);
                String decryptedPassword = new String(EncryptionUtil.decrypt(user.getPassword(), originalKey));
                account.setPassword(decryptedPassword);
            } else {
                account.setPassword(user.getPassword());
            }
            account.setFirstName(user.getFirstName());
            account.setLastName(user.getLastName());
            account.setPandaUrl(emailConfiguration.getApplicationUrl());
            account.setClientName(user.getDomain().getName());
            account.setClientCompanyNameAbbreviation(user.getDomain().getCompanyNameAbbreviation());
            context.put("user", account);
            templateName = emailTypeTemplateService.findByEventName("PASSWORD RESET");
            if (templateName != null) {
                mimeEmail.setRecipientType(templateName.getRecipientType().toString());
                mimeEmail.setSubject(templateName.getSubject());
                return validateTemplate(user, templateName, context, emailConfiguration);
            }
        }
        if (email.getEventType().equals(EmailConstants.SYSTEM_ERROR)) {
            context.put("alert", email);
            templateName = emailTypeTemplateService.findByEventName("SYSTEM ERROR");
            if (templateName != null) {
                mimeEmail.setRecipientType(templateName.getRecipientType().toString());
                mimeEmail.setSubject(templateName.getSubject());
                return validateTemplate(user, templateName, context, emailConfiguration);
            }
        }
        if (email.getEventType().equals(CloudStackConstants.CS_CAPACITY)) {
            context.put("capacity", email);
            templateName = emailTypeTemplateService.findByEventName("CAPACITY");
            if (templateName != null) {
                mimeEmail.setSubject(templateName.getSubject());
                return validateTemplate(user, templateName, context, emailConfiguration);
            }
        }
        return null;
    }

    private String validateTemplate(User user, EmailTemplate templateName, Map<String,Object> context, EmailConfiguration emailConfiguration) throws MessagingException {
        if (user.getLanguage() != null) {
            if (user.getLanguage().equals("English") && templateName.getEnglishLanguage() != null) {
                return generateContent(context, templateName.getEnglishLanguage());
            }
            if (user.getLanguage().equals("English") && templateName.getEnglishLanguage() == null) {
                return generateContent(context, templateName.getChineseLanguage());
            }
            if (user.getLanguage().equals("Chinese") && templateName.getChineseLanguage() != null) {
                return generateContent(context, templateName.getChineseLanguage());
            }
            if (user.getLanguage().equals("Chinese") && templateName.getChineseLanguage() == null) {
                return generateContent(context, templateName.getEnglishLanguage());
            }
        }
        if (templateName.getEnglishLanguage() != null && templateName.getChineseLanguage() == null) {
            return generateContent(context, templateName.getEnglishLanguage());
        }
        if (templateName.getEnglishLanguage() == null && templateName.getChineseLanguage() != null) {
            return generateContent(context, templateName.getChineseLanguage());
        }
        if ((templateName.getEnglishLanguage() != null && templateName.getChineseLanguage() != null)
                && emailConfiguration.getEmailLanguage().equals("English")) {
            return generateContent(context, templateName.getEnglishLanguage());
        }
        if ((templateName.getEnglishLanguage() != null && templateName.getChineseLanguage() != null)
                && emailConfiguration.getEmailLanguage().equals("Chinese")) {
            return generateContent(context, templateName.getChineseLanguage());
        }
        return null;
    }

    /**
     * Apply dynamic content from context.
     *
     * @param context
     *            Hashmap variable for dynamic content.
     * @param templateName
     *            template name.
     * @return email content.
     * @throws MessagingException
     *             unhandled exception.
     */
    private String generateContent(Map<String, Object> context, String templateName) throws MessagingException {
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
