package com.expenso.expense_tracker.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${spring.datasource.url:}")
    private String defaultUrl;

    @Value("${spring.datasource.username:}")
    private String defaultUsername;

    @Value("${spring.datasource.password:}")
    private String defaultPassword;

    @Bean
    @Primary
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");
        
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.postgresql.Driver");
        
        // If DATABASE_URL is set (Render deployment), parse and use it
        if (databaseUrl != null && !databaseUrl.isEmpty()) {
            try {
                System.out.println("Using DATABASE_URL from environment");
                
                // Parse Render's PostgreSQL URL format: postgresql://user:password@host:5432/database
                URI dbUri = new URI(databaseUrl);
                
                String username = dbUri.getUserInfo().split(":")[0];
                String password = dbUri.getUserInfo().split(":")[1];
                String host = dbUri.getHost();
                int port = dbUri.getPort() == -1 ? 5432 : dbUri.getPort();
                String database = dbUri.getPath().substring(1); // Remove leading slash
                
                // Convert to JDBC format: jdbc:postgresql://host:5432/database
                String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s?sslmode=require", 
                                              host, port, database);
                
                config.setJdbcUrl(jdbcUrl);
                config.setUsername(username);
                config.setPassword(password);
                
                System.out.println("Database URL parsed successfully: " + jdbcUrl.replaceAll("\\?.*", ""));
                
            } catch (URISyntaxException | ArrayIndexOutOfBoundsException e) {
                throw new RuntimeException("Invalid DATABASE_URL format: " + e.getMessage(), e);
            }
        } else {
            // Local development - use application.properties values
            System.out.println("Using local database configuration from application.properties");
            
            if (defaultUrl == null || defaultUrl.isEmpty()) {
                throw new RuntimeException("No database configuration found. Set DATABASE_URL environment variable or configure spring.datasource.url");
            }
            
            config.setJdbcUrl(defaultUrl);
            config.setUsername(defaultUsername);
            config.setPassword(defaultPassword);
        }
        
        // Connection pool settings
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(300000);
        config.setMaxLifetime(600000);
        config.setValidationTimeout(5000);
        config.setLeakDetectionThreshold(60000);
        
        return new HikariDataSource(config);
    }
}
