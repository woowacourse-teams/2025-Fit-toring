CREATE TABLE dummy_scenario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    original_filename VARCHAR(255) NOT NULL,
    content_hash CHAR(64) NOT NULL,
    yaml_content LONGTEXT NOT NULL,
    status VARCHAR(30) NOT NULL,
    uploaded_at DATETIME(6) NOT NULL,
    inserted_at DATETIME(6) NULL,
    original_start_at DATETIME(6) NOT NULL,
    original_duration_seconds BIGINT NOT NULL,
    applied_start_at DATETIME(6) NULL,
    applied_duration_seconds BIGINT NULL,
    post_count INT NOT NULL,
    comment_count INT NOT NULL,
    CONSTRAINT chk_dummy_scenario_status CHECK (status IN ('UPLOADED', 'INSERTED', 'FAILED'))
);

CREATE INDEX idx_dummy_scenario_uploaded_at
    ON dummy_scenario(uploaded_at);

CREATE INDEX idx_dummy_scenario_content_hash
    ON dummy_scenario(content_hash);

ALTER TABLE dummy_post_pending
    ADD COLUMN scenario_id BIGINT NULL AFTER id;

ALTER TABLE dummy_comment_pending
    ADD COLUMN scenario_id BIGINT NULL AFTER id;

ALTER TABLE dummy_post_pending
    ADD CONSTRAINT fk_dummy_post_pending_scenario
        FOREIGN KEY (scenario_id) REFERENCES dummy_scenario(id);

ALTER TABLE dummy_comment_pending
    ADD CONSTRAINT fk_dummy_comment_pending_scenario
        FOREIGN KEY (scenario_id) REFERENCES dummy_scenario(id);

CREATE INDEX idx_dummy_post_pending_scenario_id
    ON dummy_post_pending(scenario_id);

CREATE INDEX idx_dummy_comment_pending_scenario_id
    ON dummy_comment_pending(scenario_id);
