import styled from '@emotion/styled';

import Button from '../Button/Button';
import Modal from '../Modal/Modal';

interface DeleteConfirmModalProps {
  opened: boolean;
  title: string;
  description: string;
  onCloseClick: () => void;
  onConfirmClick: () => void;
  confirmLabel?: string;
  cancelLabel?: string;
}

function DeleteConfirmModal({
  opened,
  title,
  description,
  onCloseClick,
  onConfirmClick,
  confirmLabel = '예',
  cancelLabel = '아니오',
}: DeleteConfirmModalProps) {
  return (
    <Modal opened={opened} onCloseClick={onCloseClick}>
      <S_Container>
        <S_Title>{title}</S_Title>
        <S_Description>{description}</S_Description>
        <S_ButtonWrapper>
          <S_ActionButton
            type="button"
            variant="secondary"
            onClick={onCloseClick}
          >
            {cancelLabel}
          </S_ActionButton>
          <S_ActionButton type="button" onClick={onConfirmClick}>
            {confirmLabel}
          </S_ActionButton>
        </S_ButtonWrapper>
      </S_Container>
    </Modal>
  );
}

export default DeleteConfirmModal;

const S_Container = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1.6rem;
`;

const S_Title = styled.h2`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.H4_SB}
`;

const S_Description = styled.p`
  color: ${({ theme }) => theme.FONT.B03};
  ${({ theme }) => theme.TYPOGRAPHY.B3_R}
`;

const S_ButtonWrapper = styled.div`
  display: flex;
  justify-content: flex-end;
  gap: 0.8rem;
`;

const S_ActionButton = styled(Button)`
  min-width: 7.2rem;

  ${({ theme }) => theme.TYPOGRAPHY.C3_R}
`;
