ALTER TABLE chat_message
    ADD COLUMN message_id VARCHAR(36) NULL;

CREATE UNIQUE INDEX uk_chat_message_message_id
    ON chat_message (message_id);
