import styled from '@emotion/styled';

import { StatusTypeEnum } from '../../../common/types/statusType';

import type { StatusType } from '../../../common/types/statusType';

interface ReviewWriteButtonProps {
  onClick: (event: React.MouseEvent<HTMLButtonElement>) => void;
  disabled: boolean;
}

interface ReviewButtonProps {
  isReviewed: boolean;
  status: StatusType;
  onReviewButtonClick: (event: React.MouseEvent<HTMLButtonElement>) => void;
  onReviewCompleteButtonClick: (
    event: React.MouseEvent<HTMLButtonElement>,
  ) => void;
}

function ReviewWriteButton({ onClick, disabled }: ReviewWriteButtonProps) {
  return (
    <S_ReviewWriteButton onClick={onClick} disabled={disabled}>
      리뷰 작성하기
    </S_ReviewWriteButton>
  );
}

function ReviewCompleteButton({
  onClick,
}: {
  onClick: (event: React.MouseEvent<HTMLButtonElement>) => void;
}) {
  return (
    <S_ReviewCompleteButton onClick={onClick}>
      내가 작성한 리뷰
    </S_ReviewCompleteButton>
  );
}

function ReviewButton({
  isReviewed,
  status,
  onReviewButtonClick,
  onReviewCompleteButtonClick,
}: ReviewButtonProps) {
  const canWriteReview = !isReviewed && status === StatusTypeEnum.COMPLETE;

  if (isReviewed) {
    return <ReviewCompleteButton onClick={onReviewCompleteButtonClick} />;
  }

  return (
    <ReviewWriteButton
      onClick={onReviewButtonClick}
      disabled={!canWriteReview}
    />
  );
}

export default ReviewButton;

const S_Container = styled.button`
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.4rem;

  width: 100%;
  height: 3.8rem;
  margin-left: auto;
  padding: 0.4rem 0.8rem;
  border: none;
  border-radius: 6px;

  transition: all 0.2s ease;

  cursor: pointer;

  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;

const S_ReviewWriteButton = styled(S_Container)<{ disabled: boolean }>`
  ${({ theme }) => theme.TYPOGRAPHY.BTN2_R}
  background-color: ${({ theme, disabled }) =>
    disabled ? theme.SYSTEM.GRAY400 : theme.BG.BLACK};

  color: ${({ theme }) => theme.FONT.W01};

  cursor: ${({ disabled }) => (disabled ? 'not-allowed' : 'pointer')};
`;

const S_ReviewCompleteButton = styled(S_Container)`
  background-color: ${({ theme }) => theme.BG.BLACK};

  color: ${({ theme }) => theme.FONT.W01};

  cursor: pointer;
`;
