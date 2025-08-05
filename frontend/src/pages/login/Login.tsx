import styled from '@emotion/styled';

import LoginFormSection from './components/LoginFormSection/LoginFormSection';
import LoginHeader from './components/LoginHeader/LoginHeader';
import LoginIntro from './components/LoginIntro/LoginIntro';

function Login() {
  return (
    <>
      <LoginHeader />
      <StyledWrapper>
        <LoginIntro />
        <LoginFormSection />
      </StyledWrapper>
    </>
  );
}

export default Login;

const StyledWrapper = styled.div`
  padding: 0 1.9rem;
`;
