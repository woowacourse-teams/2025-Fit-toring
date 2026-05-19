CREATE TABLE sms_outbox (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reservation_id BIGINT NOT NULL,
    event_type VARCHAR(32) NOT NULL,
    to_phone VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    subject VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL,
    attempts INT NOT NULL DEFAULT 0,
    last_error TEXT,
    created_at DATETIME(3) NOT NULL,
    updated_at DATETIME(3) NOT NULL,
    INDEX idx_sms_outbox_status_created (status, created_at)
);
