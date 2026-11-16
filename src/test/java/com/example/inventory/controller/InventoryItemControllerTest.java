package com.example.inventory.controller;

import com.example.inventory.InventoryApiApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

@SpringBootTest(classes = InventoryApiApplication.class)
@AutoConfigureMockMvc
@Transactional
class InventoryItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(authorities = "ITEM_READ")
    void getAllItems_shouldReturnOk() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/items")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page").exists());
    }

    @Test
    @WithMockUser(authorities = "ITEM_READ")
    void getItemById_shouldReturnNotFound_whenItemDoesNotExist() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/items/999999")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path")
                        .value("/api/items/999999"));
    }



    @Test
    @WithMockUser(authorities = "ITEM_READ")
    void updateItem_shouldReturnForbidden_whenUserIsViewer() throws Exception {
        Long itemId = createTestItem();

        String requestBody = """
                {
                    "name": "Forbidden Update",
                    "sku": "FORBIDDEN-UPD-%s",
                    "quantity": 20,
                    "price": 59.99,
                    "category": "ELECTRONICS",
                    "location": "warehouse-test",
                    "description": "This item should not be updated"
                }
                """.formatted(System.nanoTime());

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/items/" + itemId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }



    @Test
    @WithMockUser(authorities = "ITEM_READ")
    void deleteItem_shouldReturnForbidden_whenUserIsViewer() throws Exception {
        Long itemId = createTestItem();

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/items/" + itemId)
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {
            "ITEM_READ",
            "ITEM_CREATE",
            "ITEM_UPDATE",
            "ITEM_DELETE"
    })
    void createItem_shouldReturnCreated() throws Exception {
        String sku = "TEST-KEYBOARD-" + System.nanoTime();

        String requestBody = """
                {
                    "name": "Test Keyboard",
                    "sku": "%s",
                    "quantity": 15,
                    "price": 49.99,
                    "category": "ELECTRONICS",
                    "location": "warehouse-test",
                    "description": "Test keyboard for integration testing"
                }
                """.formatted(sku);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test Keyboard"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sku").value(sku));
    }

    @Test
    @WithMockUser(authorities = {
            "ITEM_READ",
            "ITEM_CREATE",
            "ITEM_UPDATE",
            "ITEM_DELETE"
    })
    void updateItem_shouldReturnOk() throws Exception {
        Long itemId = createTestItem();

        String requestBody = """
                {
                    "name": "Updated Keyboard",
                    "sku": "UPD-%s",
                    "quantity": 20,
                    "price": 59.99,
                    "category": "ELECTRONICS",
                    "location": "warehouse-2",
                    "description": "Updated test keyboard"
                }
                """.formatted(System.nanoTime());

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/items/" + itemId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Updated Keyboard"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.quantity").value(20));
    }

    @Test
    @WithMockUser(authorities = {
            "ITEM_READ",
            "ITEM_CREATE",
            "ITEM_UPDATE",
            "ITEM_DELETE"
    })
    void deleteItem_shouldReturnNoContent() throws Exception {
        Long itemId = createTestItem();

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/items/" + itemId)
                )
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = "ITEM_READ")
    void createItem_shouldReturnForbidden_whenUserIsViewer() throws Exception {
        String requestBody = """
                {
                    "name": "Forbidden Keyboard",
                    "sku": "FORBIDDEN-001",
                    "quantity": 10,
                    "price": 39.99,
                    "category": "ELECTRONICS",
                    "location": "warehouse-test",
                    "description": "This item should not be created"
                }
                """;

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    private Long createTestItem() throws Exception {
        String sku = "TEST-ITEM-" + System.nanoTime();

        String requestBody = """
                {
                    "name": "Test Item",
                    "sku": "%s",
                    "quantity": 10,
                    "price": 25.99,
                    "category": "ELECTRONICS",
                    "location": "warehouse-test",
                    "description": "Temporary test item"
                }
                """.formatted(sku);

        String response = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/items")
                                .with(SecurityMockMvcRequestPostProcessors.user("test-admin")
                                        .authorities(
                                                new org.springframework.security.core.authority.SimpleGrantedAuthority("ITEM_READ"),
                                                new org.springframework.security.core.authority.SimpleGrantedAuthority("ITEM_CREATE"),
                                                new org.springframework.security.core.authority.SimpleGrantedAuthority("ITEM_UPDATE"),
                                                new org.springframework.security.core.authority.SimpleGrantedAuthority("ITEM_DELETE")
                                        ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return new com.fasterxml.jackson.databind.ObjectMapper()
                .readTree(response)
                .get("id")
                .asLong();
    }
}