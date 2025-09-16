import styled from '@emotion/styled';
import { Link } from 'react-router-dom';

const AUTH_TYPE = {
  signup: { url: '/login', text: '이미 계정이 있으신가요?', goPage: '로그인' },
  login: { url: '/signup', text: '계정이 없으신가요?', goPage: '회원가입' },
} as const;

function AuthFooter({ currentPage }: { currentPage: 'login' | 'signup' }) {
  return (
    <S_Container>
      <S_Divider>
        <S_Text>또는</S_Text>
      </S_Divider>
      <S_InfoText>
        {AUTH_TYPE[currentPage].text}
        <S_Link to={AUTH_TYPE[currentPage].url}>
          {AUTH_TYPE[currentPage].goPage}
        </S_Link>
      </S_InfoText>
    </S_Container>
  );
}

export default AuthFooter;

const S_Container = styled.div`
  display: flow-root;

  text-align: center;
`;

const S_Divider = styled.div`
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

const S_Text = styled.span`
  padding: 0 1.6rem;

  color: ${({ theme }) => theme.FONT.G01};
  ${({ theme }) => theme.TYPOGRAPHY.B2_R};
`;

const S_InfoText = styled.p`
  color: ${({ theme }) => theme.FONT.B04};
  ${({ theme }) => theme.TYPOGRAPHY.B2_R};
`;

const S_Link = styled(Link)`
  all: unset;

  margin-left: 0.4rem;

  color: ${({ theme }) => theme.SYSTEM.MAIN600};
  cursor: pointer;

  ${({ theme }) => theme.TYPOGRAPHY.B2_B};
`;
