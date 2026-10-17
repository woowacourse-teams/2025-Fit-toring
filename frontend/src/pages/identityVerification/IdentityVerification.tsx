import styled from '@emotion/styled';

import SEO from '../../common/components/SEO/SEO';
import { PAGE_URL } from '../../common/constants/url';

import IdentityVerificationForm from './components/IdentityVerificationForm/IdentityVerificationForm';
import IdentityVerificationHeader from './components/IdentityVerificationHeader/IdentityVerificationHeader';
import IdentityVerificationIntro from './components/IdentityVerificationIntro/IdentityVerificationIntro';

function IdentityVerification() {
  return (
    <S_Container>
      <SEO
        title="본인인증 - 핏토링 Fittoring"
        canonicalPath={PAGE_URL.IDENTITY_VERIFICATION}
      />
      <IdentityVerificationHeader />
      <IdentityVerificationIntro />
      <IdentityVerificationForm />
    </S_Container>
  );
}

const S_Container = styled.div`
  padding-bottom: 3rem;

  background-color: ${({ theme }) => theme.BG.WHITE};
`;
export default IdentityVerification;
