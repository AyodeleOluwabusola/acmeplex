package com.uofc.acmeplex.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web security configuration
 * This class is used to configure the security of the application
 * Determines which requests should be authenticated and which should not
 * Also, it is used to configure CORS
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class WebSecurityConfig {

    private final WebAuthenticationFilter authenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {


        http.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> {
                    authorizationManagerRequestMatcherRegistry.requestMatchers("/actuator/**").permitAll()
                            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                            .requestMatchers("/user/**").permitAll()
                            .requestMatchers("/movie/**").permitAll()
                            .requestMatchers("/ticket/issue").permitAll()
                            .requestMatchers("/ticket/cancel").permitAll()
                            .requestMatchers("/ticket/code").permitAll()
                            .requestMatchers("/theatre/**").permitAll()
                            .requestMatchers("/refund/**").permitAll()
                            .requestMatchers("/payment").permitAll()
                            .requestMatchers(HttpMethod.POST,"/payment/**").permitAll()
                            .requestMatchers("/theatre-seat/**").permitAll()
                            .anyRequest().authenticated();
                })
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedHeaders("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
            }
        };
    }
}
