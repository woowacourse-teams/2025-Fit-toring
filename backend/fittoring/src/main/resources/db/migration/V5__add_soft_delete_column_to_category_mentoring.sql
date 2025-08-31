ALTER TABLE category_mentoring
ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE category_mentoring
ADD COLUMN deleted_at DATETIME NULL

