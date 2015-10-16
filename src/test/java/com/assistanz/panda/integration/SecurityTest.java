package com.assistanz.panda.integration;


import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.ValidatableResponse;

import ck.panda.ApplicationConfig;
import ck.panda.util.infrastructure.AuthenticatedExternalWebService;
import ck.panda.util.infrastructure.ServiceGateway;
import ck.panda.util.infrastructure.security.ExternalServiceAuthenticator;
import ck.panda.util.web.ApiController;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;

/**
 * Security test class.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ApplicationConfig.class, SecurityTest.SecurityTestConfig.class})
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class SecurityTest {

    /** Username header attribute. */
    private static final String X_AUTH_USERNAME = "X-Auth-Username";

    /** Password header attribute. */
    private static final String X_AUTH_PASSWORD = "X-Auth-Password";

    /** Token header attribute. */
    private static final String X_AUTH_TOKEN = "X-Auth-Token";

    /** Port attribute. */
    @Value("${local.server.port}")
    private int port;

    /** Keystore file attribute. */
    @Value("${keystore.file}")
    private String keystoreFile;

    /** Keystore password attribute. */
    @Value("${keystore.pass}")
    private String keystorePass;

    /** Mocked external authenticator attribute. */
    @Autowired
    private ExternalServiceAuthenticator mockedExternalServiceAuthenticator;

    /** Service gateway attribute. */
    @Autowired
    private ServiceGateway mockedServiceGateway;

    /** Security test config inner class. */
    @Configuration
    public static class SecurityTestConfig {

        /** External service authenticator.
         *
         * @return ExternalServiceAuthenticator
         */
        @Bean
        public ExternalServiceAuthenticator someExternalServiceAuthenticator() {
            return mock(ExternalServiceAuthenticator.class);
        }

        /**
         * Service gateway.
         * @return ServiceGateway
         */
        @Bean
        @Primary
        public ServiceGateway serviceGateway() {
            return mock(ServiceGateway.class);
        }
    }

    /**
     * Setup tests.
     */
    @Before
    public void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.keystore(keystoreFile, keystorePass);
        RestAssured.port = port;
        Mockito.reset(mockedExternalServiceAuthenticator, mockedServiceGateway);
    }

    /**
     * Health endpoint test.
     */
    @Test
    public void healthEndpointIsAvailableToEveryone() {
        when().get("/health").
                then().statusCode(HttpStatus.OK.value()).body("status", equalTo("UP"));
    }

    /**
     * Metrics endpoint test.
     */
    @Test
    public void metricsEndpointWithoutBackendAdminCredentialsReturnsUnauthorized() {
        when().get("/metrics").
                then().statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    /**
     *  metricsEndpointWithInvalidBackendAdminCredentialsReturnsUnauthorized.
     */
    @Test
    public void metricsEndpointWithInvalidBackendAdminCredentialsReturnsUnauthorized() {
        String username = "test_user_2";
        String password = "InvalidPassword";
        given().header(X_AUTH_USERNAME, username).header(X_AUTH_PASSWORD, password).
                when().get("/metrics").
                then().statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    /**
     * metricsEndpointWithCorrectBackendAdminCredentialsReturnsOk.
     */
    @Test
    public void metricsEndpointWithCorrectBackendAdminCredentialsReturnsOk() {
        String username = "admin";
        String password = "admin";
        given().header(X_AUTH_USERNAME, username).header(X_AUTH_PASSWORD, password).
                when().get("/metrics").
                then().statusCode(HttpStatus.OK.value());
    }

    /**
     * authenticateWithoutPasswordReturnsUnauthorized.
     */
    @Test
    public void authenticateWithoutPasswordReturnsUnauthorized() {
        given().header(X_AUTH_USERNAME, "SomeUser").
                when().post(ApiController.AUTHENTICATE_URL).
                then().statusCode(HttpStatus.UNAUTHORIZED.value());

        BDDMockito.verifyNoMoreInteractions(mockedExternalServiceAuthenticator);
    }

    /**
     * authenticateWithoutUsernameReturnsUnauthorized.
     */
    @Test
    public void authenticateWithoutUsernameReturnsUnauthorized() {
        given().header(X_AUTH_PASSWORD, "SomePassword").
                when().post(ApiController.AUTHENTICATE_URL).
                then().statusCode(HttpStatus.UNAUTHORIZED.value());

        BDDMockito.verifyNoMoreInteractions(mockedExternalServiceAuthenticator);
    }

    /**
     * authenticateWithoutUsernameAndPasswordReturnsUnauthorized.
     */
    @Test
    public void authenticateWithoutUsernameAndPasswordReturnsUnauthorized() {
        when().post(ApiController.AUTHENTICATE_URL).
                then().statusCode(HttpStatus.UNAUTHORIZED.value());

        BDDMockito.verifyNoMoreInteractions(mockedExternalServiceAuthenticator);
    }

    /**
     * authenticateWithValidUsernameAndPasswordReturnsToken.
     */
    @Test
    public void authenticateWithValidUsernameAndPasswordReturnsToken() {
        authenticateByUsernameAndPasswordAndGetToken();
    }

    /**
     * authenticateWithInvalidUsernameOrPasswordReturnsUnauthorized.
     */
    @Test
    public void authenticateWithInvalidUsernameOrPasswordReturnsUnauthorized() {
        String username = "test_user_2";
        String password = "InvalidPassword";

        BDDMockito.when(mockedExternalServiceAuthenticator.authenticate(anyString(), anyString())).
                thenThrow(new BadCredentialsException("Invalid Credentials"));

        given().header(X_AUTH_USERNAME, username).header(X_AUTH_PASSWORD, password).
                when().post(ApiController.AUTHENTICATE_URL).
                then().statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    /**
     * gettingStuffWithoutTokenReturnsUnauthorized.
     */
    @Test
    public void gettingStuffWithoutTokenReturnsUnauthorized() {
        when().get(ApiController.STUFF_URL).
                then().statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    /**
     * gettingStuffWithInvalidTokenReturnsUnathorized.
     */
    @Test
    public void gettingStuffWithInvalidTokenReturnsUnathorized() {
        given().header(X_AUTH_TOKEN, "InvalidToken").
                when().get(ApiController.STUFF_URL).
                then().statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    /**
     * gettingStuffWithValidTokenReturnsData.
     */
    @Test
    public void gettingStuffWithValidTokenReturnsData() {
        String generatedToken = authenticateByUsernameAndPasswordAndGetToken();

        given().header(X_AUTH_TOKEN, generatedToken).
                when().get(ApiController.STUFF_URL).
                then().statusCode(HttpStatus.OK.value());
    }

    /**
     * authenticateByUsernameAndPasswordAndGetToken.
     * @return token
     */
    private String authenticateByUsernameAndPasswordAndGetToken() {
        String username = "test_user_2";
        String password = "ValidPassword";

        AuthenticatedExternalWebService authenticationWithToken = new AuthenticatedExternalWebService(username, null,
                AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_DOMAIN_USER"));
        BDDMockito.when(mockedExternalServiceAuthenticator.authenticate(eq(username), eq(password))).
                thenReturn(authenticationWithToken);

        ValidatableResponse validatableResponse = given().header(X_AUTH_USERNAME, username).
                header(X_AUTH_PASSWORD, password).
                when().post(ApiController.AUTHENTICATE_URL).
                then().statusCode(HttpStatus.OK.value());
        String generatedToken = authenticationWithToken.getToken();
        validatableResponse.body("token", equalTo(generatedToken));

        return generatedToken;
    }
}
