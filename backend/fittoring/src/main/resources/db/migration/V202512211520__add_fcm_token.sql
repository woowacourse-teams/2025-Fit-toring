CREATE TABLE fcm_token(
    id          BIGINT  AUTO_INCREMENT  PRIMARY KEY,
    member_id   BIGINT  NOT NULL,
    token       VARCHAR(255) NOT NULL,
    updated_at  DATETIME NOT NULL,

    UNIQUE KEY uk_member_id(member_id),
    UNIQUE KEY uk_token(token),
    INDEX idx_member_id(member_id)
);