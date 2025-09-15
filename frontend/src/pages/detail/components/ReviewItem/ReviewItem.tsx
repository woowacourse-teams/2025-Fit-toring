import styled from '@emotion/styled';

import emptyStar from '../../../../common/assets/images/emptyStarIcon.svg';
import filledStar from '../../../../common/assets/images/starIcon.svg';

import type { ReviewResponse } from '../../types/ReviewResponse';

function ReviewItem({ review }: { review: ReviewResponse }) {
  const { reviewerName, createdAt, rating, content } = review;
  const [year, month, day] = createdAt.split('-');

  return (
    <StyledContainer>
      <StyledNameAndRatingWrapper>
        <StyledName>{reviewerName}</StyledName>
        <StyledRating>
          {Array.from({ length: 5 }).map((_, index) => {
            const score = index + 1;
            if (score <= rating) {
              return <img key={index} src={filledStar} />;
            }
            return <img key={index} src={emptyStar} />;
          })}
        </StyledRating>
      </StyledNameAndRatingWrapper>
      <StyledDate>
        {year}년 {month}월 {day}일
      </StyledDate>
      <StyledContent>{content}</StyledContent>
    </StyledContainer>
  );
}

export default ReviewItem;

const StyledContainer = styled.li`
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

const StyledNameAndRatingWrapper = styled.div`
  display: flex;
  justify-content: space-between;
`;

const StyledName = styled.p`
  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
  color: ${({ theme }) => theme.FONT.B01};
`;

const StyledRating = styled.div`
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

const StyledDate = styled.p`
  ${({ theme }) => theme.TYPOGRAPHY.B3_R}
  color: ${({ theme }) => theme.FONT.B04};
`;

const StyledContent = styled.p`
  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
  color: ${({ theme }) => theme.FONT.B03};
`;
