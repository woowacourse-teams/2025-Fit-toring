CREATE TABLE chat_message
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    chat_room_id BIGINT NOT NULL,
    sender_id    BIGINT NOT NULL,
    content      TEXT NOT NULL,
    created_at   DATETIME NOT NULL,
    is_deleted   BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at   DATETIME NULL
);

CREATE INDEX idx_chat_message_room_created_at
    ON chat_message (chat_room_id, created_at DESC);
