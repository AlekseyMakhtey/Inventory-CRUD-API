package com.example.statistics.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "statistics_category",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_snapshot_category",
            columnNames = {"snapshot_id", "category"}
        )
    }
)
public class StatisticsCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "snapshot_id", nullable = false)
    private StatisticsSnapshot snapshot;

    @Column(nullable = false, length = 30)
    private String category;

    @Column(name = "item_count", nullable = false)
    private Long itemCount;

    public StatisticsCategory() {
    }

    public StatisticsCategory(
            StatisticsSnapshot snapshot,
            String category,
            Long itemCount
    ) {
        this.snapshot = snapshot;
        this.category = category;
        this.itemCount = itemCount;
    }

    public Long getId() {
        return id;
    }

    public StatisticsSnapshot getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(StatisticsSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getItemCount() {
        return itemCount;
    }

    public void setItemCount(Long itemCount) {
        this.itemCount = itemCount;
    }
}
