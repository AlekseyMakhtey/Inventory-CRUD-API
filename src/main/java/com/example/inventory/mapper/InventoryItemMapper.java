package com.example.inventory.mapper;

import com.example.inventory.dto.InventoryItemRequest;
import com.example.inventory.dto.InventoryItemResponse;
import com.example.inventory.entity.InventoryItem;

public class InventoryItemMapper {

    // Преобразование Request DTO в Entity
    public InventoryItem toEntity(InventoryItemRequest request) {
        InventoryItem item = new InventoryItem();

        item.setName(request.getName());
        item.setSku(request.getSku());
        item.setQuantity(request.getQuantity());
        item.setPrice(request.getPrice());
        item.setCategory(request.getCategory());
        item.setLocation(request.getLocation());
        item.setDescription(request.getDescription());

        return item;
    }

    // Преобразование Entity в Response DTO
    public InventoryItemResponse toResponse(InventoryItem item) {
        return new InventoryItemResponse(
                item.getId(),
                item.getName(),
                item.getSku(),
                item.getQuantity(),
                item.getPrice(),
                item.getCategory(),
                item.getLocation(),
                item.getDescription()
        );
    }
}