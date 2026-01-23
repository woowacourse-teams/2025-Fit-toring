-- 1. 기존 숫자 status를 임시 컬럼으로 보존
ALTER TABLE reservation
    ADD COLUMN status_tmp VARCHAR(20);

-- 2. 숫자 → 문자열 Enum 값으로 변환
UPDATE reservation
SET status_tmp = CASE status
    WHEN 0 THEN 'APPROVED'
    WHEN 1 THEN 'PENDING'
    WHEN 2 THEN 'REJECTED'
    WHEN 3 THEN 'COMPLETE'
    ELSE NULL
END;

-- 3. 기존 status 컬럼 제거
ALTER TABLE reservation
    DROP COLUMN status;

-- 4. 문자열 status 컬럼 새로 생성
ALTER TABLE reservation
    ADD COLUMN status VARCHAR(20) NOT NULL;

-- 5. 변환된 값 복사
UPDATE reservation
SET status = status_tmp;

-- 6. 임시 컬럼 제거
ALTER TABLE reservation
    DROP COLUMN status_tmp;
