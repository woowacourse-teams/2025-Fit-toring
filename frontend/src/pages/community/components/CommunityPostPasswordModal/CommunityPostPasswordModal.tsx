import { useState, type ChangeEvent } from 'react';

import styled from '@emotion/styled';

import Button from '../../../../common/components/Button/Button';
import Modal from '../../../../common/components/Modal/Modal';

interface CommunityPostPasswordModalProps {
  opened: boolean;
  onCloseClick: () => void;
  onConfirmClick: (password: string) => void;
  title?: string;
  description?: string;
  confirmLabel?: string;
}

function CommunityPostPasswordModal({
  opened,
  onCloseClick,
  onConfirmClick,
  title = '비밀번호 확인',
  description = '게시글 작성 시 설정한 비밀번호를 입력해주세요.',
  confirmLabel = '확인',
}: CommunityPostPasswordModalProps) {
  const [password, setPassword] = useState('');

  const handlePasswordChange = (e: ChangeEvent<HTMLInputElement>) => {
    setPassword(e.target.value);
  };

  const handleCloseClick = () => {
    setPassword('');
    onCloseClick();
  };

  const handleConfirmClick = () => {
    if (!password.trim()) {
      return;
    }

    onConfirmClick(password.trim());
    setPassword('');
  };

  return (
    <Modal opened={opened} onCloseClick={handleCloseClick}>
      <S_Container>
        <S_Title>{title}</S_Title>
        <S_Description>{description}</S_Description>
        <S_PasswordInput
          type="password"
          value={password}
          placeholder="비밀번호를 입력해주세요."
          onChange={handlePasswordChange}
        />
        <S_ButtonWrapper>
          <S_ActionButton
            type="button"
            variant="secondary"
            onClick={handleCloseClick}
          >
            취소
          </S_ActionButton>
          <S_ActionButton
            type="button"
            variant={password.trim() ? 'primary' : 'disabled'}
            onClick={handleConfirmClick}
          >
            {confirmLabel}
          </S_ActionButton>
        </S_ButtonWrapper>
      </S_Container>
    </Modal>
  );
}

export default CommunityPostPasswordModal;

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

const S_PasswordInput = styled.input`
  width: 100%;
  height: 4.4rem;
  padding: 0 1.3rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 1.2rem;

  background-color: ${({ theme }) => theme.BG.WHITE};

  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.B3_R};

  &::placeholder {
    color: ${({ theme }) => theme.SYSTEM.GRAY300};
  }

  &:focus {
    outline: none;
    border-color: ${({ theme }) => theme.SYSTEM.MAIN500};
  }
`;

const S_ButtonWrapper = styled.div`
  display: flex;
  justify-content: flex-end;
  gap: 0.8rem;
`;

const S_ActionButton = styled(Button)`
  min-width: 7.2rem;

  ${({ theme }) => theme.TYPOGRAPHY.C4_R}
`;
