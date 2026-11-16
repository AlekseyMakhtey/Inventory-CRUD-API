package com.example.inventory.service;

import com.example.inventory.dto.InventoryItemRequest;
import com.example.inventory.dto.InventoryItemResponse;
import com.example.inventory.entity.Category;
import com.example.inventory.entity.InventoryItem;
import com.example.inventory.mapper.InventoryItemMapper;
import com.example.inventory.repository.InventoryItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class InventoryItemService {

    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryItemMapper inventoryItemMapper;

    public InventoryItemService(InventoryItemRepository inventoryItemRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.inventoryItemMapper = new InventoryItemMapper();
    }

    public List<InventoryItemResponse> getAllItems() {
        return inventoryItemRepository.findAll()
                .stream()
                .map(inventoryItemMapper::toResponse)
                .toList();
    }

    public Page<InventoryItemResponse> searchItems(
            String name,
            String sku,
            Category category,
            Pageable pageable
    ) {
        return inventoryItemRepository.searchItems(
                        normalizeFilter(name),
                        normalizeFilter(sku),
                        category,
                        pageable
                )
                .map(inventoryItemMapper::toResponse);
    }

    private String normalizeFilter(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    public InventoryItemResponse getItemById(Long id) {
        InventoryItem item = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Inventory item not found id: " + id
                ));

        return inventoryItemMapper.toResponse(item);
    }

    public InventoryItemResponse createItem(InventoryItemRequest request) {
        InventoryItem item = inventoryItemMapper.toEntity(request);

        InventoryItem savedItem = inventoryItemRepository.save(item);

        return inventoryItemMapper.toResponse(savedItem);
    }

    public InventoryItemResponse updateItem(Long id, InventoryItemRequest request) {
        InventoryItem existingItem = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Inventory item not found id: " + id
                ));

        InventoryItem updatedItem = inventoryItemMapper.toEntity(request);

        existingItem.setName(updatedItem.getName());
        existingItem.setSku(updatedItem.getSku());
        existingItem.setQuantity(updatedItem.getQuantity());
        existingItem.setPrice(updatedItem.getPrice());
        existingItem.setCategory(updatedItem.getCategory());
        existingItem.setLocation(updatedItem.getLocation());
        existingItem.setDescription(updatedItem.getDescription());

        InventoryItem savedItem = inventoryItemRepository.save(existingItem);

        return inventoryItemMapper.toResponse(savedItem);
    }

    public void deleteItem(Long id) {
        InventoryItem item = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Inventory item not found id: " + id
                ));

        inventoryItemRepository.delete(item);
    }
}
