package com.example.statistics.repository;

import com.example.statistics.entity.StatisticsSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticsSnapshotRepository
        extends JpaRepository<StatisticsSnapshot, Long> {

    List<StatisticsSnapshot> findByCollectedAtBetweenOrderByCollectedAtAsc(
            LocalDateTime from,
            LocalDateTime to
    );
}
