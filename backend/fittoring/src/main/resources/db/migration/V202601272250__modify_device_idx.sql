ALTER TABLE device DROP INDEX idx_device_push_token;
CREATE INDEX idx_device_push_token ON device(push_token(35));
