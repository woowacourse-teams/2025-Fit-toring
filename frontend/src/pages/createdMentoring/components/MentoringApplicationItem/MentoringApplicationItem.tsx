import styled from '@emotion/styled';

import MentoringApplicationStatus from '../../../../common/components/MentoringApplicationStatus/MentoringApplicationStatus';
import useContentOverflowedRef from '../../hooks/useContentOverflowedRef';
import useShowMore from '../../hooks/useShowMore';
import ActionButtons from '../ActionButtons/ActionButtons';

import type { MentoringApplication } from '../../types/mentoringApplication';

interface MentoringApplicationItemProps {
  mentoringApplication: MentoringApplication;
  onActionButtonsClick: () => Promise<void>;
}

const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  const fullDate = date.toISOString().split('T')[0];

  return fullDate;
};

function MentoringApplicationItem({
  mentoringApplication: {
    reservationId,
    menteeName,
    content,
    status,
    createdAt,
    chatRoomId,
  },
  onActionButtonsClick,
}: MentoringApplicationItemProps) {
  const { showMore, toggleShowMore: handleShowMoreButtonClick } = useShowMore();

  const { contentOverflowed, setRef: contentRef } = useContentOverflowedRef();

  const handleActionButtonsComplete = async () => {
    await onActionButtonsClick();
  };

  return (
    <S_Container key={reservationId}>
      <S_SummaryWrapper>
        <S_Name>{menteeName} 님</S_Name>
        <MentoringApplicationStatus status={status} />
      </S_SummaryWrapper>
      <S_ApplicationInfoWrapper>
        <S_CreatedAt>신청일: {formatDate(createdAt)}</S_CreatedAt>
      </S_ApplicationInfoWrapper>
      <S_ApplicationContent showMore={showMore} ref={contentRef}>
        {content}
      </S_ApplicationContent>
      {contentOverflowed && (
        <S_ApplicationContentShowMoreButton onClick={handleShowMoreButtonClick}>
          ({showMore ? '접기' : '더보기'})
        </S_ApplicationContentShowMoreButton>
      )}
      <S_ButtonWrapper>
        <ActionButtons
          reservationId={reservationId}
          status={status}
          chatRoomId={chatRoomId}
          onClick={handleActionButtonsComplete}
        />
      </S_ButtonWrapper>
    </S_Container>
  );
}

export default MentoringApplicationItem;

const S_Container = styled.li`
  display: flex;
  flex-direction: column;
  gap: 1rem;

  height: auto;
  padding: 1.8rem 2rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 5px;

  transition: all 0.2s ease;

  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;

const S_SummaryWrapper = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
`;

const S_Name = styled.h4`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.LB3_B}
`;

const S_ApplicationInfoWrapper = styled.div`
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 1rem;
`;

const S_CreatedAt = styled.p`
  color: ${({ theme }) => theme.SYSTEM.GRAY500};
  ${({ theme }) => theme.TYPOGRAPHY.B3_R}
`;

const S_ApplicationContent = styled.p<{ showMore: boolean }>`
  ${({ showMore }) =>
    !showMore &&
    `
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  `}
  width: 100%;
  word-break: break-all;

  color: ${({ theme }) => theme.FONT.B01};

  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;

const S_ApplicationContentShowMoreButton = styled.button`
  display: flex;
  align-self: flex-end;

  width: fit-content;
  padding: 0;
  border: none;

  background: none;

  color: ${({ theme }) => theme.SYSTEM.GRAY500};
  cursor: pointer;
  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;

const S_ButtonWrapper = styled.div`
  display: flex;
  justify-content: flex-end;
`;
