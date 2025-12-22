-- mentoring 테이블: 정렬 타이브레이커 추가
DROP INDEX idx_created_at ON mentoring;
CREATE INDEX idx_created_at_id ON mentoring (created_at DESC, id DESC);

-- mentoring_statistics 테이블: 인기순/평점순 정렬 및 타이브레이커 추가
DROP INDEX idx_reservation_count ON mentoring_statistics;
DROP INDEX idx_average_rating ON mentoring_statistics;

CREATE INDEX idx_res_count_mid ON mentoring_statistics (reservation_count DESC, mentoring_id DESC);
CREATE INDEX idx_avg_rating_mid ON mentoring_statistics (average_rating DESC, mentoring_id DESC);
