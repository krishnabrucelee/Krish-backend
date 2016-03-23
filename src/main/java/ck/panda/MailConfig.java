package ck.panda;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

@Configuration
public class MailConfig {
	/** Email server host details.*/
    @Value("${spring.mail.host}")
    private String host;

    /** Email server port details.*/
    @Value("${spring.mail.port}")
    private Integer port;

    /** Email template path.*/
    @Value("${spring.template.view.prefix}")
    private String viewPrefix;

    /** Email template suffix.*/
    @Value("${spring.template.view.suffix}")
    private String viewSuffix;

    /**
     * View resolver for email template.
     *
     * @return viewResolver.
     */
    @Bean
    public ViewResolver freeMarkerViewResolver() {
        FreeMarkerViewResolver viewResolver = new FreeMarkerViewResolver();
        viewResolver.setCache(true);
        viewResolver.setPrefix("");
        viewResolver.setSuffix(viewSuffix);
        viewResolver.setContentType("text/html;charset=UTF-8");
        viewResolver.setExposeSpringMacroHelpers(true);
        viewResolver.setExposeRequestAttributes(false);
        viewResolver.setExposeSessionAttributes(false);
        return viewResolver;
    }

    /**
     * Get mail sender object.
     *
     * @return JavaMailSenderImpl.
     */
    @Bean
    public JavaMailSenderImpl javaMailService() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(host);
        javaMailSender.setPort(port);
        javaMailSender.setJavaMailProperties(getMailProperties());
        return javaMailSender;
    }

	/**
	 * Get mail properties.
	 *
	 * @return properties.
	 */
    private Properties getMailProperties() {
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.auth", "false");
        properties.setProperty("mail.smtp.starttls.enable", "false");
        properties.setProperty("mail.debug", "false");
        return properties;
    }

     /**
      * Get FreeMarker Configuration Factory Bean for freemarker template.
      *
      * @return FreeMarkerConfigurationFactoryBean.
      */
    @Bean(name = "freemarkerConfiguration")
   	public FreeMarkerConfigurationFactoryBean freemarkerConfiguration() {
      FreeMarkerConfigurationFactoryBean freemarkerConfiguration = new FreeMarkerConfigurationFactoryBean();
      freemarkerConfiguration.setTemplateLoaderPath(viewPrefix);
      freemarkerConfiguration.setPreferFileSystemAccess(true);
      freemarkerConfiguration.setDefaultEncoding("UTF-8");
   	  return freemarkerConfiguration;
   	}
}