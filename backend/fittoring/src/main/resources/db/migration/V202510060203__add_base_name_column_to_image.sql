ALTER TABLE image
    ADD COLUMN base_name VARCHAR(64) NULL;

ALTER TABLE image DROP INDEX image_type;

ALTER TABLE image
    ADD CONSTRAINT uq_image_base_name_variant UNIQUE (base_name, image_variant);

CREATE TABLE image_session
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    base_name     VARCHAR(64) NOT NULL,
    image_type    VARCHAR(32) NOT NULL,
    image_variant VARCHAR(32) NOT NULL,
    url           TEXT        NOT NULL,
    payload_json  JSON NULL,
    created_at    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_parking_base_variant UNIQUE (base_name, image_variant)
);
