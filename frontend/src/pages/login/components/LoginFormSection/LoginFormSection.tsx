import styled from '@emotion/styled';

import LoginForm from '../LoginForm/LoginForm';

function LoginFormSection() {
  return (
    <StyledContainer>
      <LoginForm />
    </StyledContainer>
  );
}

export default LoginFormSection;

const StyledContainer = styled.div`
  padding: 2.4rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 16px;

  background-color: ${({ theme }) => theme.BG.WHITE};
`;
