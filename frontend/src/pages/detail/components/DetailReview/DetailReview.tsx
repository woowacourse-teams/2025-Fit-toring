import styled from '@emotion/styled';
import filledStar from '../../../../common/assets/images/starIcon.svg';
import ReviewItem from '../ReviewItem/ReviewItem';
import { getReviews } from '../../apis/getReviews';
import { useEffect, useState } from 'react';
import { ReviewResponse } from '../../types/ReviewResponse';
import { captureSentryError } from '../../../../common/utils/captureSentryError';

interface DetailReviewProps {
  mentoringId: number;
  ratingAverage: string;
  ratingCount: number;
}

function DetailReview({
  mentoringId,
  ratingAverage,
  ratingCount,
}: DetailReviewProps) {
  const [totalReviewInfo, setTotalReviewInfo] = useState<
    ReviewResponse[] | null
  >(null);

  const fetchReview = async () => {
    try {
      const response = await getReviews(mentoringId);
      setTotalReviewInfo(response);
    } catch (error) {
      console.error('Error fetching reviews:', error);
      captureSentryError({
        error,
        level: 'warning',
        feature: 'detail',
        step: 'mentoring-review-fetch',
      });
    }
  };

  useEffect(() => {
    fetchReview();
  }, []);

  if (!totalReviewInfo) return null;

  return (
    <StyledContainer>
      <StyledTotalWrapper>
        <img src={filledStar} />
        <strong>{ratingAverage}</strong>
        <p>({ratingCount}개 리뷰)</p>
      </StyledTotalWrapper>
      <StyledReviewList>
        {totalReviewInfo.map((review) => (
          <ReviewItem key={review.id} review={review} />
        ))}
      </StyledReviewList>
    </StyledContainer>
  );
}

export default DetailReview;

const StyledContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: 3rem;

  width: 100%;
  margin-top: 4rem;
`;

const StyledTotalWrapper = styled.div`
  display: flex;
  align-items: center;
  gap: 0.5rem;

  > img {
    width: 2.4rem;
    height: 2.4rem;
  }

  > strong {
    ${({ theme }) => theme.TYPOGRAPHY.LB3_R}
    color: ${({ theme }) => theme.FONT.B01};
  }

  > p {
    ${({ theme }) => theme.TYPOGRAPHY.B2_R}
    color: ${({ theme }) => theme.FONT.B04};
  }
`;

const StyledReviewList = styled.ul`
  display: flex;
  flex-direction: column;
  gap: 1rem;

  width: 100%;
`;
