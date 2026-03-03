-- 1. 가상 컬럼 추가 (Virtual Column)
-- PENDING(0) 또는 APPROVE(1)일 때만 ACTIVE 값을 가지고, 그 외에는 NULL을 반환
ALTER TABLE reservation
    ADD COLUMN active_status_checker VARCHAR(20) AS (
        CASE
            WHEN is_deleted = false AND status IN ('PENDING', 'APPROVED')
                THEN 'ACTIVE'
            ELSE NULL
        END
    ) VIRTUAL;

-- 2. 정책 강제를 위한 복합 유니크 인덱스 추가
-- (mentee_id, mentoring_id, active_status_checker) 조합이 중복되지 않도록 설정
ALTER TABLE reservation
    ADD CONSTRAINT uk_active_reservation
   UNIQUE (mentee_id, mentoring_id, active_status_checker);
