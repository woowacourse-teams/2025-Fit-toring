ALTER TABLE chat_room
    ADD COLUMN last_message_id BIGINT NULL,
    ADD COLUMN last_message_content TEXT NULL,
    ADD COLUMN last_message_type VARCHAR(20) NULL,
    ADD COLUMN last_message_created_at DATETIME NULL,
    ADD COLUMN last_message_sender_id BIGINT NULL;

-- 기존 chat_room 이 새 snapshot 구조를 바로 사용할 수 있도록
-- 채팅방별 마지막 메시지 정보를 한 번 backfill 한다.
UPDATE chat_room cr
JOIN (
    -- 기존 목록 조회 정책과 동일하게, 삭제되지 않은 메시지 중
    -- chat_room 별 최대 id 를 마지막 메시지로 간주한다.
    SELECT cm.chat_room_id, MAX(cm.id) AS last_message_id
    FROM chat_message cm
    WHERE cm.is_deleted = false
    GROUP BY cm.chat_room_id
) latest ON latest.chat_room_id = cr.id
-- 찾은 마지막 메시지 id 로 다시 chat_message 를 조인해
-- snapshot 컬럼에 필요한 실제 값을 채운다.
JOIN chat_message cm ON cm.id = latest.last_message_id
SET cr.last_message_id = cm.id,
    cr.last_message_content = cm.content,
    cr.last_message_type = cm.message_type,
    cr.last_message_created_at = cm.created_at,
    cr.last_message_sender_id = cm.sender_id;
