import styled from '@emotion/styled';

import { InstallPromptContent } from '../../common/components/InstallPromptModal/InstallPromptModal';
import { IOSInstallGuideContent } from '../../common/components/IOSInstallGuideModal/IOSInstallGuideModal';
import usePWAInstall from '../../common/hooks/usePWAInstall';
import { isIOS } from '../../common/utils/deviceDetection';

function AppInstallGuide() {
  const { promptInstall } = usePWAInstall();

  return (
    <S_Container>
      <S_ContentSection>
        {isIOS() ? (
          <IOSInstallGuideContent />
        ) : (
          <InstallPromptContent onInstallClick={promptInstall} />
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
