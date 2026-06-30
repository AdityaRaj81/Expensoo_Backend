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
        if (databaseUrl != null && !databaseUrl.trim().isEmpty()) {
            try {
                System.out.println("==============================================");
                System.out.println("DATABASE_URL FOUND in environment");
                System.out.println("Raw value length: " + databaseUrl.length());
                System.out.println("Starts with 'postgresql://': " + databaseUrl.startsWith("postgresql://"));
                System.out.println("==============================================");
                
                // Parse Render's PostgreSQL URL format: postgresql://user:password@host:5432/database
                URI dbUri = new URI(databaseUrl.trim());
                
                if (dbUri.getUserInfo() == null || !dbUri.getUserInfo().contains(":")) {
                    throw new IllegalArgumentException("DATABASE_URL missing credentials. Expected format: postgresql://user:password@host:5432/database");
                }
                
                String[] credentials = dbUri.getUserInfo().split(":", 2);
                String username = credentials[0];
                String password = credentials[1];
                String host = dbUri.getHost();
                int port = dbUri.getPort() == -1 ? 5432 : dbUri.getPort();
                String database = dbUri.getPath() != null && dbUri.getPath().length() > 1 
                                  ? dbUri.getPath().substring(1) 
                                  : "postgres";
                
                // Convert to JDBC format: jdbc:postgresql://host:5432/database
                String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s?sslmode=require", 
                                              host, port, database);
                
                config.setJdbcUrl(jdbcUrl);
                config.setUsername(username);
                config.setPassword(password);
                
                System.out.println("✅ Database URL parsed successfully!");
                System.out.println("Host: " + host);
                System.out.println("Port: " + port);
                System.out.println("Database: " + database);
                System.out.println("Username: " + username);
                System.out.println("JDBC URL: " + jdbcUrl.replaceAll("\\?.*", ""));
                System.out.println("==============================================");
                
            } catch (URISyntaxException | ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
                System.err.println("❌ ERROR: Invalid DATABASE_URL format");
                System.err.println("Received: " + databaseUrl);
                System.err.println("Expected format: postgresql://user:password@host:5432/database");
                System.err.println("Error: " + e.getMessage());
                throw new RuntimeException("Invalid DATABASE_URL format: " + e.getMessage(), e);
            }
        } else {
            // Local development - use application.properties values
            System.out.println("==============================================");
            System.out.println("⚠️  DATABASE_URL NOT FOUND in environment");
            System.out.println("Using local database configuration from application.properties");
            System.out.println("==============================================");
            
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
