package com.george.usermanagementapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configures the SpringDoc OpenAPI bean with project metadata.
 *
 * <p>Swagger UI is served at {@code /swagger-ui.html} and the raw OpenAPI
 * JSON spec is available at {@code /api-docs} (both paths are set in
 * {@code application.yml}).
 */
@Configuration
public class OpenApiConfig {

    /**
     * Builds and returns the {@link OpenAPI} descriptor used by SpringDoc to
     * generate Swagger UI and the machine-readable API spec.
     *
     * @return fully populated {@link OpenAPI} instance
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Management API")
                        .description(
                                "A production-ready REST API for managing user accounts, "
                                + "built with Spring Boot 3, Spring Data JPA, and PostgreSQL.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("George")
                                .email("george@example.com"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local development server")));
    }
}
