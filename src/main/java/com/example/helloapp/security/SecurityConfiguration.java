package com.example.helloapp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/", "/welcome.html", "/login", "/error").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(login -> login
                        .loginPage("/welcome.html")
                        .defaultSuccessUrl("/landing.html", true)
                        .failureUrl("/welcome.html?error"))
                .logout(logout -> logout.logoutSuccessUrl("/welcome.html?logout"))
                .build();
    }
}
