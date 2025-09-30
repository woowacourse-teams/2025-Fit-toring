import styled from '@emotion/styled';
import IdentityVerificationHeader from './components/IdentityVerificationHeader/IdentityVerificationHeader';

function IdentityVerification() {
  return (
    <S_Container>
      <IdentityVerificationHeader />
    </S_Container>
  );
}

const S_Container = styled.div`
  padding-bottom: 3rem;

  background-color: ${({ theme }) => theme.BG.WHITE};
`;
export default IdentityVerification;
