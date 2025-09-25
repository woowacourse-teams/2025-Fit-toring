CREATE TABLE mentoring_statistics (
    mentoring_id BIGINT PRIMARY KEY,
    reservation_count BIGINT NOT NULL,
    review_count BIGINT NOT NULL,
    review_sum BIGINT NOT NULL,
    updated_at DATETIME NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at DATETIME NULL,

    CONSTRAINT fk_mentoring_statistics_mentoring FOREIGN KEY (mentoring_id) REFERENCES mentoring(id)
);
