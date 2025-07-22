-- MENTORING
INSERT INTO mentoring (mentor_name, mentor_phone, price, career, content, introduction)
VALUES ('김트레이너', '010-1234-5678', 5000, 5, '체형 교정 전문가', '5년차 헬스트레이너입니다. 자세 교정에 특화돼 있습니다.'),
       ('이헬스', '010-2345-6789', 6000, 3, '근비대 프로그램 제공', '근육량 증가에 관심 있다면 저를 찾아주세요.'),
       ('박다이어트', '010-3456-7890', 4500, 4, '식단 중심의 다이어트', '비용 효율적인 다이어트 루틴을 제공합니다.'),
       ('최홈트', '010-4567-8901', 3000, 2, '홈트레이닝 입문', '운동 초보자를 위한 홈트 멘토링을 진행합니다.'),
       ('정PT', '010-5678-9012', 8000, 6, '전문 선수 출신', '하드 트레이닝을 원하시면 추천드립니다.');

-- CATEGORY
INSERT INTO category (title)
VALUES ('체형교정'),
       ('근육증가'),
       ('다이어트'),
       ('홈트레이닝'),
       ('고강도 훈련');

-- CATEGORY_MENTORING (연결 테이블)
INSERT INTO category_mentoring (mentoring_id, category_id)
VALUES (1, 1), -- 김트레이너 → 체형교정
       (2, 2), -- 이헬스 → 근육증가
       (3, 3), -- 박다이어트 → 다이어트
       (4, 4), -- 최홈트 → 홈트레이닝
       (5, 5); -- 정PT → 고강도 훈련

-- -- CERTIFICATE (멘토당 하나씩 자격증)
INSERT INTO certificate (mentoring_id, title)
VALUES (1, 'NASM CPT'),
       (2, '생활스포츠지도사 2급'),
       (3, '건강운동관리사'),
       (4, '홈트레이너 자격증'),
       (5, 'NSCA CSCS');

-- OFFERING (멘토링 제공 서비스 설명)
INSERT INTO offering (mentoring_id, content)
VALUES (1, '체형 분석 및 1:1 맞춤 운동'),
       (2, '3개월 근비대 루틴 제공'),
       (3, '식단 컨설팅 및 체중 관리'),
       (4, '매일 10분 홈트 챌린지'),
       (5, '웨이트 고강도 코칭');

INSERT INTO image (url, image_type, relation_id)
VALUES ( 'https://www.google.com/imgres?q=%ED%97%AC%EC%8A%A4&imgurl=https%3A%2F%2Fwww.ksponco.or.kr%2Fsports%2Ffiles%2Fview%3Fid%3D2fac6016-a968-4e73-833c-d3097045390e%26seq%3D1&imgrefurl=https%3A%2F%2Fwww.ksponco.or.kr%2Fsports%2Fcourses%2F140%3Fmid%3Da90101000000&docid=G8P9PUkT98VZKM&tbnid=OoN80IRMs2RweM&vet=12ahUKEwjn8f2J58COAxWtglYBHUDWBaAQM3oECA0QAA..i&w=880&h=539&hcb=2&ved=2ahUKEwjn8f2J58COAxWtglYBHUDWBaAQM3oECA0QAA'
       , 'MENTORING', 1);
