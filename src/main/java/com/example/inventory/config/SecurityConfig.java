package com.example.inventory.config;

import com.example.inventory.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // Авторизация и Swagger доступны без токена
                        .requestMatchers(
                                "/api/auth/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Просмотр товаров
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/items",
                                "/api/items/**"
                        ).hasAuthority("ITEM_READ")

                        // Создание товара
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/items",
                                "/api/items/**"
                        ).hasAuthority("ITEM_CREATE")

                        // Изменение товара
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/items",
                                "/api/items/**"
                        ).hasAuthority("ITEM_UPDATE")

                        // Удаление товара
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/items",
                                "/api/items/**"
                        ).hasAuthority("ITEM_DELETE")

                        // Остальные запросы запрещены
                        .anyRequest().denyAll()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
            return configuration.getAuthenticationManager();
    }
}
