import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import { PAGE_URL } from '../../../../common/constants/url';
import {
  StatusTypeEnum,
  type StatusType,
} from '../../../../common/types/statusType';
import useReservationApprove from '../../hooks/useReservationApprove';
import useReservationComplete from '../../hooks/useReservationComplete';
import useReservationReject from '../../hooks/useReservationReject';

interface ActionButtonsProps {
  reservationId: number;
  status: StatusType;
  chatRoomId: number | null;
  onClick: () => Promise<void>;
}

type ButtonVariant = 'solid' | 'solidDark' | 'outline' | 'outlineDanger';

function ActionButtons({
  reservationId,
  status,
  chatRoomId,
  onClick,
}: ActionButtonsProps) {
  const navigate = useNavigate();

  const { mutate: approveMutate } = useReservationApprove(onClick);

  const handleApproveButtonClick = async () => {
    if (
      confirm('한번 승인한 후에는 취소할 수 없습니다. 정말 승인하시겠습니까?')
    ) {
      approveMutate(reservationId);
    }
  };

  const { mutate: rejectMutate } = useReservationReject(onClick);

  const handleRejectedButtonClick = async () => {
    if (
      confirm('한번 거절한 후에는 취소할 수 없습니다. 정말 거절하시겠습니까?')
    ) {
      rejectMutate(reservationId);
    }
  };

  const { mutate: completeMutate } = useReservationComplete(onClick);

  const handleCompleteButtonClick = async () => {
    if (
      confirm('한번 완료한 후에는 취소할 수 없습니다. 정말 완료하시겠습니까?')
    ) {
      completeMutate(reservationId);
    }
  };

  const handleChatButtonClick = async () => {
    navigate(`${PAGE_URL.CHAT_ROOM}/${chatRoomId}`);
  };

  if (status === StatusTypeEnum.PENDING) {
    return (
      <S_Container>
        <S_Button variant="outlineDanger" onClick={handleRejectedButtonClick}>
          거절
        </S_Button>
        <S_Button variant="solid" onClick={handleApproveButtonClick}>
          승인
        </S_Button>
      </S_Container>
    );
  }
  if (status === StatusTypeEnum.APPROVED) {
    return (
      <S_Container>
        <S_Button variant="outline" onClick={handleCompleteButtonClick}>
          완료 처리
        </S_Button>
        <S_Button variant="solidDark" onClick={handleChatButtonClick}>
          채팅방으로 이동
        </S_Button>
      </S_Container>
    );
  }
  if (status === StatusTypeEnum.COMPLETE) {
    return (
      <S_Container>
        <S_Button variant="solidDark" onClick={handleChatButtonClick}>
          채팅방으로 이동
        </S_Button>
      </S_Container>
    );
  }
}

export default ActionButtons;

const S_Container = styled.div`
  display: flex;
  gap: 0.8rem;

  width: 100%;
  margin-top: 1.2rem;
`;

const S_Button = styled.button<{ variant: ButtonVariant }>`
  flex: 1;

  height: 4.2rem;
  border-radius: 7px;

  cursor: pointer;

  ${({ theme }) => theme.TYPOGRAPHY.BTN4_SB}

  ${({ theme, variant }) => {
    switch (variant) {
      case 'solid':
        return `
          background-color: ${theme.SYSTEM.MAIN500};
          color: ${theme.FONT.W01};
          border: none;
        `;
      case 'solidDark':
        return `
          background-color: ${theme.BG.BLACK};
          color: ${theme.FONT.W01};
          border: none;
        `;
      case 'outline':
        return `
          background-color: ${theme.BG.WHITE};
          color: ${theme.FONT.B01};
          border: 1px solid ${theme.OUTLINE.DARK};
        `;
      case 'outlineDanger':
        return `
          background-color: ${theme.BG.WHITE};
          color: #B91C1C;
          border: 1px solid #FCA5A5;
        `;
    }
  }}
`;
