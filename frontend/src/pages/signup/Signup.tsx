import styled from '@emotion/styled';

import AuthFooter from './components/AuthFooter/AuthFooter';
import SignupForm from './components/SignupForm/SignupForm';
import SignupHeader from './components/SignupHeader/SignupHeader';
import SignupIntro from './components/SignupIntro/SignupIntro';

function Signup() {
  return (
    <StyledContainer>
      <SignupHeader />
      <SignupIntro />
      <SignupForm />
      <AuthFooter currentPage="signup" />
    </StyledContainer>
  );
}

const StyledContainer = styled.div`
  padding-bottom: 3rem;

  background-color: ${({ theme }) => theme.BG.WHITE};
`;
export default Signup;
