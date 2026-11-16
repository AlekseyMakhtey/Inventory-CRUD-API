package com.example.inventory.service;

import com.example.inventory.dto.InventoryItemRequest;
import com.example.inventory.repository.InventoryItemRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.inventory.entity.Category;
import com.example.inventory.entity.InventoryItem;
import com.example.inventory.dto.InventoryItemResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryItemServiceTest {

    @Mock
    private InventoryItemRepository inventoryItemRepository;

    @InjectMocks
    private InventoryItemService inventoryItemService;

    @Test
    void getAllItems_shouldReturnAllItems() {

        InventoryItem item = new InventoryItem();
        item.setId(1L);
        item.setName("Monitor");
        item.setSku("MONITOR-001");
        item.setQuantity(10);
        item.setPrice(new BigDecimal("300.00"));
        item.setCategory(Category.ELECTRONICS);
        item.setLocation("Warehouse A");
        item.setDescription("27 inch monitor");

        when(inventoryItemRepository.findAll())
                .thenReturn(List.of(item));

        List<InventoryItemResponse> result =
                inventoryItemService.getAllItems();

        assertEquals(1, result.size());
        assertEquals("Monitor", result.get(0).getName());
        assertEquals("MONITOR-001", result.get(0).getSku());

        verify(inventoryItemRepository, times(1)).findAll();
    }

    @Test
    void getAllItems_shouldReturnEmptyList() {
        when(inventoryItemRepository.findAll())
                .thenReturn(List.of());

        List<InventoryItemResponse> result =
                inventoryItemService.getAllItems();

        assertTrue(result.isEmpty());

        verify(inventoryItemRepository, times(1)).findAll();
    }
    @Test
    void getItemById_shouldReturnItem_whenItemExists() {
        InventoryItem item = new InventoryItem();
        item.setId(1L);
        item.setName("Monitor");
        item.setSku("MONITOR-001");
        item.setQuantity(10);
        item.setPrice(new BigDecimal("300.00"));
        item.setCategory(Category.ELECTRONICS);
        item.setLocation("Warehouse A");
        item.setDescription("27 inch monitor");

        when(inventoryItemRepository.findById(1L))
                .thenReturn(java.util.Optional.of(item));

        InventoryItemResponse result =
                inventoryItemService.getItemById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Monitor", result.getName());
        assertEquals("MONITOR-001", result.getSku());

        verify(inventoryItemRepository, times(1)).findById(1L);
    }

    @Test
    void getItemById_shouldThrowException_whenItemDoesNotExist() {
        when(inventoryItemRepository.findById(999L))
                .thenReturn(java.util.Optional.empty());

        org.springframework.web.server.ResponseStatusException exception =
                assertThrows(
                        org.springframework.web.server.ResponseStatusException.class,
                        () -> inventoryItemService.getItemById(999L)
                );

        assertEquals(
                org.springframework.http.HttpStatus.NOT_FOUND,
                exception.getStatusCode()
        );

        verify(inventoryItemRepository, times(1)).findById(999L);
    }

    @Test
    void createItem_shouldSaveAndReturnItem() {
        InventoryItemRequest request = new InventoryItemRequest();
        request.setName("Keyboard");
        request.setSku("KEYBOARD-001");
        request.setQuantity(20);
        request.setPrice(new BigDecimal("75.00"));
        request.setCategory(Category.ELECTRONICS);
        request.setLocation("Warehouse A");
        request.setDescription("Mechanical keyboard");

        when(inventoryItemRepository.save(any(InventoryItem.class)))
                .thenAnswer(invocation -> {
                    InventoryItem savedItem = invocation.getArgument(0);
                    savedItem.setId(10L);
                    return savedItem;
                });

        InventoryItemResponse result =
                inventoryItemService.createItem(request);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Keyboard", result.getName());
        assertEquals("KEYBOARD-001", result.getSku());
        assertEquals(20, result.getQuantity());

        verify(inventoryItemRepository, times(1))
                .save(any(InventoryItem.class));
    }

    @Test
    void updateItem_shouldUpdateAndReturnItem() {
        InventoryItem existingItem = new InventoryItem();
        existingItem.setId(1L);
        existingItem.setName("Old Keyboard");
        existingItem.setSku("KEYBOARD-OLD");
        existingItem.setQuantity(10);
        existingItem.setPrice(new BigDecimal("50.00"));
        existingItem.setCategory(Category.ELECTRONICS);
        existingItem.setLocation("Warehouse A");
        existingItem.setDescription("Old keyboard");

        InventoryItemRequest request = new InventoryItemRequest();
        request.setName("New Keyboard");
        request.setSku("KEYBOARD-NEW");
        request.setQuantity(25);
        request.setPrice(new BigDecimal("85.00"));
        request.setCategory(Category.ELECTRONICS);
        request.setLocation("Warehouse B");
        request.setDescription("Updated mechanical keyboard");

        when(inventoryItemRepository.findById(1L))
                .thenReturn(java.util.Optional.of(existingItem));

        when(inventoryItemRepository.save(any(InventoryItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        InventoryItemResponse result =
                inventoryItemService.updateItem(1L, request);

        assertEquals(1L, result.getId());
        assertEquals("New Keyboard", result.getName());
        assertEquals("KEYBOARD-NEW", result.getSku());
        assertEquals(25, result.getQuantity());
        assertEquals(new BigDecimal("85.00"), result.getPrice());

        verify(inventoryItemRepository, times(1)).findById(1L);
        verify(inventoryItemRepository, times(1)).save(existingItem);
    }

    @Test
    void deleteItem_shouldDeleteItem_whenItemExists() {
        InventoryItem item = new InventoryItem();
        item.setId(1L);
        item.setName("Monitor");
        item.setSku("MONITOR-001");
        item.setQuantity(10);
        item.setPrice(new BigDecimal("300.00"));
        item.setCategory(Category.ELECTRONICS);

        when(inventoryItemRepository.findById(1L))
                .thenReturn(java.util.Optional.of(item));

        inventoryItemService.deleteItem(1L);

        verify(inventoryItemRepository, times(1)).findById(1L);
        verify(inventoryItemRepository, times(1)).delete(item);
    }

    @Test
    void deleteItem_shouldThrowException_whenItemDoesNotExist() {
        when(inventoryItemRepository.findById(999L))
                .thenReturn(java.util.Optional.empty());

        org.springframework.web.server.ResponseStatusException exception =
                assertThrows(
                        org.springframework.web.server.ResponseStatusException.class,
                        () -> inventoryItemService.deleteItem(999L)
                );

        assertEquals(
                org.springframework.http.HttpStatus.NOT_FOUND,
                exception.getStatusCode()
        );

        verify(inventoryItemRepository, times(1)).findById(999L);
        verify(inventoryItemRepository, never()).delete(any(InventoryItem.class));
    }

    @Test
    void searchItems_shouldReturnFilteredPage() {
        InventoryItem item = new InventoryItem();
        item.setId(1L);
        item.setName("Wireless Mouse");
        item.setSku("MOUSE-001");
        item.setQuantity(15);
        item.setPrice(new BigDecimal("29.99"));
        item.setCategory(Category.ELECTRONICS);

        org.springframework.data.domain.Pageable pageable =
                org.springframework.data.domain.PageRequest.of(0, 10);

        org.springframework.data.domain.Page<InventoryItem> page =
                new org.springframework.data.domain.PageImpl<>(
                        List.of(item), pageable, 1
                );

        when(inventoryItemRepository.searchItems(
                "Mouse", "MOUSE", Category.ELECTRONICS, pageable
        )).thenReturn(page);

        org.springframework.data.domain.Page<InventoryItemResponse> result =
                inventoryItemService.searchItems(
                        " Mouse ", " MOUSE ", Category.ELECTRONICS, pageable
                );

        assertEquals(1, result.getTotalElements());
        assertEquals("Wireless Mouse", result.getContent().get(0).getName());
        assertEquals("MOUSE-001", result.getContent().get(0).getSku());

        verify(inventoryItemRepository, times(1)).searchItems(
                "Mouse", "MOUSE", Category.ELECTRONICS, pageable
        );
    }

    @Test
    void searchItems_shouldConvertBlankFiltersToNull() {
        org.springframework.data.domain.Pageable pageable =
                org.springframework.data.domain.PageRequest.of(0, 10);

        org.springframework.data.domain.Page<InventoryItem> page =
                new org.springframework.data.domain.PageImpl<>(
                        List.of(), pageable, 0
                );

        when(inventoryItemRepository.searchItems(
                null, null, null, pageable
        )).thenReturn(page);

        org.springframework.data.domain.Page<InventoryItemResponse> result =
                inventoryItemService.searchItems(
                        "   ", null, null, pageable
                );

        assertTrue(result.isEmpty());

        verify(inventoryItemRepository, times(1)).searchItems(
                null, null, null, pageable
        );
    }
}
