import styled from '@emotion/styled';

import AuthFooter from '../../../signup/components/AuthFooter/AuthFooter';
import LoginForm from '../LoginForm/LoginForm';

function LoginFormSection() {
  return (
    <S_Container>
      <LoginForm />
      <AuthFooter currentPage="login" />
    </S_Container>
  );
}

export default LoginFormSection;

const S_Container = styled.div`
  padding: 2.4rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 16px;
  box-shadow: 0 10px 25px -5px rgb(0 0 0 / 10%);

  background-color: ${({ theme }) => theme.BG.WHITE};
`;
