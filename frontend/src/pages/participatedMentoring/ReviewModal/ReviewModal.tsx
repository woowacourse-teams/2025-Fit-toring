import React, { useState } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';

import Button from '../../../common/components/Button/Button';
import Modal from '../../../common/components/Modal/Modal';
import { postReview } from '../apis/postReview';
import { MAX_RATING_COUNT } from '../constants/starRating';
import StarRating from '../StarRating/StarRating';
import { captureSentryError } from '../../../common/utils/captureSentryError';

interface ReviewModalProps {
  reservationId: number;
  mentorName: string;
  opened: boolean;
  onCloseClick: () => void;
  onReviewSubmitButtonClick: (reservationId: number) => void;
}

function ReviewModal({
  reservationId,
  mentorName,
  opened,
  onCloseClick,
  onReviewSubmitButtonClick,
}: ReviewModalProps) {
  const [rating, setRating] = useState(MAX_RATING_COUNT);
  const [content, setContent] = useState('');

  const handleRatingChange = (newRating: number) => {
    setRating(newRating);
  };

  const handleContentChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setContent(e.target.value);
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    try {
      await postReview({
        reservationId,
        rating,
        content,
      });
      onReviewSubmitButtonClick(reservationId);
      onCloseClick();
    } catch (error) {
      console.error('리뷰 등록 실패', error);
      captureSentryError({
        error,
        level: 'warning',
        feature: 'participatedMentoring',
        step: 'review-create',
      });
    }
  };

  return (
    <Modal opened={opened} onCloseClick={onCloseClick}>
      <StyledContainer onSubmit={handleSubmit}>
        <StyledWrapper>
          <StyledTitle>리뷰 작성</StyledTitle>
          <StyledDescription>
            {mentorName} 멘토와의 상담은 어떠셨나요? 솔직한 후기를 남겨주세요.
          </StyledDescription>
          <StyledSeparator />
        </StyledWrapper>
        <StyledWrapper>
          <StyledSubtitle>만족도 *</StyledSubtitle>
          <StarRating
            rating={rating}
            maxRatingCount={MAX_RATING_COUNT}
            onRatingChange={handleRatingChange}
          />
        </StyledWrapper>
        <StyledWrapper>
          <StyledSubtitle>상세 리뷰</StyledSubtitle>
          <StyledTextarea
            value={content}
            onChange={handleContentChange}
            placeholder="멘토와의 상담 경험을 자세히 공유해주세요. 어떤 점이 도움이 되었는지, 개선할 점은 무엇인지 등을 솔직하게 작성해주시면 다른 분들에게 도움이 됩니다."
          />
        </StyledWrapper>
        <StyledButtonWrapper>
          <Button
            variant="secondary"
            customStyle={css`
              font-size: 1.2rem;
            `}
            type="button"
            onClick={onCloseClick}
          >
            취소
          </Button>
          <Button
            type="submit"
            customStyle={css`
              font-size: 1.2rem;
            `}
          >
            리뷰 등록
          </Button>
        </StyledButtonWrapper>
      </StyledContainer>
    </Modal>
  );
}

export default ReviewModal;

const StyledContainer = styled.form`
  display: flex;
  flex-direction: column;
  gap: 2.4rem;

  width: 100%;
  height: 100%;
`;

const StyledTitle = styled.p`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.LB3_R}
`;

const StyledDescription = styled.p`
  color: ${({ theme }) => theme.FONT.B04};
  ${({ theme }) => theme.TYPOGRAPHY.B3_R}
`;

const StyledSeparator = styled.div`
  border-bottom: 1px solid ${({ theme }) => theme.OUTLINE.DARK};
`;

const StyledWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
`;

const StyledSubtitle = styled.p`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.B3_R}
`;

const StyledTextarea = styled.textarea`
  width: 100%;
  height: 14rem;
  padding: 0.7rem 1.1rem;
  border: ${({ theme }) => theme.OUTLINE.DARK} 1px solid;
  border-radius: 0.7rem;

  ${({ theme }) => theme.TYPOGRAPHY.B2_R};
  resize: none;

  :focus {
    outline: none;
  }

  color: ${({ theme }) => theme.FONT.B01};
`;

const StyledButtonWrapper = styled.div`
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
`;
