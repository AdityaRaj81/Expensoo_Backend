package com.expenso.expense_tracker.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT Authentication Filter
 *
 * Executes once per request.
 *
 * Extracts JWT Token
 * Validates JWT
 * Loads User
 * Sets Authentication Context
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Filter Request
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader = request.getHeader(
                SecurityConstants.AUTHORIZATION_HEADER
        );

        if (authorizationHeader == null
                || !authorizationHeader.startsWith(SecurityConstants.TOKEN_PREFIX)) {

            filterChain.doFilter(request, response);

            return;

        }

        try {

            String token = authorizationHeader.substring(
                    SecurityConstants.TOKEN_PREFIX.length()
            );

            String userId = jwtService.extractUserId(token).toString();

            if (SecurityContextHolder.getContext().getAuthentication() == null
                    && jwtService.isTokenValid(token)) {

                UserDetails userDetails = customUserDetailsService
                        .loadUserByUsername(
                                ((UserPrincipal) customUserDetailsService
                                        .loadUserByUsername(
                                                jwtService.extractUsername(token)
                                        )).getUsername()
                        );

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);

            }

        } catch (Exception exception) {

            SecurityContextHolder.clearContext();

        }

        filterChain.doFilter(request, response);

    }

}