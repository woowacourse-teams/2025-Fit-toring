-- chat_room 테이블: 멤버별 채팅방 목록 조회를 위한 인덱스 추가
-- findAllByMenteeIdOrMentorId 필터링 최적화 (정렬은 애플리케이션에서 마지막 메시지 기준으로 수행)
CREATE INDEX idx_mentee_id ON chat_room (mentee_id, id DESC);
CREATE INDEX idx_mentor_id ON chat_room (mentor_id, id DESC);

-- chat_message 테이블: 채팅방별 마지막 메시지 조회를 위한 인덱스 추가
-- SELECT MAX(id) FROM chat_message WHERE chat_room_id IN (...) GROUP BY chat_room_id 커버링 인덱스 처리
CREATE INDEX idx_chat_message_room_id ON chat_message (chat_room_id, id DESC);
