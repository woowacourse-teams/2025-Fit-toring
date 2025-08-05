import styled from '@emotion/styled';
import { Link } from 'react-router-dom';

const AUTH_TYPE = {
  signup: { url: '/login', text: '이미 계정이 있으신가요?', goPage: '로그인' },
  login: { url: '/signup', text: '계정이 없으신가요?', goPage: '회원가입' },
};

function AuthFooter({ currentPage }: { currentPage: 'login' | 'signup' }) {
  return (
    <StyledContainer>
      <StyledDivider>
        <StyledText>또는</StyledText>
      </StyledDivider>
      <StyledInfoText>
        {AUTH_TYPE[currentPage].text}
        <StyledLink to={AUTH_TYPE[currentPage].url}>
          {AUTH_TYPE[currentPage].goPage}
        </StyledLink>
      </StyledInfoText>
    </StyledContainer>
  );
}

export default AuthFooter;

const StyledContainer = styled.div`
  display: flow-root;
  text-align: center;
`;

const StyledDivider = styled.div`
  display: flex;
  align-items: center;
  margin: 3rem 0;

  &::before,
  &::after {
    content: '';
    flex: 1;
    height: 1px;
    background-color: ${({ theme }) => theme.OUTLINE.REGULAR};
  }
`;

const StyledText = styled.span`
  padding: 0 1.6rem;
  color: ${({ theme }) => theme.FONT.G01};
  ${({ theme }) => theme.TYPOGRAPHY.B2_R};
`;

const StyledInfoText = styled.p`
  color: ${({ theme }) => theme.FONT.B04};
  ${({ theme }) => theme.TYPOGRAPHY.B2_R};
`;

const StyledLink = styled(Link)`
  all: unset;
  color: ${({ theme }) => theme.SYSTEM.MAIN600};
  cursor: pointer;

  margin-left: 0.4rem;

  ${({ theme }) => theme.TYPOGRAPHY.B2_B};
`;
