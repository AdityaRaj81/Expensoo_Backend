package com.expenso.expense_tracker.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Jackson Configuration
 *
 * Centralizes JSON serialization and deserialization settings
 * for the entire application.
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {

        ObjectMapper mapper = new ObjectMapper();

        /*
         * Support Java 8+ Date/Time API
         * (LocalDate, LocalDateTime, etc.)
         */
        mapper.registerModule(new JavaTimeModule());

        /*
         * Serialize dates as ISO-8601 instead of timestamps.
         */
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        /*
         * Ignore unknown JSON properties instead of failing.
         */
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        /*
         * Do not fail on empty objects.
         */
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        /*
         * Exclude null fields from API responses.
         */
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return mapper;
    }
}