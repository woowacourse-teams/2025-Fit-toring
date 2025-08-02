import styled from '@emotion/styled';

import TitleSeparator from '../TitleSeparator/TitleSeparator';

function CertificateSection() {
  return (
    <section>
      <TitleSeparator>검증된 자격 사항</TitleSeparator>
      <StyledGuideText>최대 3개까지 등록 가능합니다.</StyledGuideText>
    </section>
  );
}

export default CertificateSection;

const StyledGuideText = styled.p`
  margin-bottom: 2rem;
  padding-left: 0.5rem;

  ${({ theme }) => theme.TYPOGRAPHY.B4_R}
  color: ${({ theme }) => theme.FONT.B04}
`;
