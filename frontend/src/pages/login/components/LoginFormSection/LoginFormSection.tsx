import styled from '@emotion/styled';

import AuthFooter from '../../../signup/components/AuthFooter/AuthFooter';
import LoginForm from '../LoginForm/LoginForm';

function LoginFormSection() {
  return (
    <StyledContainer>
      <LoginForm />
      <AuthFooter currentPage="login" />
    </StyledContainer>
  );
}

export default LoginFormSection;

const StyledContainer = styled.div`
  padding: 2.4rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 16px;

  background-color: ${({ theme }) => theme.BG.WHITE};
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1);
`;
