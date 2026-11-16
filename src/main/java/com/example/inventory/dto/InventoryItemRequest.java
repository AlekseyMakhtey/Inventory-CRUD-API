package com.example.inventory.dto;

import com.example.inventory.entity.Category;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class InventoryItemRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 50, message = "Name must contain 3-50 characters")
    private String name;

    @NotBlank(message = "SKU is required")
    @Size(max = 30, message = "SKU must contain under 30 characyers")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$",
            message = "SKU can contain only latin letters, digits, hyphens and underscores")
    private String sku;

    @NotNull(message = "Quantity is required")
    @PositiveOrZero(message = "Quantity must be >=0")
    private Integer quantity;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00", message = "Price must be positiv")
    @Digits(integer = 10, fraction = 2,
            message = "Price must have up to 10 integer digits and 2 decimal places")
    private BigDecimal price;

    @NotNull(message = "Category is required")
    private Category category;

    @Size(max = 40, message = "Location must contain under 40 characters")
    private String location;

    @Size(max = 500, message = "Description must contain under 500 characters")
    private String description;

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