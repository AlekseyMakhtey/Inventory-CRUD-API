
package com.example.statistics.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record StatisticsResponse(
        LocalDateTime collectedAt,
        Long totalCount,
        Map<String, Long> countPerCategory
) {
}
