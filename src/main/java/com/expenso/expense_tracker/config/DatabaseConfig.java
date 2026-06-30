package com.expenso.expense_tracker.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Database Configuration for Render deployment
 * Converts Render's DATABASE_URL format to Spring Boot JDBC format
 */
@Configuration
public class DatabaseConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");
        
        // If DATABASE_URL is not set (local development), use default Spring Boot config
        if (databaseUrl == null || databaseUrl.isEmpty()) {
            return DataSourceBuilder.create().build();
        }

        try {
            // Parse Render's PostgreSQL URL format: postgresql://user:password@host:5432/database
            URI dbUri = new URI(databaseUrl);
            
            String username = dbUri.getUserInfo().split(":")[0];
            String password = dbUri.getUserInfo().split(":")[1];
            String host = dbUri.getHost();
            int port = dbUri.getPort();
            String database = dbUri.getPath().substring(1); // Remove leading slash
            
            // Convert to JDBC format: jdbc:postgresql://host:5432/database
            String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s?sslmode=require", 
                                          host, port, database);

            // Build DataSource with converted URL
            return DataSourceBuilder
                    .create()
                    .url(jdbcUrl)
                    .username(username)
                    .password(password)
                    .driverClassName("org.postgresql.Driver")
                    .build();
                    
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid DATABASE_URL format", e);
        }
    }
}
