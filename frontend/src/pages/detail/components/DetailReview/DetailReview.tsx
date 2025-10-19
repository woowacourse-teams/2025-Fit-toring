import { useCallback, useEffect, useState } from 'react';

import styled from '@emotion/styled';

import filledStar from '../../../../common/assets/images/starIcon.svg';
import { captureSentryError } from '../../../../common/utils/captureSentryError';
import { getReviews } from '../../apis/getReviews';
import ReviewItem from '../ReviewItem/ReviewItem';

import type { ReviewResponse } from '../../types/ReviewResponse';

interface DetailReviewProps {
  mentoringId: number;
  ratingAverage: string;
  ratingCount: number;
  loadingComponent: React.ReactNode;
}

function DetailReview({
  mentoringId,
  ratingAverage,
  ratingCount,
  loadingComponent,
}: DetailReviewProps) {
  const [totalReviewInfo, setTotalReviewInfo] = useState<
    ReviewResponse[] | null
  >(null);

  const fetchReview = useCallback(async () => {
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
  }, [mentoringId]);

  useEffect(() => {
    fetchReview();
  }, [fetchReview]);

  if (!totalReviewInfo) {
    return <>{loadingComponent}</>;
  }

  return (
    <S_Container>
      <S_TotalWrapper
        role="text"
        aria-label={`${ratingCount}개의 리뷰, 평균 ${ratingAverage}점`}
      >
        <img src={filledStar} />
        <strong aria-hidden="true">{ratingAverage}</strong>
        <p aria-hidden="true">({ratingCount}개 리뷰)</p>
      </S_TotalWrapper>
      <S_ReviewList>
        {totalReviewInfo.map((review) => (
          <ReviewItem key={review.id} review={review} />
        ))}
      </S_ReviewList>
    </S_Container>
  );
}

export default DetailReview;

const S_Container = styled.div`
  display: flex;
  flex-direction: column;
  gap: 3rem;

  width: 100%;
  margin-top: 4rem;
`;

const S_TotalWrapper = styled.div`
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

const S_ReviewList = styled.ul`
  display: flex;
  flex-direction: column;
  gap: 1rem;

  width: 100%;
`;
