package com.example.inventory.config;

import com.example.inventory.entity.AppUser;
import com.example.inventory.entity.Permission;
import com.example.inventory.entity.Role;
import com.example.inventory.repository.AppUserRepository;
import com.example.inventory.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Set;

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
            PermissionRepository permissionRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

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

            // Получаем разрешения
            Set<Permission> adminPermissions = Set.of(
                    getPermission(permissionRepository, "ITEM_READ"),
                    getPermission(permissionRepository, "ITEM_CREATE"),
                    getPermission(permissionRepository, "ITEM_UPDATE"),
                    getPermission(permissionRepository, "ITEM_DELETE")
            );

            Set<Permission> viewerPermissions = Set.of(
                    getPermission(permissionRepository, "ITEM_READ")
            );

            // Создаём администратора
            if (userRepository.findByUsername(adminUsername).isEmpty()) {

                AppUser admin = new AppUser();
                admin.setUsername(adminUsername);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setRole(Role.ADMINISTRATOR);
                admin.setPermissions(adminPermissions);

                userRepository.save(admin);

                System.out.println("Administrator created.");
            }

            // Создаём пользователя
            if (userRepository.findByUsername(viewerUsername).isEmpty()) {

                AppUser viewer = new AppUser();
                viewer.setUsername(viewerUsername);
                viewer.setPassword(passwordEncoder.encode(viewerPassword));
                viewer.setRole(Role.VIEWER);
                viewer.setPermissions(viewerPermissions);

                userRepository.save(viewer);

                System.out.println("Viewer created.");
            }
        };
    }

    private Permission getPermission(
            PermissionRepository permissionRepository,
            String name
    ) {
        return permissionRepository.findByName(name)
                .orElseThrow(() -> new IllegalStateException(
                        "Permission not found: " + name
                ));
    }
}
