ALTER TABLE image
    ADD COLUMN image_variant VARCHAR(50) NOT NULL DEFAULT 'DEFAULT' AFTER image_type;

ALTER TABLE image
    ADD CONSTRAINT chk_image_variant CHECK (image_variant IN ('DEFAULT', 'THUMBNAIL'));
