package com.example.statistics.service;

import com.example.statistics.client.InventoryApiClient;
import com.example.statistics.dto.InventoryPageResponse;
import com.example.statistics.entity.StatisticsCategory;
import com.example.statistics.entity.StatisticsSnapshot;
import com.example.statistics.repository.StatisticsCategoryRepository;
import com.example.statistics.repository.StatisticsSnapshotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class StatisticsCollectionService {

    private final InventoryApiClient inventoryApiClient;
    private final StatisticsSnapshotRepository snapshotRepository;
    private final StatisticsCategoryRepository categoryRepository;

    public StatisticsCollectionService(
            InventoryApiClient inventoryApiClient,
            StatisticsSnapshotRepository snapshotRepository,
            StatisticsCategoryRepository categoryRepository
    ) {
        this.inventoryApiClient = inventoryApiClient;
        this.snapshotRepository = snapshotRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public void collectStatistics() {
        String token = inventoryApiClient.login();

        Map<String, Long> countPerCategory = new HashMap<>();
        long totalCount = 0;

        InventoryPageResponse firstPage =
                inventoryApiClient.getItems(token, 0);

        if (firstPage == null || firstPage.page() == null) {
            throw new IllegalStateException("Inventory API returned an invalid response");
        }

        for (int page = 0; page < firstPage.page().totalPages(); page++) {
            InventoryPageResponse response = page == 0
                    ? firstPage
                    : inventoryApiClient.getItems(token, page);

            if (response == null || response.content() == null) {
                throw new IllegalStateException("Invalid inventory page response");
            }

            for (var item : response.content()) {
                totalCount++;

                if (item.category() != null) {
                    countPerCategory.merge(
                            item.category(),
                            1L,
                            Long::sum
                    );
                }
            }
        }

        StatisticsSnapshot snapshot = snapshotRepository.save(
                new StatisticsSnapshot(LocalDateTime.now(), totalCount)
        );

        for (var entry : countPerCategory.entrySet()) {
            categoryRepository.save(
                    new StatisticsCategory(
                            snapshot,
                            entry.getKey(),
                            entry.getValue()
                    )
            );
        }
    }
}
