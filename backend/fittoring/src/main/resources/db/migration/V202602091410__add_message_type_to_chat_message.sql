ALTER TABLE chat_message
    ADD COLUMN message_type VARCHAR(32) NOT NULL DEFAULT 'TEXT';

ALTER TABLE chat_message
    ADD CONSTRAINT chk_message_type CHECK (message_type IN ('TEXT', 'IMAGE'));
