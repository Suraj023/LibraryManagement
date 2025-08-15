package com.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // disable for APIs (enable later if using forms)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/users/**").authenticated() // Protect these endpoints
                .requestMatchers("/api/books/**").permitAll() // Public for now
            )
            .httpBasic(); // Simple basic auth for now

        return http.build();
    }
}
