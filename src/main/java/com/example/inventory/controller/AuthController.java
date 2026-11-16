package com.example.inventory.controller;

import com.example.inventory.dto.LoginRequest;
import com.example.inventory.dto.LoginResponse;
import com.example.inventory.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        // Проверяем логин и пароль
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Генерируем JWT
        String token = jwtService.generateToken(request.getUsername());

        // Возвращаем токен клиенту
        LoginResponse response = new LoginResponse(token, "Bearer");

        return ResponseEntity.ok(response);
    }
}
