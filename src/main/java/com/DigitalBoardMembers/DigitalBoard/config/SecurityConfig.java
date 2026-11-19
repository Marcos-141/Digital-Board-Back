package com.DigitalBoardMembers.DigitalBoard.config;

import com.DigitalBoardMembers.DigitalBoard.controllers.ApiConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@EnableWebSecurity
@org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Habilitar CORS y deshabilitar CSRF para APIs REST stateless y consola H2
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Gestion de sesiones sin estado (Stateless)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                // 3. Reglas de autorizacion de endpoints
                .authorizeHttpRequests(auth -> auth
                        // Documentacion Swagger y OpenAPI
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml"
                        ).permitAll()
                        // Consola H2
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers(ApiConstants.AUTH + "/**").permitAll()
                        // Endpoints de la API REST
                        .requestMatchers("/api/v1/**").permitAll()
                        // Cualquier otra peticion
                        .anyRequest().authenticated()
                )

                // 4. Permitir frames del mismo origen (necesario para visualizar H2 Console)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                );

        return http.build();
    }
}
