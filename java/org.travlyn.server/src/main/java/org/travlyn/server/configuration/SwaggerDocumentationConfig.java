package org.travlyn.server.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerDocumentationConfig {

    ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Travlyn API")
                .description("Travlyn is an intelligent travel and city guide that provides interest-based trips in cities and countries. Depending on available " +
                        "time, interests, budget and many other parameters, Travlyn creates personalized routes with additional information about the locations " +
                        "and the sights. ")
                .license("BSD-3-Clause")
                .licenseUrl("https://github.com/raphaelmue/travlyn/blob/master/LICENSE")
                .termsOfServiceUrl("")
                .version("1.0.0")
                .contact(new Contact("", "", "raphael@muesseler.de"))
                .build();
    }

    @Bean
    public Docket customImplementation() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.travlyn.server.api"))
                .build()
                .directModelSubstitute(org.threeten.bp.LocalDate.class, java.sql.Date.class)
                .directModelSubstitute(org.threeten.bp.OffsetDateTime.class, java.util.Date.class)
                .apiInfo(apiInfo());
    }

}
