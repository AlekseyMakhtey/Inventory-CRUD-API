package com.example.inventory.security;

import com.example.inventory.entity.AppUser;
import com.example.inventory.entity.Permission;
import com.example.inventory.repository.AppUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    public CustomUserDetailsService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found: " + username
                ));

        return User.withUsername(appUser.getUsername())
                .password(appUser.getPassword())
                .authorities(
                    appUser.getPermissions()
                        .stream()
                        .map(Permission::getName)
                        .toArray(String[]::new)
                )
                .build();
    }
}