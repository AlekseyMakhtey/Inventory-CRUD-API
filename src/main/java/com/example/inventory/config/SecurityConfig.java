
package com.example.inventory.config;

import com.example.inventory.security.CustomUserDetailsService;
import com.example.inventory.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
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
                // Отключаем CSRF, так как используем JWT
                .csrf(csrf -> csrf.disable())

                // Не используем HTTP-сессии
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Настраиваем права доступа
                .authorizeHttpRequests(auth -> auth

                        // Авторизация и Swagger доступны без токена
                        .requestMatchers(
                                "/api/auth/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Просмотр товаров доступен обеим ролям
                        .requestMatchers(HttpMethod.GET,
                                "/api/items",
                                "/api/items/**"
                        ).hasAnyRole("ADMINISTRATOR", "VIEWER")

                        // Создание, изменение и удаление — только администратор
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/items",
                                "/api/items/**"
                        ).hasRole("ADMINISTRATOR")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/items",
                                "/api/items/**"
                        ).hasRole("ADMINISTRATOR")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/items",
                                "/api/items/**"
                        ).hasRole("ADMINISTRATOR")

                        // Все остальные запросы запрещены
                        .anyRequest().denyAll()
                )

                // Подключаем наш JWT-фильтр
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    // Создаём AuthenticationManager для будущей авторизации
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
