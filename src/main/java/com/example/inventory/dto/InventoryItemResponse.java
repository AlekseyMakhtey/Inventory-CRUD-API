package com.example.inventory.dto;

import com.example.inventory.entity.Category;

import java.math.BigDecimal;

public class InventoryItemResponse {

    private Long id;
    private String name;
    private String sku;
    private Integer quantity;
    private BigDecimal price;
    private Category category;
    private String location;
    private String description;

    public InventoryItemResponse() {
    }

    public InventoryItemResponse(
            Long id,
            String name,
            String sku,
            Integer quantity,
            BigDecimal price,
            Category category,
            String location,
            String description
    ) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.quantity = quantity;
        this.price = price;
        this.category = category;
        this.location = location;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}