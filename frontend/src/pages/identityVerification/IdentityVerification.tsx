import styled from '@emotion/styled';

import IdentityVerificationForm from './components/IdentityVerificationForm/IdentityVerificationForm';
import IdentityVerificationHeader from './components/IdentityVerificationHeader/IdentityVerificationHeader';
import IdentityVerificationIntro from './components/IdentityVerificationIntro/IdentityVerificationIntro';

function IdentityVerification() {
  return (
    <S_Container>
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
