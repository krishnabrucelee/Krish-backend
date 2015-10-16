package ck.panda.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 *
 */
@Configuration
public class BeanFactory {

    /** Bean id error static attribute. */
    private static final String BEAN_ID_ERROR = "errors";

    /** Application context attribute. */
    @Autowired
    private ApplicationContext context;
}
