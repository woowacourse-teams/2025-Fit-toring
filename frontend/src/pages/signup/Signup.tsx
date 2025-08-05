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
  background-color: ${({ theme }) => theme.BG.WHITE};
  padding-bottom: 3rem;
`;
export default Signup;
