import styled from '@emotion/styled';

import MentoringApplicationStatus from '../../../../common/components/MentoringApplicationStatus/MentoringApplicationStatus';
import {
  StatusTypeEnum,
  type StatusType,
} from '../../../../common/types/statusType';
import useContentOverflowedRef from '../../hooks/useContentOverflowedRef';
import useShowMore from '../../hooks/useShowMore';
import ActionButtons from '../ActionButtons/ActionButtons';

import type { MentoringApplication } from '../../types/mentoringApplication';

interface MentoringApplicationItemProps {
  mentoringApplication: MentoringApplication;
  onActionButtonsClick: () => Promise<void>;
}

const STATUS_BAR_COLOR: Record<StatusType, string> = {
  [StatusTypeEnum.PENDING]: '#F59E0B',
  [StatusTypeEnum.APPROVED]: '#2BA79F',
  [StatusTypeEnum.COMPLETE]: '#94A3B8',
  [StatusTypeEnum.REJECTED]: '#EF4444',
};

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
      <S_StatusBar color={STATUS_BAR_COLOR[status]} />
      <S_SummaryWrapper>
        <S_NameLine>
          <S_Name>{menteeName}</S_Name>
          <S_NameSuffix>님</S_NameSuffix>
        </S_NameLine>
        <MentoringApplicationStatus status={status} />
      </S_SummaryWrapper>
      <S_CreatedAt>신청일 · {formatDate(createdAt)}</S_CreatedAt>
      <S_ApplicationContent showMore={showMore} ref={contentRef}>
        {content}
      </S_ApplicationContent>
      {contentOverflowed && (
        <S_ApplicationContentShowMoreButton onClick={handleShowMoreButtonClick}>
          ({showMore ? '접기' : '더보기'})
        </S_ApplicationContentShowMoreButton>
      )}
      <ActionButtons
        reservationId={reservationId}
        status={status}
        chatRoomId={chatRoomId}
        onClick={handleActionButtonsComplete}
      />
    </S_Container>
  );
}

export default MentoringApplicationItem;

const S_Container = styled.li`
  position: relative;

  display: flex;
  flex-direction: column;

  padding: 1.6rem 1.6rem 1.6rem 1.8rem;
  border: 1px solid ${({ theme }) => theme.SYSTEM.GRAY300};
  border-radius: 8px;
  overflow: hidden;

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const S_StatusBar = styled.div<{ color: string }>`
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 6px;

  background-color: ${({ color }) => color};
`;

const S_SummaryWrapper = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
`;

const S_NameLine = styled.div`
  display: flex;
  align-items: center;
  gap: 0.6rem;
`;

const S_Name = styled.h4`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.B2_B}
`;

const S_NameSuffix = styled.span`
  ${({ theme }) => theme.TYPOGRAPHY.B4_R}

  color: ${({ theme }) => theme.FONT.B04};
`;

const S_CreatedAt = styled.p`
  margin-top: 0.4rem;

  color: ${({ theme }) => theme.FONT.B04};
  ${({ theme }) => theme.TYPOGRAPHY.C4_R}
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
  margin-top: 0.8rem;
  word-break: break-all;

  color: ${({ theme }) => theme.FONT.B02};
  line-height: 1.45;

  ${({ theme }) => theme.TYPOGRAPHY.B4_R}
`;

const S_ApplicationContentShowMoreButton = styled.button`
  display: flex;
  align-self: flex-end;

  width: fit-content;
  padding: 0;
  border: none;

  background: none;

  color: ${({ theme }) => theme.FONT.B04};
  cursor: pointer;
  ${({ theme }) => theme.TYPOGRAPHY.B4_R}
`;
