package com.expenso.expense_tracker.security;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.expenso.expense_tracker.model.User;

/**
 * User Principal
 * Custom implementation of Spring Security UserDetails.
 * Wraps the User entity and provides authentication information.
 */
public class UserPrincipal implements UserDetails {
    /**
     * -- GETTER --
     *  User ID
     */
    @Getter
    private final UUID id;
    /**
     * -- GETTER --
     *  User Name
     */
    @Getter
    private final String name;
    /**
     * -- GETTER --
     *  User Email
     */
    @Getter
    private final String email;
    private final String password;
    private final boolean active;
    private final Collection<? extends GrantedAuthority> authorities;
    /**
     * Constructor
     */
    public UserPrincipal(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.active = user.isActive();
        this.authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
    }

    /**
     * Granted Authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return authorities;

    }

    /**
     * Password
     */
    @Override
    public String getPassword() {

        return password;

    }

    /**
     * Username
     * Email is used as username.
     */
    @Override
    public String getUsername() {

        return email;

    }

    /**
     * Account Expired
     */
    @Override
    public boolean isAccountNonExpired() {

        return true;

    }

    /**
     * Account Locked
     */
    @Override
    public boolean isAccountNonLocked() {

        return active;

    }

    /**
     * Credentials Expired
     */
    @Override
    public boolean isCredentialsNonExpired() {

        return true;

    }

    /**
     * Account Enabled
     */
    @Override
    public boolean isEnabled() {

        return active;

    }
}