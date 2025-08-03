import styled from '@emotion/styled';

import BaseInfoSection from '../BaseInfoSection/BaseInfoSection';
import ButtonSection from '../ButtonSection/ButtonSection';
import CertificateSection from '../CertificateSection/CertificateSection';
import DetailIntroduce from '../DetailIntroduce/DetailIntroduce';
import IntroduceSection from '../IntroduceSection/IntroduceSection';
import ProfileSection from '../ProfileSection/ProfileSection';
import SpecialtySection from '../SpecialtySection/SpecialtySection';

function MentoringCreateForm() {
  return (
    <StyledContainer>
      <BaseInfoSection />
      <ProfileSection />
      <SpecialtySection />
      <IntroduceSection />
      <CertificateSection />
      <DetailIntroduce />
      <StyledSeparator />
      <ButtonSection />
    </StyledContainer>
  );
}

export default MentoringCreateForm;

const StyledContainer = styled.form`
  display: flex;
  flex-direction: column;
  gap: 3.2rem;

  width: 100%;
  height: 100%;
  padding: 3.3rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 16px;
  box-shadow: 0 4px 6px -1px rgb(0 0 0 / 10%);

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const StyledSeparator = styled.div`
  width: 100%;
  height: 0.1rem;

  background-color: ${({ theme }) => theme.OUTLINE.REGULAR};
`;
