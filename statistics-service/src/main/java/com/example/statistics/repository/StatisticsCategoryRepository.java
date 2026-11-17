package com.example.statistics.repository;

import com.example.statistics.entity.StatisticsCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatisticsCategoryRepository
        extends JpaRepository<StatisticsCategory, Long> {

    List<StatisticsCategory> findBySnapshotId(Long snapshotId);
}
