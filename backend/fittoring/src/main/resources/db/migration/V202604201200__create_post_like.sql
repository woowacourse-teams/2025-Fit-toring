CREATE TABLE post_like (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    actor_key_hash VARCHAR(64) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    FOREIGN KEY (post_id) REFERENCES post(id),
    UNIQUE KEY uk_post_like_post_id_actor_key_hash (post_id, actor_key_hash)
);

CREATE INDEX idx_post_like_post_id ON post_like(post_id);
