ALTER TABLE mentoring_statistics
    ADD COLUMN average_rating DOUBLE NOT NULL DEFAULT 0;

ALTER TABLE mentoring ADD INDEX idx_created_at (created_at);

ALTER TABLE mentoring_statistics
    ADD INDEX idx_reservation_count (reservation_count),
    ADD INDEX idx_average_rating (average_rating);