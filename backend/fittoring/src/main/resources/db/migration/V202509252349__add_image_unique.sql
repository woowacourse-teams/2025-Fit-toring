ALTER TABLE image
    ADD UNIQUE KEY (image_type, relation_id, image_variant);
