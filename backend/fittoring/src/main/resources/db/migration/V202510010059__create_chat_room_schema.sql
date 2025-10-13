CREATE TABLE chat_room
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    reservation_id BIGINT NOT NULL,
    mentee_id      BIGINT NOT NULL,
    mentor_id      BIGINT NOT NULL,
    created_at     DATETIME NOT NULL,
    is_deleted     BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at     DATETIME DEFAULT NULL
)
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;
