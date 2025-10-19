import styled from '@emotion/styled';

import emptyStar from '../../../../common/assets/images/emptyStarIcon.svg';
import filledStar from '../../../../common/assets/images/starIcon.svg';

import type { ReviewResponse } from '../../types/ReviewResponse';

function ReviewItem({ review }: { review: ReviewResponse }) {
  const { reviewerName, createdAt, rating, content } = review;
  const [year, month, day] = createdAt.split('-');

  return (
    <S_Container>
      <S_NameAndRatingWrapper>
        <S_Name>{reviewerName}</S_Name>
        <S_Rating role="text" aria-label={`별점 ${rating}점`}>
          {Array.from({ length: 5 }).map((_, index) => {
            const score = index + 1;
            if (score <= rating) {
              return <img key={index} src={filledStar} aria-hidden="true" />;
            }
            return <img key={index} src={emptyStar} aria-hidden="true" />;
          })}
        </S_Rating>
      </S_NameAndRatingWrapper>
      <S_Date
        role="text"
        aria-label={`리뷰 작성일: ${year}년 ${month}월 ${day}일`}
      >
        {year}년 {month}월 {day}일
      </S_Date>
      <S_Content>{content}</S_Content>
    </S_Container>
  );
}

export default ReviewItem;

const S_Container = styled.li`
  display: flex;
  flex-direction: column;
  gap: 1.2rem;

  width: 100%;
  padding: 2.1rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.DARK};
  border-radius: 8px;

  background-color: ${({ theme }) => theme.BG.WHITE};

  color: ${({ theme }) => theme.FONT.B04};
`;

const S_NameAndRatingWrapper = styled.div`
  display: flex;
  justify-content: space-between;
`;

const S_Name = styled.p`
  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
  color: ${({ theme }) => theme.FONT.B01};
`;

const S_Rating = styled.div`
  display: flex;
  align-items: center;
  gap: 0.1rem;

  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
  color: ${({ theme }) => theme.FONT.B01};

  > img {
    width: 1.7rem;
    height: 1.7rem;
  }
`;

const S_Date = styled.p`
  ${({ theme }) => theme.TYPOGRAPHY.B3_R}
  color: ${({ theme }) => theme.FONT.B04};
`;

const S_Content = styled.p`
  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
  color: ${({ theme }) => theme.FONT.B03};
`;
