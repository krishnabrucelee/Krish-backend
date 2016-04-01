package ck.panda.service;

import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.EmailConfiguration;
import ck.panda.email.util.*;
import ck.panda.util.EncryptionUtil;
import ck.panda.util.error.exception.CustomGenericException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.internet.MimeMessage;
import java.lang.reflect.Field;
import java.util.Base64;

/**
 * Email service for send mail.
 */
@Service
public class EmailServiceImpl implements EmailService {

    /** Constant for sender . */
    private static final String SENDER = "from";

    /** Constant for recipient . */
    private static final String RECIPIENT = "to";

    /** Constant for body . */
    private static final String BODY = "body";

    /** Constant for validation . */
    private static final String ERROR = "value cannot be null";

    /** EmailConfigurationService reference . */
    @Autowired
    private EmailConfigurationService emailServiceConfig;

    /** Java Mail Service for mail send . */
    @Autowired
    private JavaMailSenderImpl javaMailService;

    /** Constant for generic UTF. */
    public static final String CS_UTF = "utf-8";

    /** Constant for generic AES. */
    public static final String CS_AES = "AES";

    /** Secret key value is append. */
    @Value(value = "${aes.salt.secretKey}")
    private String secretKey;

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
            String strEncoded = Base64.getEncoder().encodeToString(secretKey.getBytes(CS_UTF));
            byte[] decodedKey = Base64.getDecoder().decode(strEncoded);
            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, CS_AES);
            String decryptedPassword = new String(EncryptionUtil.decrypt(emailConfiguration.getPassword(), originalKey));
            javaMailService.setPassword(decryptedPassword);
            MimeMessage message = javaMailService.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(email.getFrom());
            helper.setTo(email.getTo());
            helper.setSubject(email.getSubject());
            String content = email.getBody();
            helper.setText(content, true);
            if (email.getAttachments() != null) {
                for (String attachmentFile : email.getAttachments().keySet()) {
                    // Add the attachment
                    FileSystemResource file = new FileSystemResource( email.getAttachments().get(attachmentFile));
                    helper.addAttachment(file.getFilename(), file);
                }
            }
            javaMailService.send(message);
            return true;
        } catch (MailException me) {
            me.printStackTrace();
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
            if (value == null && (field.getName().equalsIgnoreCase(SENDER) || field.getName().equalsIgnoreCase(RECIPIENT)
                    || field.getName().equalsIgnoreCase(BODY))) {
                hasNullValues = true;
                break;
            }
            if (hasNullValues) {
                return field.getName() + ERROR;
            }
        }
        return "";
    }
}