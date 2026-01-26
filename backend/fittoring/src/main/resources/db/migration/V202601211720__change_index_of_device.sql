ALTER TABLE device ADD KEY idx_device_push_token (push_token);
ALTER TABLE device ADD KEY idx_device_member_id (member_id);

ALTER TABLE device DROP INDEX uk_member_push_token;