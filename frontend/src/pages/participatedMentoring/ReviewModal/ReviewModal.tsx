import React, { useState } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import Button from '../../../common/components/Button/Button';
import Modal from '../../../common/components/Modal/Modal';
import { PAGE_URL } from '../../../common/constants/url';
import { captureSentryError } from '../../../common/utils/captureSentryError';
import { postReview } from '../apis/postReview';
import { MAX_RATING_COUNT } from '../constants/starRating';
import StarRating from '../StarRating/StarRating';

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
  const navigate = useNavigate();

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
      const response = await postReview({
        reservationId,
        rating,
        content,
      });
      const data = await response.json();
      onReviewSubmitButtonClick(reservationId);
      onCloseClick();
      alert('리뷰가 등록되었습니다.');
      navigate(`${PAGE_URL.DETAIL}/${data.mentoringId}`, {
        state: { tab: 'review' },
      });
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
      <S_Container onSubmit={handleSubmit}>
        <S_Wrapper>
          <S_Title>리뷰 작성</S_Title>
          <S_Description>
            {mentorName} 멘토와의 상담은 어떠셨나요? 솔직한 후기를 남겨주세요.
          </S_Description>
          <S_Separator />
        </S_Wrapper>
        <S_Wrapper>
          <S_Subtitle>만족도 *</S_Subtitle>
          <StarRating
            rating={rating}
            maxRatingCount={MAX_RATING_COUNT}
            onRatingChange={handleRatingChange}
          />
        </S_Wrapper>
        <S_Wrapper>
          <S_Subtitle>상세 리뷰</S_Subtitle>
          <S_Textarea
            value={content}
            onChange={handleContentChange}
            placeholder="멘토와의 상담 경험을 자세히 공유해주세요. 어떤 점이 도움이 되었는지, 개선할 점은 무엇인지 등을 솔직하게 작성해주시면 다른 분들에게 도움이 됩니다."
          />
        </S_Wrapper>
        <S_ButtonWrapper>
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
        </S_ButtonWrapper>
      </S_Container>
    </Modal>
  );
}

export default ReviewModal;

const S_Container = styled.form`
  display: flex;
  flex-direction: column;
  gap: 2.4rem;

  width: 100%;
  height: 100%;
`;

const S_Title = styled.p`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.LB3_R}
`;

const S_Description = styled.p`
  color: ${({ theme }) => theme.FONT.B04};
  ${({ theme }) => theme.TYPOGRAPHY.B3_R}
`;

const S_Separator = styled.div`
  border-bottom: 1px solid ${({ theme }) => theme.OUTLINE.DARK};
`;

const S_Wrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
`;

const S_Subtitle = styled.p`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.B3_R}
`;

const S_Textarea = styled.textarea`
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

const S_ButtonWrapper = styled.div`
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
`;
