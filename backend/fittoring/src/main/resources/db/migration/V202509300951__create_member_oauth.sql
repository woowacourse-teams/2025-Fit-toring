CREATE TABLE member_oauth (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  member_id BIGINT NOT NULL,
  provider VARCHAR(20) NOT NULL,
  provider_member_id VARCHAR(128) NOT NULL,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  deleted_at DATETIME NULL,

  UNIQUE(provider, provider_member_id),
  FOREIGN KEY (member_id) REFERENCES member(id)
);
