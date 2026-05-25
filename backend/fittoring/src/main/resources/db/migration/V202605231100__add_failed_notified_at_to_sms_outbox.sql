ALTER TABLE sms_outbox
    ADD COLUMN failed_notified_at DATETIME(3) NULL;
