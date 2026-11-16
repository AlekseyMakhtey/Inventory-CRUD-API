
package com.example.statistics.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "statistics_snapshots")
public class StatisticsSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "collected_at", nullable = false)
    private LocalDateTime collectedAt;

    @Column(name = "total_count", nullable = false)
    private Long totalCount;

    public StatisticsSnapshot() {
    }

    public StatisticsSnapshot(LocalDateTime collectedAt, Long totalCount) {
        this.collectedAt = collectedAt;
        this.totalCount = totalCount;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCollectedAt() {
        return collectedAt;
    }

    public void setCollectedAt(LocalDateTime collectedAt) {
        this.collectedAt = collectedAt;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
}
