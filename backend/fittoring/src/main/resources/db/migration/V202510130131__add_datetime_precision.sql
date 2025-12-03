ALTER TABLE certificate
    MODIFY COLUMN created_at DATETIME(3) NOT NULL,
    MODIFY COLUMN deleted_at DATETIME(3) NULL;

ALTER TABLE category_mentoring
    MODIFY COLUMN deleted_at DATETIME(3) NULL;

ALTER TABLE member
    MODIFY COLUMN deleted_at DATETIME(3) NULL;

ALTER TABLE member_oauth
    MODIFY COLUMN deleted_at DATETIME(3) NULL;

ALTER TABLE mentoring
    MODIFY COLUMN created_at DATETIME(3) NOT NULL,
    MODIFY COLUMN deleted_at DATETIME(3) NULL;

ALTER TABLE mentoring_statistics
    MODIFY COLUMN updated_at DATETIME(3) NOT NULL,
    MODIFY COLUMN deleted_at DATETIME(3) NULL;

ALTER TABLE phone_verification
    MODIFY COLUMN expire_at DATETIME(3) NOT NULL;

ALTER TABLE refresh_token
    MODIFY COLUMN create_at DATETIME(3) NOT NULL;

ALTER TABLE reservation
    MODIFY COLUMN created_at DATETIME(3) NOT NULL,
    MODIFY COLUMN deleted_at DATETIME(3) NULL;

ALTER TABLE review
    MODIFY COLUMN created_at DATETIME(3) NOT NULL,
    MODIFY COLUMN deleted_at DATETIME(3) NULL;
