package ck.panda;

import java.util.Locale;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import ck.panda.util.audit.AuditingDateTimeProvider;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import ck.panda.util.audit.CurrentTimeDateTimeService;
import ck.panda.util.audit.DateTimeService;
import ck.panda.util.audit.UsernameAuditorAware;

/**
 * Spring boot application configuration class.
 */
@Configuration
@EnableAutoConfiguration(exclude = { ErrorMvcAutoConfiguration.class })
@ComponentScan
@EnableWebMvc
@SpringBootApplication
@EnableConfigurationProperties
@EnableTransactionManagement
@EnableJpaAuditing(dateTimeProviderRef = "dateTimeProvider")
@EnableJpaRepositories("ck.panda.domain.repository")
public class ApplicationConfig extends WebMvcConfigurerAdapter {
	/**
	 * Default spring boot main method.
	 *
	 * @param args to pass from command line
	 * @throws Exception if any error
	 */
	public static void main(String[] args) throws Exception {
		SpringApplication.run(ApplicationConfig.class, args);
	}

	/**
	 * Configures the DateTimeService.
	 *
	 * @return CurrentTimeDateTimeService implementation
	 */
	@Bean
	DateTimeService currentTimeDateTimeService() {
		return new CurrentTimeDateTimeService();
	}

	/**
	 * Configures the DateTimeProvider bean for providing date and time.
	 *
	 * @param dateTimeService DateTimeServiceObject.
	 * @return AuditingDateTimeProvider implementation
	 */
	@Bean
	DateTimeProvider dateTimeProvider(DateTimeService dateTimeService) {
		return new AuditingDateTimeProvider(dateTimeService);
	}

	/**
	 * Configures the auditProvider bean for providing updated and created user information.
	 *
	 * @return AuditingDateTimeProvider implementation
	 */
	@Bean
	AuditorAware<Long> auditorProvider() {
		return new UsernameAuditorAware();
	}

	@Bean
	public InternalResourceViewResolver getInternalResourceViewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/jsp/");
		resolver.setSuffix(".jsp");
		return resolver;
	}

	/**
	 * Instantiates validator bean for javax.validation.Validator interface.
	 *
	 * @return Validator implementation
	 */
	@Bean
	public javax.validation.Validator localValidatorFactoryBean() {
		return new LocalValidatorFactoryBean();
	}

	/**
	 * Internationalization messages configuration.
	 *
	 * @return MessageSource
	 */
	@Bean(name = "messageSource")
	public ReloadableResourceBundleMessageSource getMessageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:i18n/messages/messages");
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setCacheSeconds(1000);
		return messageSource;
	}

	/**
	 * Configuring default locale resolver.
	 *
	 * @return LocaleResolver
	 */
	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver slr = new SessionLocaleResolver();
		slr.setDefaultLocale(Locale.ENGLISH);
		return slr;
	}

	/**
	 * Locale change interceptor configuration.
	 *
	 * @return LocaleChangeInterceptor
	 */
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
		lci.setParamName("lang");
		return lci;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}
}
