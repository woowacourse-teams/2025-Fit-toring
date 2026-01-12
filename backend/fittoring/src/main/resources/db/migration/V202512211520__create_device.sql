CREATE TABLE device(
    id          BIGINT  AUTO_INCREMENT  PRIMARY KEY,
    member_id   BIGINT,
    push_token  VARCHAR(255) NOT NULL,
    is_push_enabled BOOLEAN DEFAULT TRUE,
    created_at  DATETIME NOT NULL,
    UNIQUE KEY uk_member_push_token (member_id, push_token),
    CONSTRAINT fk_device_member FOREIGN KEY (member_id) REFERENCES member (id)
);
