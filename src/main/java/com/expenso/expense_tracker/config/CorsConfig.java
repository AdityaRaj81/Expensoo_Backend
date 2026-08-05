package com.expenso.expense_tracker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins=http://localhost:5173,https://expensoo-raj.vercel.app}")
    private String allowedOrigins;

    @Bean
    public WebMvcConfigurer corsConfigurer() {

        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {

                registry.addMapping("/**")

                        .allowedOrigins(parseOrigins())

                        .allowedMethods(
                                "GET",
                                "POST",
                                "PUT",
                                "PATCH",
                                "DELETE",
                                "OPTIONS")

                        .allowedHeaders("*")

                        .exposedHeaders(
                                "Authorization",
                                "Content-Disposition")

                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }

    private String[] parseOrigins() {
        return allowedOrigins.split("\\s*,\\s*");
    }
}