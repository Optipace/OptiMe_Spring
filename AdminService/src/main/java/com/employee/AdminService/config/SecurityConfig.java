package com.employee.AdminService.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Slf4j
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/admin/internal/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-resources/**", "/webjars/**", "/swagger-ui/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll() // Open all auth endpoints
                        .anyRequest().authenticated()
                );
        log.info("Admin service security filter chain");
        return http.build();
    }
}

