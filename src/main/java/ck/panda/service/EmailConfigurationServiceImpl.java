package ck.panda.service;

import java.util.Base64;
import java.util.List;
import java.util.Properties;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.EmailConfiguration;
import ck.panda.domain.repository.jpa.EmailConfigurationRepository;
import ck.panda.util.EncryptionUtil;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Email configuration service implementation.
 */
@Service
public class EmailConfigurationServiceImpl implements EmailConfigurationService {

    @Autowired
    private EmailConfigurationRepository emailRepo;

    /** Secret key value is append. */
    @Value(value = "${aes.salt.secretKey}")
    private String secretKey;

    /** Constant for generic UTF. */
    public static final String CS_UTF = "utf-8";

    /** Constant for generic AES. */
    public static final String CS_AES = "AES";

    @Override
    public void sendEmailTo(String emailTo, String emailSubject, String emailBody) throws Exception {
        EmailConfiguration email = emailRepo.findByIsActive(true);
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        Properties mailProperties = new Properties();
        mailSender.setJavaMailProperties(mailProperties);
        mailSender.setHost(email.getHost());
        mailSender.setPort(email.getPort());
        mailSender.setUsername(email.getUserName());
        String strEncoded = Base64.getEncoder().encodeToString(secretKey.getBytes(CS_UTF));
        byte[] decodedKey = Base64.getDecoder().decode(strEncoded);
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, CS_AES);
        String encryptedPassword = new String(EncryptionUtil.decrypt(email.getPassword(), originalKey));
        email.setPassword(encryptedPassword);
        mailSender.setPassword(email.getPassword());
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeHelper = new MimeMessageHelper(mimeMessage, true);
        mimeHelper.setSubject(emailSubject);
        mimeHelper.setTo(emailTo);
        mimeHelper.setText(emailBody, true);
        mailSender.send(mimeMessage);
    }

    @Override
    public EmailConfiguration save(EmailConfiguration email) throws Exception {
        email.setIsActive(true);
        if (email.getPassword() != null) {
            String strEncoded = Base64.getEncoder().encodeToString(secretKey.getBytes(CS_UTF));
            byte[] decodedKey = Base64.getDecoder().decode(strEncoded);
            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, CS_AES);
            String encryptedPassword = new String(EncryptionUtil.encrypt(email.getPassword(), originalKey));
            email.setPassword(encryptedPassword);
        }
        return emailRepo.save(email);
    }

    @Override
    public EmailConfiguration update(EmailConfiguration email) throws Exception {
        if (email.getPassword() != null) {
            EmailConfiguration emailConfig = emailRepo.findByIsActive(true);
            if (!emailConfig.getPassword().equals(email.getPassword())) {
                String strEncoded = Base64.getEncoder().encodeToString(secretKey.getBytes(CS_UTF));
                byte[] decodedKey = Base64.getDecoder().decode(strEncoded);
                SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, CS_AES);
                String encryptedPassword = new String(EncryptionUtil.encrypt(email.getPassword(), originalKey));
                email.setPassword(encryptedPassword);
            }
        }
        return emailRepo.save(email);
    }

    @Override
    public void delete(EmailConfiguration email) throws Exception {
    }

    @Override
    public void delete(Long id) throws Exception {
    }

    @Override
    public EmailConfiguration find(Long id) throws Exception {
        return null;
    }

    @Override
    public Page<EmailConfiguration> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return null;
    }

    @Override
    public List<EmailConfiguration> findAll() throws Exception {
        return null;
    }

    @Override
    public EmailConfiguration findByIsActive(Boolean isActive) {
        return emailRepo.findByIsActive(true);
    }
}
