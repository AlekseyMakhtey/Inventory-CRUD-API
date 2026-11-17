package com.example.inventory.controller;

import com.example.inventory.dto.InventoryItemRequest;
import com.example.inventory.dto.InventoryItemResponse;
import com.example.inventory.entity.Category;
import com.example.inventory.service.InventoryItemService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/items")
public class InventoryItemController {

    private final InventoryItemService inventoryItemService;

    private static final java.util.Set<String> ALLOWED_SORT_FIELDS = java.util.Set.of(
        "id",
        "name",
        "sku",
        "quantity",
        "price",
        "category",
        "location"
    );

    public InventoryItemController(InventoryItemService inventoryItemService) {
        this.inventoryItemService = inventoryItemService;
    }

    // Получить товары с фильтрацией, пагинацией и сортировкой
    @GetMapping
    public ResponseEntity<Page<InventoryItemResponse>> getAllItems(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String sku,
            @RequestParam(required = false) Category category,
            @RequestParam(defaultValue = "0", name = "_page") int page,
            @RequestParam(defaultValue = "10", name = "_size") int size,
            @RequestParam(defaultValue = "id", name = "_sort") String sort
    ) {
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page must be >= 0 and size must be between 1 and 100"
            );
        }
        if (!ALLOWED_SORT_FIELDS.contains(sort)) {
            throw new IllegalArgumentException(
                    "Invalid sort field: " + sort
            );
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sort).ascending()
        );

        return ResponseEntity.ok(
                inventoryItemService.searchItems(name, sku, category, pageable)
        );
    }

    // Получить товар по ID
    @GetMapping("/{id}")
    public ResponseEntity<InventoryItemResponse> getItemById(
            @PathVariable Long id) {
        return ResponseEntity.ok(inventoryItemService.getItemById(id));
    }

    // Создать товар
    @PostMapping
    public ResponseEntity<InventoryItemResponse> createItem(
            @Valid @RequestBody InventoryItemRequest request) {

        InventoryItemResponse response = inventoryItemService.createItem(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Обновить товар
    @PutMapping("/{id}")
    public ResponseEntity<InventoryItemResponse> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody InventoryItemRequest request) {

        return ResponseEntity.ok(inventoryItemService.updateItem(id, request));
    }

    // Удалить товар
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        inventoryItemService.deleteItem(id);

        return ResponseEntity.noContent().build();
    }
}
