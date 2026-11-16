package com.example.inventory.config;

import com.example.inventory.entity.AppUser;
import com.example.inventory.entity.Role;
import com.example.inventory.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Value("${app.bootstrap.admin.username:}")
    private String adminUsername;

    @Value("${app.bootstrap.admin.password:}")
    private String adminPassword;

    @Value("${app.bootstrap.viewer.username:}")
    private String viewerUsername;

    @Value("${app.bootstrap.viewer.password:}")
    private String viewerPassword;

    @Bean
    public CommandLineRunner initializeUsers(
            AppUserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            // Если учётные данные не заданы, пропускаем создание пользователей.
            boolean credentialsMissing =
                    adminUsername.isBlank()
                    || adminPassword.isBlank()
                    || viewerUsername.isBlank()
                    || viewerPassword.isBlank();

            if (credentialsMissing) {
                System.out.println(
                        "Bootstrap users were not created: credentials are missing."
                );
                return;
            }

            // Создаём администратора, если его ещё нет.
            if (userRepository.findByUsername(adminUsername).isEmpty()) {

                AppUser admin = new AppUser();
                admin.setUsername(adminUsername);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setRole(Role.ADMINISTRATOR);

                userRepository.save(admin);

                System.out.println("Administrator created.");
            }

            // Создаём пользователя с правами только на просмотр.
            if (userRepository.findByUsername(viewerUsername).isEmpty()) {

                AppUser viewer = new AppUser();
                viewer.setUsername(viewerUsername);
                viewer.setPassword(passwordEncoder.encode(viewerPassword));
                viewer.setRole(Role.VIEWER);

                userRepository.save(viewer);

                System.out.println("Viewer created.");
            }
        };
    }
}
