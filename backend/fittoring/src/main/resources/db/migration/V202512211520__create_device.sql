CREATE TABLE device(
    id          BIGINT  AUTO_INCREMENT  PRIMARY KEY,
    member_id   BIGINT  NOT NULL,
    hardware_id  VARCHAR(255) NOT NULL,
    push_token  VARCHAR(255) NOT NULL,
    updated_at  DATETIME NOT NULL,
    UNIQUE KEY uk_hardware_id(hardware_id),
    CONSTRAINT fk_device_member FOREIGN KEY (member_id) REFERENCES member (id),
    INDEX idx_member_id(member_id)
);
