package ck.panda.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.MessagingException;
import org.json.JSONObject;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import ck.panda.constants.CloudStackConstants;
import ck.panda.constants.EmailConstants;
import ck.panda.constants.EventTypes;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.EmailConfiguration;
import ck.panda.domain.entity.EmailTemplate;
import ck.panda.domain.entity.Organization;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.Zone;
import ck.panda.email.util.Account;
import ck.panda.email.util.Alert;
import ck.panda.email.util.Email;
import ck.panda.email.util.EmailPayment;
import ck.panda.email.util.Invoice;
import ck.panda.email.util.Resource;
import ck.panda.email.util.Usage;
import ck.panda.rabbitmq.util.EmailEvent;
import ck.panda.util.EncryptionUtil;
import ck.panda.util.PingService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Email job service implementation class used for send email template for
 * user,domain admin. root admin for following events user signup, password
 * reset, system alerts, invoice generation, resource limit exceed, invoice
 * generation overdue, etc.,
 */
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

    /** chinese email template directory. */
    @Value(value = "${chinese.template.dir}")
    private String chineseTemplatePath;

    /** english email template directory. */
    @Value(value = "${english.template.dir}")
    private String englishTemplatePath;

    /** Invoice base directory. */
    @Value("${invoice.base.path}")
    private String invoiceBasePath;

    /** Constant for generic UTF. */
    public static final String CS_UTF = "utf-8";

    /** Constant for generic AES. */
    public static final String CS_AES = "AES";

    /** Secret key value is append. */
    @Value(value = "${aes.salt.secretKey}")
    private String secretKey;

    /** Email event entity. */
    private EmailEvent eventResponse = null;

    /** Email payment event entity. */
    private EmailPayment eventPaymentResponse = null;

    /** Zone service reference. */
    @Autowired
    private ZoneService zoneService;

    /** Organization service reference. */
    @Autowired
    private OrganizationService organizationService;

    /** User service reference. */
    @Autowired
    private EmailTypeTemplateService emailTypeTemplateService;

    /** Ping service reference. */
    @Autowired
    private PingService pingService;

    /** Domain service reference. */
    @Autowired
    private DomainService domainService;

    @Override
    public void sendEmail(String eventObject) throws Exception {
        EmailConfiguration emailConfiguration = emailServiceConfig.findByIsActive(true);
        // Event record from email listener call.
        ObjectMapper eventmapper = new ObjectMapper();
        eventResponse = eventmapper.readValue(eventObject, EmailEvent.class);
        Email mimeEmail = new Email();
        EmailTemplate templateName = new EmailTemplate();
        Domain domain = domainService.findbyUUID(eventResponse.getDomainId());
        if (eventResponse.getEventType().equals(EventTypes.EVENT_USAGE_UPDATE_FAILED)) {
            User user = null;
            eventResponse.setEvent(eventResponse.getEventType());
            mimeEmail.setFrom(emailConfiguration.getEmailFrom());
            mimeEmail.setTo(domain.getEmail());
            mimeEmail.setBody(generateCountContent(eventResponse, user, domain, emailConfiguration, templateName, mimeEmail, eventPaymentResponse));
            emailService.sendMail(mimeEmail);
        }
        if (eventResponse.getEventType().equals(EventTypes.EVENT_MONTHLY_INVOICE)) {
            User user = null;
            String usageResponse = pingService.getInvoiceById(eventResponse.getInvoiceId());
            eventResponse.setEvent(eventResponse.getEventType());
            mimeEmail.setInvoice(usageResponse);
            mimeEmail.setBody(generateCountContent(eventResponse, user, domain, emailConfiguration, templateName, mimeEmail, eventPaymentResponse));
            emailService.sendMail(mimeEmail);
        }
        if (eventResponse.getEventType().equals(EmailConstants.EMAIL_PAYMENT)) {
            User user = null;
            eventPaymentResponse = eventmapper.readValue(eventObject, EmailPayment.class);
            eventResponse.setEvent(eventPaymentResponse.getEventType());
            mimeEmail.setFrom(eventPaymentResponse.getOrganisationEmail());
            mimeEmail.setTo(eventPaymentResponse.getCompanyEmail());
            mimeEmail.setBody(generateCountContent(eventResponse, user, domain, emailConfiguration, templateName, mimeEmail, eventPaymentResponse));
            emailService.sendMail(mimeEmail);
        }
        if (eventResponse.getEventType().equals(EmailConstants.EMAIL_CAPACITY)
                || eventResponse.getEventType().equals(EmailConstants.SYSTEM_ERROR)) {
            User user = null;
            Organization orgnizationDetails = organizationService.findByIsActive(true);
            mimeEmail.setFrom(emailConfiguration.getEmailFrom());
            mimeEmail.setTo(orgnizationDetails.getEmail());
            mimeEmail.setBody(generateCountContent(eventResponse, user, domain, emailConfiguration, templateName, mimeEmail, eventPaymentResponse));
            emailService.sendMail(mimeEmail);
        }
        if (eventResponse.getEvent() != null && eventResponse.getEvent().equals(EventTypes.EVENT_USER_CREATE)
                || eventResponse.getEvent().equals(EventTypes.EVENT_USER_DELETE)
                || eventResponse.getEvent().equals(EventTypes.EVENT_USER_UPDATE)) {
            User user = userService.find(Long.parseLong(eventResponse.getUser()));
            mimeEmail.setFrom(emailConfiguration.getEmailFrom());
            mimeEmail.setTo(user.getEmail());
            mimeEmail.setBody(generateCountContent(eventResponse, user, domain, emailConfiguration, templateName, mimeEmail, eventPaymentResponse));
            emailService.sendMail(mimeEmail);
        }
    }

    @Override
    public void sendMessageToQueue(EmailEvent emailEvent) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String message = mapper.writeValueAsString(emailEvent);
        // send message to email queue.
        emailTemplate.convertAndSend(routingKey, MessageBuilder.withBody(message.getBytes()).build());
    }

    @Override
    public void sendMessageToEmailPaymentQueue(EmailPayment emailEvent) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String message = mapper.writeValueAsString(emailEvent);
        // send message to email queue.
        emailTemplate.convertAndSend(routingKey, MessageBuilder.withBody(message.getBytes()).build());
    }

    /**
     * Get the email content after dynamic variables applied in html template.
     *
     * @param email email template dynamic variables.
     * @param user user details
     * @param emailConfiguration email configuration details
     * @param templateName template name configuration details.
     * @param mimeEmail email content.
     * @return mimeEmail email details.
     * @throws Exception unhandled error
     */
    private String generateCountContent(EmailEvent email, User user, Domain domain, EmailConfiguration emailConfiguration,
            EmailTemplate templateName, Email mimeEmail, EmailPayment emailPayment) throws Exception {
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
            context.put(EmailConstants.EMAIL_TEMPLATE_user, account);
            // sample template.
            templateName = emailTypeTemplateService.findByEventAndIsActive(EmailConstants.EMAIL_ACCOUNT_SIGNUP, true);
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
            context.put(EmailConstants.EMAIL_TEMPLATE_user, account);
            templateName = emailTypeTemplateService.findByEventAndIsActive(EmailConstants.EMAIL_ACCOUNT_REMOVAL, true);
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
            context.put(EmailConstants.EMAIL_TEMPLATE_user, account);
            templateName = emailTypeTemplateService.findByEventAndIsActive(EmailConstants.EMAIL_PASSWORD_RESET, true);
            if (templateName != null) {
                mimeEmail.setRecipientType(templateName.getRecipientType().toString());
                mimeEmail.setSubject(templateName.getSubject());
                return validateTemplate(user, templateName, context, emailConfiguration);
            }
        }
        if (email.getEventType().equals(EmailConstants.SYSTEM_ERROR)) {
            context.clear();
            Alert alert = new Alert();
            alert.setSubject(email.getSubject());
            alert.setDetails(email.getMessageBody());
            // resource uuid for zone name
            Zone zone = zoneService.findByUUID(email.getResourceUuid());
            alert.setZone(zone.getName());
            // resource id for pod id
            alert.setPodId(email.getResourceId());
            context.put(EmailConstants.EMAIL_TEMPLATE_alert, alert);
            templateName = emailTypeTemplateService.findByEventAndIsActive(EmailConstants.EMAIL_SYSTEM_ERROR, true);
            if (templateName != null) {
                mimeEmail.setRecipientType(templateName.getRecipientType().toString());
                mimeEmail.setSubject(templateName.getSubject());
                return validateTemplate(user, templateName, context, emailConfiguration);
            }
        }
        if (email.getEventType().equals(EventTypes.EVENT_USAGE_UPDATE_FAILED)) {
            context.clear();
            Usage usage = new Usage();
            usage.setDomainUserName(domain.getPrimaryFirstName() + " " + domain.getLastName());
            usage.setDomainName(domain.getPortalUserName());
            //Start date
            usage.setStartDate(email.getStartDate());
            //End date
            usage.setEndDate(email.getEndDate());
            usage.setStatus(email.getMessageBody());
            context.put(EmailConstants.EMAIL_usage, usage);
            templateName = emailTypeTemplateService.findByEventAndIsActive(EmailConstants.EMAIL_USAGE_UPDATE_FAILED, true);
            if (templateName != null) {
                mimeEmail.setRecipientType(templateName.getRecipientType().toString());
                mimeEmail.setSubject(templateName.getSubject());
                return validateTemplate(user, templateName, context, emailConfiguration);
            }
        }
        if (email.getEventType().equals(EmailConstants.EMAIL_CAPACITY)) {
            context.clear();
            Resource resource = new Resource();
            templateName = emailTypeTemplateService.findByEventAndIsActive(EmailConstants.EMAIL_CAPACITY, true);
            if (templateName != null) {
                mimeEmail.setSubject(templateName.getSubject());
                // zone name in resource uuid.
                resource.setZone(email.getResourceUuid());
                resource.setPercentage(email.getMessageBody());
                resource.setResourceName(email.getEvent());
                if (email.getResources().get(EmailConstants.EMAIL_Cpu) != null) {
                    resource.setCpu(email.getResources().get(EmailConstants.EMAIL_Cpu));
                }
                if (email.getResources().get(EmailConstants.EMAIL_Memory) != null) {
                    resource.setMemory(email.getResources().get(EmailConstants.EMAIL_Memory));
                }
                if (email.getResources().get(EmailConstants.EMAIL_Primary_storage) != null) {
                    resource.setPrimaryStorage(email.getResources().get(EmailConstants.EMAIL_Primary_storage));
                }
                if (email.getResources().get(EmailConstants.EMAIL_Ip) != null) {
                    resource.setIp(email.getResources().get(EmailConstants.EMAIL_Ip));
                }
                if (email.getResources().get(EmailConstants.EMAIL_Secondary_storage) != null) {
                    resource.setSecondaryStorage(email.getResources().get(EmailConstants.EMAIL_Secondary_storage));
                }
                context.put(EmailConstants.EMAIL_TEMPLATE_capacity, resource);
                return validateTemplate(user, templateName, context, emailConfiguration);
            }
        }
        if (email.getEventType().equals(EventTypes.EVENT_MONTHLY_INVOICE)) {
            context.clear();
            templateName = emailTypeTemplateService.findByEventAndIsActive(EmailConstants.EMAIL_INVOICE, true);
            if (templateName != null) {
                mimeEmail.setSubject(templateName.getSubject());
                Invoice invoice = new Invoice();
                SimpleDateFormat formatter = new SimpleDateFormat(EmailConstants.EMAIL_INVOIVE_DATE_FORMAT);
                JSONObject organisationResult = null;
                JSONObject domainResult = null;
                JSONObject usageResult = new JSONObject(mimeEmail.getInvoice());
                if (usageResult.has(EmailConstants.EMAIL_INVOIVE_organization)) {
                    organisationResult = usageResult.getJSONObject(EmailConstants.EMAIL_INVOIVE_organization);
                }
                if (usageResult.has(CloudStackConstants.CS_DOMAIN)) {
                    domainResult = usageResult.getJSONObject(CloudStackConstants.CS_DOMAIN);
                }
                mimeEmail.setFrom(organisationResult.getString(EmailConstants.EMAIL_INVOICE_email));
                mimeEmail.setTo(domainResult.getString(EmailConstants.EMAIL_INVOICE_email));
                HashMap<String, String> fileMap = new HashMap<>();
                fileMap.put(EmailConstants.EMAIL_INVOICE_fileAttachment,
                        invoiceBasePath + File.separator + usageResult.getString(EmailConstants.EMAIL_INVOICE_filePath)
                                + File.separator + emailConfiguration.getEmailLanguage() + File.separator + usageResult.getString(EmailConstants.EMAIL_INVOICE_fileName)
                                + ".pdf");
                mimeEmail.setAttachments(fileMap);
                invoice.setOrganizationAddress(organisationResult.getString(EmailConstants.EMAIL_INVOICE_address));
                invoice.setOrganizationEmail(organisationResult.getString(EmailConstants.EMAIL_INVOICE_email));
                invoice.setOrganizationName(organisationResult.getString(EmailConstants.EMAIL_INVOICE_name));
                invoice.setOrganizationPhone(organisationResult.getString(EmailConstants.EMAIL_INVOICE_phone));
                invoice.setDomainEmail(mimeEmail.getTo());
                invoice.setDomainName(domainResult.getString(EmailConstants.EMAIL_INVOICE_name));
                invoice.setDomainPhone(domainResult.getString(EmailConstants.EMAIL_INVOICE_phone));
                invoice.setInvoiceNumber(usageResult.getString(EmailConstants.EMAIL_INVOICE_invoiceNumber));
                invoice.setBillPeriod(usageResult.getString(EmailConstants.EMAIL_INVOICE_billPeriod));
                if (usageResult.getString(EmailConstants.EMAIL_INVOICE_dueDate) == null) {
                    invoice.setDueDate(formatter.parse(usageResult.getString(EmailConstants.EMAIL_INVOICE_dueDate)));
                    invoice.setDate(formatter.parse(usageResult.getString(EmailConstants.EMAIL_INVOICE_date)));
                }
                if (usageResult.getString(EmailConstants.EMAIL_INVOICE_generatedDate) == null) {
                    invoice.setGeneratedDate(
                            formatter.parse(usageResult.getString(EmailConstants.EMAIL_INVOICE_generatedDate)));
                }
                invoice.setTotalCost(usageResult.getString(EmailConstants.EMAIL_INVOICE_totalCost));
                invoice.setCurrency(usageResult.getString(EmailConstants.EMAIL_INVOICE_currency));
                context.put(EmailConstants.EVENT_MONTHLY_INVOICE, invoice);
                return validateTemplate(user, templateName, context, emailConfiguration);
            }
        }

        if (email.getEventType().equals(EmailConstants.EMAIL_PAYMENT)) {
            context.clear();
            context.put(EmailConstants.EMAIL_payment, emailPayment);
            templateName = emailTypeTemplateService.findByEventAndIsActive(EmailConstants.EMAIL_PAYMENT, true);
            if (templateName != null) {
                mimeEmail.setRecipientType(templateName.getRecipientType().toString());
                mimeEmail.setSubject(templateName.getSubject());
                return validateTemplate(user, templateName, context, emailConfiguration);
            }
        }
        return null;
    }

    /**
     * Validate template for email.
     *
     * @param user user details
     * @param templateName template name configuration details
     * @param context email context details
     * @param emailConfiguration email configuration details
     * @return generate context of email
     * @throws MessagingException unhandled message exception
     */
    private String validateTemplate(User user, EmailTemplate templateName, Map<String, Object> context,
            EmailConfiguration emailConfiguration) throws MessagingException {
        if (user != null) {
            if (user.getLanguage() != null) {
                if (user.getLanguage().equals(EmailConstants.EMAIL_English)
                        && templateName.getEnglishLanguage() != null) {
                    return generateContent(context, templateName.getEventName(), englishTemplatePath);
                }
                if (user.getLanguage().equals(EmailConstants.EMAIL_English)
                        && templateName.getEnglishLanguage() == null) {
                    return generateContent(context, templateName.getEventName(), chineseTemplatePath);
                }
                if (user.getLanguage().equals(EmailConstants.EMAIL_Chinese)
                        && templateName.getChineseLanguage() != null) {
                    return generateContent(context, templateName.getEventName(), chineseTemplatePath);
                }
                if (user.getLanguage().equals(EmailConstants.EMAIL_Chinese)
                        && templateName.getChineseLanguage() == null) {
                    return generateContent(context, templateName.getEventName(), englishTemplatePath);
                }
            }
        }
        if (templateName.getEnglishLanguage() != null && templateName.getChineseLanguage() == null) {
            return generateContent(context, templateName.getEventName(), englishTemplatePath);
        }
        if (templateName.getEnglishLanguage() == null && templateName.getChineseLanguage() != null) {
            return generateContent(context, templateName.getEventName(), chineseTemplatePath);
        }
        if ((templateName.getEnglishLanguage() != null && templateName.getChineseLanguage() != null)
                && emailConfiguration.getEmailLanguage().equals(EmailConstants.EMAIL_English)) {
            return generateContent(context, templateName.getEventName(), englishTemplatePath);
        }
        if ((templateName.getEnglishLanguage() != null && templateName.getChineseLanguage() != null)
                && emailConfiguration.getEmailLanguage().equals(EmailConstants.EMAIL_Chinese)) {
            return generateContent(context, templateName.getEventName(), chineseTemplatePath);
        }
        return null;
    }

    /**
     * Apply dynamic content from context.
     *
     * @param context Hashmap variable for dynamic content.
     * @param templateName template name.
     * @param templatePath email template path.
     * @return email content.
     * @throws MessagingException unhandled exception.
     */
    private String generateContent(Map<String, Object> context, String templateName, String templatePath)
            throws MessagingException {
        try {
            // free marker template for dynamic content.
            freemarkerConfiguration.setDirectoryForTemplateLoading(new File(templatePath));
            Template template = freemarkerConfiguration.getTemplate(templateName, DEFAULT_ENCODING);
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, context);
        } catch (IOException e) {
            throw new MessagingException(EmailConstants.EMAIL_FreeMarker, e);
        } catch (TemplateException e) {
            throw new MessagingException(EmailConstants.EMAIL_FreeMarker, e);
        }
    }
}
