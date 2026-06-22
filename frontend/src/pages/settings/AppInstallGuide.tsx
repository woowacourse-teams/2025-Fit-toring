import { useState } from 'react';

import styled from '@emotion/styled';

import { InstallPromptContent } from '../../common/components/InstallPromptModal/InstallPromptModal';
import { IOSInstallGuideContent } from '../../common/components/IOSInstallGuideModal/IOSInstallGuideModal';
import usePWAInstall from '../../common/hooks/usePWAInstall';
import { isIOS } from '../../common/utils/deviceDetection';

function AppInstallGuide() {
  const [isInstalling, setIsInstalling] = useState(false);
  const { canInstall, hasInstalledBefore, promptInstall } = usePWAInstall();

  const handleInstallClick = async () => {
    if (isInstalling) {
      return;
    }

    if (hasInstalledBefore) {
      window.alert('이미 설치되었습니다.');
      return;
    }

    if (!canInstall) {
      window.alert('현재 브라우저에서는 앱 설치를 바로 실행할 수 없습니다.');
      return;
    }

    setIsInstalling(true);

    try {
      await promptInstall();
    } finally {
      setIsInstalling(false);
    }
  };

  return (
    <S_Container>
      <S_ContentSection>
        {isIOS() ? (
          <IOSInstallGuideContent />
        ) : (
          <InstallPromptContent
            onInstallClick={handleInstallClick}
            isInstalling={isInstalling}
          />
        )}
      </S_ContentSection>
    </S_Container>
  );
}

export default AppInstallGuide;

const S_Container = styled.main`
  width: 100%;

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const S_ContentSection = styled.section`
  display: flex;
  flex-direction: column;
  gap: 2rem;

  padding: 2rem;
`;
