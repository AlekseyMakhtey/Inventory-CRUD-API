
CREATE TABLE statistics_snapshots (
    id BIGSERIAL PRIMARY KEY,
    collected_at TIMESTAMP NOT NULL,
    total_count BIGINT NOT NULL
);

CREATE TABLE statistics_category (
    id BIGSERIAL PRIMARY KEY,
    snapshot_id BIGINT NOT NULL,
    category VARCHAR(30) NOT NULL,
    item_count BIGINT NOT NULL,

    CONSTRAINT fk_statistics_snapshot
        FOREIGN KEY (snapshot_id)
        REFERENCES statistics_snapshots(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_snapshot_category
        UNIQUE (snapshot_id, category)
);

CREATE INDEX idx_statistics_collected_at
    ON statistics_snapshots(collected_at);
