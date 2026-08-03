package com.expenso.expense_tracker.security;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security Configuration
 *
 * Configures Spring Security for JWT Authentication.
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

        /**
         * Security Filter Chain
         */
        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http) throws Exception {

                http

                                .cors(Customizer.withDefaults())

                                .csrf(csrf -> csrf.disable())

                                .formLogin(form -> form.disable())

                                .httpBasic(Customizer.withDefaults())

                                .sessionManagement(session ->

                                session.sessionCreationPolicy(
                                                SessionCreationPolicy.STATELESS))

                                /*
                                 * Exception Handler
                                 */
                                .exceptionHandling(exception ->

                                exception.authenticationEntryPoint(
                                                jwtAuthenticationEntryPoint))

                                /*
                                 * Authorization Rules
                                 */
                                .authorizeHttpRequests(auth -> auth

                                                /*
                                                 * Public APIs
                                                 */
                                                .requestMatchers(

                                                                "/",

                                                                "/api/auth/**",

                                                                "/api/health/**"

                                                ).permitAll()

                                                /*
                                                 * Admin APIs
                                                 */
                                                .requestMatchers(

                                                                "/api/admin/**"

                                                ).hasRole("ADMIN")

                                                /*
                                                 * Everything else requires authentication
                                                 */
                                                .anyRequest()

                                                .authenticated()

                                )

                                /*
                                 * JWT Filter
                                 */
                                .addFilterBefore(

                                                jwtAuthenticationFilter,

                                                UsernamePasswordAuthenticationFilter.class

                                );

                return http.build();

        }

        /**
         * Authentication Manager
         */
        @Bean
        public AuthenticationManager authenticationManager(
                        AuthenticationConfiguration configuration) throws Exception {

                return configuration.getAuthenticationManager();

        }

}