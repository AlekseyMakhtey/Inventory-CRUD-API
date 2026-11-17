package com.example.statistics.dto;

import java.util.List;

public record InventoryPageResponse(
        List<InventoryItemResponse> content,
        PageMetadata page
) {
}
