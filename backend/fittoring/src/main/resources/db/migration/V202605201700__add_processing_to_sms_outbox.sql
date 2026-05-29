ALTER TABLE sms_outbox
    ADD COLUMN processing_started_at DATETIME(3) NULL;
