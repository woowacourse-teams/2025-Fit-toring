import styled from '@emotion/styled';

import Button from '../Button/Button';
import Modal from '../Modal/Modal';

interface NotificationPermissionModalProps {
  isOpen: boolean;
  onAllow: () => void;
  onClose: () => void;
}

function NotificationPermissionModal({
  isOpen,
  onAllow,
  onClose,
}: NotificationPermissionModalProps) {
  return (
    <Modal opened={isOpen} onCloseClick={onClose}>
      <S_ModalContent>
        <S_Title>채팅 알림 받기</S_Title>

        <S_Description>
          새로운 메시지가 도착하면
          <br />
          알림으로 바로 확인할 수 있어요
        </S_Description>

        <S_ButtonGroup>
          <Button type="button" onClick={onAllow} variant="primary" size="full">
            알림 허용하기
          </Button>

          <S_SkipButton onClick={onClose}>나중에 하기</S_SkipButton>
        </S_ButtonGroup>
      </S_ModalContent>
    </Modal>
  );
}

export default NotificationPermissionModal;

const S_ModalContent = styled.div`
  text-align: center;
`;

const S_Title = styled.h2`
  margin-bottom: 12px;

  color: ${({ theme }) => theme.FONT.B01};
  font-weight: 700;
  font-size: 20px;
`;

const S_Description = styled.p`
  margin-bottom: 24px;

  color: ${({ theme }) => theme.FONT.G01};
  font-size: 15px;
  line-height: 1.5;
`;

const S_ButtonGroup = styled.div`
  display: flex;
  flex-direction: column;
  gap: 12px;
`;

const S_SkipButton = styled.button`
  padding: 8px;
  border: none;

  background: none;

  color: ${({ theme }) => theme.FONT.B02};
  font-size: 14px;
  cursor: pointer;
  transition: color 0.2s;

  &:disabled {
    cursor: not-allowed;
    opacity: 0.5;
  }

  &:hover:not(:disabled) {
    color: ${({ theme }) => theme.FONT.B01};
  }
`;
