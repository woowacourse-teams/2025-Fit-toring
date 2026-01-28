ALTER TABLE device
        ADD UNIQUE KEY uk_device_member_push_token (member_id, push_token);
