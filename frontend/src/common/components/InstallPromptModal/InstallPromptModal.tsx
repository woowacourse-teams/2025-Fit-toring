import { useState } from 'react';

import styled from '@emotion/styled';

import closeIcon from '../../assets/images/closeBlack.svg';
import fittoringIconWithBg from '../../assets/images/fittoringIconWithBg.png';
import Modal from '../Modal/Modal';

interface InstallPromptModalProps {
  opened: boolean;
  onCloseClick: () => void;
  onInstallClick: () => Promise<void>;
  onLaterClick?: () => void;
}

function InstallPromptModal({
  opened,
  onCloseClick,
  onInstallClick,
  onLaterClick,
}: InstallPromptModalProps) {
  const [isInstalling, setIsInstalling] = useState(false);

  const handleInstallClick = async () => {
    if (isInstalling) {
      return;
    }

    setIsInstalling(true);

    try {
      await onInstallClick();
      onCloseClick();
    } finally {
      setIsInstalling(false);
    }
  };

  const handleLaterClick = onLaterClick ?? onCloseClick;

  return (
    <Modal opened={opened} onCloseClick={onCloseClick}>
      <S_Container>
        <S_CloseButton type="button" onClick={onCloseClick} aria-label="닫기">
          <S_CloseIcon src={closeIcon} alt="" aria-hidden="true" />
        </S_CloseButton>

        <S_Header>
          <S_IconBox aria-hidden="true">
            <S_AppIcon src={fittoringIconWithBg} alt="" aria-hidden="true" />
          </S_IconBox>

          <S_Title>
            홈 화면에 <S_TitleStrong>핏토링 앱</S_TitleStrong>을 추가하고
            <br />더 편하게 이용해보세요.
          </S_Title>
        </S_Header>

        <S_ButtonsWrapper>
          <S_Button
            type="button"
            onClick={handleInstallClick}
            disabled={isInstalling}
          >
            {isInstalling ? '설치 중...' : '설치하기'}
          </S_Button>

          <S_LaterButton type="button" onClick={handleLaterClick}>
            다음에 할래요
          </S_LaterButton>
        </S_ButtonsWrapper>
      </S_Container>
    </Modal>
  );
}

export default InstallPromptModal;

const S_Container = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2rem;
  position: relative;

  padding: 0.8rem 0.2rem 0;

  box-sizing: border-box;
`;

const S_CloseButton = styled.button`
  display: inline-flex;
  align-items: center;
  justify-content: center;
  position: absolute;
  top: 0;
  right: 0;

  padding: 0.4rem;
  border: none;

  background: transparent;
  cursor: pointer;
`;

const S_CloseIcon = styled.img`
  width: 1.5rem;
  height: 1.5rem;
`;

const S_Header = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.6rem;

  padding-top: 1.8rem;

  text-align: center;
`;

const S_IconBox = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;

  width: 8rem;
  height: 8rem;
  border: 1px solid ${({ theme }) => theme.SYSTEM.GRAY100};
  border-radius: 2rem;
`;

const S_AppIcon = styled.img`
  width: 100%;
  height: 100%;
  object-fit: cover;
`;

const S_Title = styled.h2`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.H4_R}
  line-height: 1.45;
`;

const S_TitleStrong = styled.span`
  ${({ theme }) => theme.TYPOGRAPHY.H4_B}
`;

const S_ButtonsWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2rem;
`;

const S_Button = styled.button`
  width: 85%;
  padding: 1.4rem 1.6rem;
  border: none;
  border-radius: 999px;
  box-shadow: 0 0.8rem 1.8rem rgb(0 120 111 / 14%);

  background-color: ${({ theme }) => theme.SYSTEM.MAIN500};

  color: ${({ theme }) => theme.FONT.W01};
  ${({ theme }) => theme.TYPOGRAPHY.B1_B}
  cursor: pointer;

  &:disabled {
    cursor: default;
    opacity: 0.75;
  }
`;

const S_LaterButton = styled.button`
  align-self: center;

  border: none;

  background: transparent;

  color: ${({ theme }) => theme.FONT.G01};
  ${({ theme }) => theme.TYPOGRAPHY.B4_R}
  cursor: pointer;
  text-decoration: underline;
  text-underline-offset: 0.3rem;
`;
