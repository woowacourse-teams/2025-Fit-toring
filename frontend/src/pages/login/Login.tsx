import styled from '@emotion/styled';

import LoginFormSection from './components/LoginFormSection/LoginFormSection';
import LoginHeader from './components/LoginHeader/LoginHeader';
import LoginIntro from './components/LoginIntro/LoginIntro';

function Login() {
  return (
    <>
      <LoginHeader />
      <S_Wrapper>
        <LoginIntro />
        <LoginFormSection />
      </S_Wrapper>
    </>
  );
}

export default Login;

const S_Wrapper = styled.div`
  padding: 0 1.9rem;
`;
