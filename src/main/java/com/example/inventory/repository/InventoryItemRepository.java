package com.example.inventory.repository;

import com.example.inventory.entity.Category;
import com.example.inventory.entity.InventoryItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

    @Query("""
            SELECT i FROM InventoryItem i
            WHERE (COALESCE(:name, '') = '' OR LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%')))
              AND (COALESCE(:sku, '') = '' OR LOWER(i.sku) LIKE LOWER(CONCAT('%', :sku, '%')))
              AND (CAST(:category AS string) IS NULL OR i.category = :category)
            """)
    Page<InventoryItem> searchItems(
            @Param("name") String name,
            @Param("sku") String sku,
            @Param("category") Category category,
            Pageable pageable
    );
}
