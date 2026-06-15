import styled from '@emotion/styled';

import { InstallPromptContent } from '../../common/components/InstallPromptModal/InstallPromptModal';
import { IOSInstallGuideContent } from '../../common/components/IOSInstallGuideModal/IOSInstallGuideModal';
import usePWAInstall from '../../common/hooks/usePWAInstall';
import { isIOS, isPWAStandalone } from '../../common/utils/deviceDetection';

function AppInstallGuide() {
  const { canInstall, promptInstall } = usePWAInstall();

  if (isPWAStandalone()) {
    return (
      <S_Container>
        <S_InstalledSection>
          <S_InstalledText>이미 설치됨</S_InstalledText>
        </S_InstalledSection>
      </S_Container>
    );
  }

  return (
    <S_Container>
      <S_ContentSection>
        {isIOS() ? (
          <IOSInstallGuideContent />
        ) : (
          <InstallPromptContent
            installDisabled={!canInstall}
            onInstallClick={promptInstall}
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

const S_InstalledSection = styled.section`
  padding: 2rem;
`;

const S_InstalledText = styled.p`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.B3_R}
`;
