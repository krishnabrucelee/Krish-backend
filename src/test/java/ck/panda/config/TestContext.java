package ck.panda.config;


import ck.panda.service.DepartmentService;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 *
 * @author Krishna <krishnakumar@assistanz.com>
 */
@Configuration
public class TestContext {

    /**
     * MESSAGE_SOURCE_BASE_NAME.
     */
    private static final String MESSAGE_SOURCE_BASE_NAME = "i18n/messages";

    /**
     * @return MessageSource
     */
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

        messageSource.setBasename(MESSAGE_SOURCE_BASE_NAME);
        messageSource.setUseCodeAsDefaultMessage(true);

        return messageSource;
    }

    /**
     * @return Mockito.mock(DepartmentService.class)
     */
    @Bean
    public DepartmentService departmentService() {
        return Mockito.mock(DepartmentService.class);
    }
}
