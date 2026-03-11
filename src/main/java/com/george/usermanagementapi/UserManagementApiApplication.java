package com.george.usermanagementapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the User Management REST API.
 *
 * <p>Bootstraps the Spring Boot application context and starts the embedded Tomcat server.
 */
@SpringBootApplication
public class UserManagementApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserManagementApiApplication.class, args);
    }
}
