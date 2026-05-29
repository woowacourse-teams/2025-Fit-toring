CREATE TABLE dummy_post_pending (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    scenario_file VARCHAR(255) NOT NULL,
    scenario_seq INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    nickname VARCHAR(50) NOT NULL,
    guest_password VARCHAR(255) NOT NULL,
    scheduled_at DATETIME(6) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    published_post_id BIGINT NULL,
    attempt_count INT NOT NULL DEFAULT 0,
    CONSTRAINT uk_dummy_post_pending_scenario UNIQUE (scenario_file, scenario_seq),
    CONSTRAINT chk_dummy_post_pending_status CHECK (status IN ('PENDING', 'PUBLISHED', 'FAILED'))
);

CREATE TABLE dummy_comment_pending (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pending_post_id BIGINT NOT NULL,
    pending_root_id BIGINT NULL,
    pending_parent_id BIGINT NULL,
    content TEXT NOT NULL,
    nickname VARCHAR(50) NOT NULL,
    guest_password VARCHAR(255) NOT NULL,
    scheduled_at DATETIME(6) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    published_comment_id BIGINT NULL,
    attempt_count INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_dummy_comment_pending_post
        FOREIGN KEY (pending_post_id) REFERENCES dummy_post_pending(id),
    CONSTRAINT fk_dummy_comment_pending_root
        FOREIGN KEY (pending_root_id) REFERENCES dummy_comment_pending(id),
    CONSTRAINT fk_dummy_comment_pending_parent
        FOREIGN KEY (pending_parent_id) REFERENCES dummy_comment_pending(id),
    CONSTRAINT chk_dummy_comment_pending_status CHECK (status IN ('PENDING', 'PUBLISHED', 'FAILED'))
);

CREATE INDEX idx_dummy_post_pending_status_scheduled_at
    ON dummy_post_pending(status, scheduled_at);
CREATE INDEX idx_dummy_post_pending_published_post_id
    ON dummy_post_pending(published_post_id);

CREATE INDEX idx_dummy_comment_pending_status_scheduled_at
    ON dummy_comment_pending(status, scheduled_at);
CREATE INDEX idx_dummy_comment_pending_pending_post_id
    ON dummy_comment_pending(pending_post_id);
CREATE INDEX idx_dummy_comment_pending_pending_root_id
    ON dummy_comment_pending(pending_root_id);
CREATE INDEX idx_dummy_comment_pending_pending_parent_id
    ON dummy_comment_pending(pending_parent_id);
