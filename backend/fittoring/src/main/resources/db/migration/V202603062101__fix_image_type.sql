-- 기존 DB 상태가 flyway 초기화 명세와 일치하지 않아 추가 수정합니다.
ALTER TABLE image MODIFY image_type VARCHAR(255) NOT NULL;
