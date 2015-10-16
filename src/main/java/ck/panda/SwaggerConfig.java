package ck.panda;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Import;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger configurations will be added here.
 */
@Configuration
@ComponentScan(basePackages = "ck.panda.web.resource")
@EnableSwagger2
@PropertySource("classpath:swagger.properties")
@Import(SwaggerUiConfig.class)
public class SwaggerConfig {
    /**
     * Application information.
     * @return ApiInfor entity
     */
    @Bean
    ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfo(
        "Online-Exam API",
        "Move your app forward with the Online-Exam API",
        "1.0.0",
        "",
        "Chakravarthi Bharathi",
        "N/A",
        "N/A");
        return apiInfo;
    }

    /**
     * Custom implementation configuration.
     * @return Docket entity
     */
    @Bean
    public Docket customImplementation() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo());
    }

}
