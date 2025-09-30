CREATE TABLE member_oauth (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  member_id BIGINT NOT NULL,
  provider VARCHAR(20) NOT NULL,
  provider_member_id VARCHAR(128) NOT NULL,
  UNIQUE(provider, provider_member_id),
  FOREIGN KEY (member_id) REFERENCES member(id)
);
