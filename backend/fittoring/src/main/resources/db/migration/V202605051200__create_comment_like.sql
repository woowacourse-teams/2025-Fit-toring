ALTER TABLE comment
    ADD COLUMN like_count INT NOT NULL DEFAULT 0;

CREATE TABLE comment_like (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    comment_id BIGINT NOT NULL,
    actor_key_hash VARCHAR(64) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    FOREIGN KEY (comment_id) REFERENCES comment(id),
    UNIQUE KEY uk_comment_like_comment_id_actor_key_hash (comment_id, actor_key_hash)
);
