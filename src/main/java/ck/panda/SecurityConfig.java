package ck.panda;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ck.panda.constants.GenericConstants;
import ck.panda.service.GeneralConfigurationService;
import ck.panda.service.GeneralConfigurationServiceImpl;
import ck.panda.service.LoginHistoryService;
import ck.panda.service.LoginHistoryServiceImpl;
import ck.panda.service.RoleService;
import ck.panda.service.RoleServiceImpl;
import ck.panda.service.ThemeSettingService;
import ck.panda.service.UserService;
import ck.panda.service.UserServiceImpl;
import ck.panda.util.TokenDetails;
import ck.panda.util.error.MessageByLocaleService;
import ck.panda.util.infrastructure.externalwebservice.SomeExternalServiceAuthenticator;
import ck.panda.util.infrastructure.security.AuthenticationFilter;
import ck.panda.util.infrastructure.security.DatabaseAuthenticationManager;
import ck.panda.util.infrastructure.security.ExternalServiceAuthenticator;
import ck.panda.util.infrastructure.security.TokenAuthenticationProvider;
import ck.panda.util.infrastructure.security.TokenService;
import ck.panda.util.web.ApiController;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Security configuration.
 */
@Configuration
@EnableScheduling
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /** Root admin role. */
    @Value("${backend.admin.role}")
    private String backendAdminRole;

    /** Database authentication manager reference. */
    @Autowired
    private DatabaseAuthenticationManager databaseAuthenticationManager;

    /** Data source reference. */
    @Autowired
    private DataSource dataSource;

    /** Token details reference. */
    @Autowired
    private TokenDetails userTokenDetails;

    /** Message properties service attribute. */
    @Autowired
    private MessageByLocaleService messageByLocaleService;

    /** Theme Setting service attribute. */
    @Autowired
    private ThemeSettingService themeSettingService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests().antMatchers(actuatorEndpoints()).hasRole(backendAdminRole).anyRequest()
                .authenticated().and().formLogin().loginPage(GenericConstants.LOGIN_URL).permitAll().and().logout()
                .logoutRequestMatcher(new AntPathRequestMatcher(GenericConstants.LOGOUT_URL))
                .logoutSuccessUrl(GenericConstants.LOGIN_OUT_URL)
                .deleteCookies(GenericConstants.COOKIES_NAME).invalidateHttpSession(true).and().anonymous().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedEntryPoint());

        http.addFilterBefore(new AuthenticationFilter(databaseAuthenticationManager, userTokenDetails, messageByLocaleService, themeSettingService),
                BasicAuthenticationFilter.class);
    }

    /**
     * Persistent token service.
     *
     * @return TokenService
     */
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepositoryImpl = new JdbcTokenRepositoryImpl();
        tokenRepositoryImpl.setDataSource(dataSource);
        return tokenRepositoryImpl;
    }

    /**
     * Actuator management end points.
     *
     * @return String array
     */
    private String[] actuatorEndpoints() {
        return new String[] {ApiController.AUTOCONFIG_ENDPOINT, ApiController.BEANS_ENDPOINT,
                ApiController.CONFIGPROPS_ENDPOINT, ApiController.ENV_ENDPOINT, ApiController.MAPPINGS_ENDPOINT,
                ApiController.METRICS_ENDPOINT, ApiController.SHUTDOWN_ENDPOINT };
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(tokenAuthenticationProvider());
    }

    /**
     * Factory method to get token service.
     *
     * @return TokenService
     */
    @Bean
    public TokenService tokenService() {
        return new TokenService();
    }

    /**
     * user service method to get token service.
     *
     * @return TokenService
     */
    @Bean
    public UserService userService() {
        return new UserServiceImpl();
    }

    /**
     * role service method to get token service.
     *
     * @return TokenService
     */
    @Bean
    public RoleService roleService() {
        return new RoleServiceImpl();
    }

    /**
     * Factory method to get token service.
     *
     * @return TokenService
     */
    @Bean
    public LoginHistoryService loginHistoryService() {
        return new LoginHistoryServiceImpl();
    }
    /**
     * Factory method to get token service.
     *
     * @return TokenService
     */
    @Bean
    public GeneralConfigurationService generalConfigurationService() {
        return new GeneralConfigurationServiceImpl();
    }
    /**
     * Factory method to get external service authenticator.
     *
     * @return ExternalServiceAuthenticator
     */
    @Bean
    public ExternalServiceAuthenticator someExternalServiceAuthenticator() {
        return new SomeExternalServiceAuthenticator();
    }

    /**
     * Factory method to get token authentication provider.
     *
     * @return AuthenticationProvider
     */
    @Bean
    public AuthenticationProvider tokenAuthenticationProvider() {
        return new TokenAuthenticationProvider(tokenService(), loginHistoryService(),
                generalConfigurationService(),someExternalServiceAuthenticator(), userService(), roleService());
    }

    /**
     * Unauthorized entry point.
     *
     * @return AuthenticationEntryPoint
     */
    @Bean
    public AuthenticationEntryPoint unauthorizedEntryPoint() {
        return (request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
