package com.example.statistics.service;

import com.example.statistics.dto.StatisticsResponse;
import com.example.statistics.entity.StatisticsSnapshot;
import com.example.statistics.repository.StatisticsCategoryRepository;
import com.example.statistics.repository.StatisticsSnapshotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsQueryService {

    private final StatisticsSnapshotRepository snapshotRepository;
    private final StatisticsCategoryRepository categoryRepository;

    public StatisticsQueryService(
            StatisticsSnapshotRepository snapshotRepository,
            StatisticsCategoryRepository categoryRepository
    ) {
        this.snapshotRepository = snapshotRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public java.util.List<StatisticsResponse> getStatistics(
            LocalDateTime from,
            LocalDateTime to
    ) {
        return snapshotRepository
                .findByCollectedAtBetweenOrderByCollectedAtAsc(from, to)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private StatisticsResponse toResponse(StatisticsSnapshot snapshot) {
        Map<String, Long> countPerCategory =
                categoryRepository.findBySnapshotId(snapshot.getId())
                        .stream()
                        .collect(Collectors.toMap(
                                category -> category.getCategory(),
                                category -> category.getItemCount()
                        ));

        return new StatisticsResponse(
                snapshot.getCollectedAt(),
                snapshot.getTotalCount(),
                countPerCategory
        );
    }
}
