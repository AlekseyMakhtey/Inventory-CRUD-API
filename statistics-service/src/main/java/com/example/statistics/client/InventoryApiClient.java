package com.example.statistics.client;

import com.example.statistics.dto.InventoryPageResponse;
import com.example.statistics.dto.LoginRequest;
import com.example.statistics.dto.LoginResponse;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class InventoryApiClient {

    private final RestClient restClient;
    private final String username;
    private final String password;

    public InventoryApiClient(
            @Value("${inventory.api.base-url}") String baseUrl,
            @Value("${inventory.api.username}") String username,
            @Value("${inventory.api.password}") String password
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();

        this.username = username;
        this.password = password;
    }

    @Retry(name = "inventoryApi")
    public String login() {
        LoginResponse response = restClient.post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new LoginRequest(username, password))
                .retrieve()
                .body(LoginResponse.class);

        if (response == null || response.token() == null) {
            throw new IllegalStateException("Failed to obtain JWT token");
        }

        return response.token();
    }

    @Retry(name = "inventoryApi")
    public InventoryPageResponse getItems(String token, int page) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/items")
                        .queryParam("_page", page)
                        .queryParam("_size", 100)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .body(InventoryPageResponse.class);
    }
}
