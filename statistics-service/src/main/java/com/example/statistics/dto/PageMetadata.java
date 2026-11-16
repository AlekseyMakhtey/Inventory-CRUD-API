
package com.example.statistics.dto;

public record PageMetadata(
        int size,
        int number,
        long totalElements,
        int totalPages
) {
}
