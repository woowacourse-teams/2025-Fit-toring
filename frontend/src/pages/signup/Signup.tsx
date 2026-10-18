import styled from '@emotion/styled';

import SEO from '../../common/components/SEO/SEO';
import { PAGE_URL } from '../../common/constants/url';

import AuthFooter from './components/AuthFooter/AuthFooter';
import SignupForm from './components/SignupForm/SignupForm';
import SignupHeader from './components/SignupHeader/SignupHeader';
import SignupIntro from './components/SignupIntro/SignupIntro';

function Signup() {
  return (
    <S_Container>
      <SEO
        title="회원가입 - 핏토링 Fittoring"
        canonicalPath={PAGE_URL.SIGNUP}
      />
      <SignupHeader />
      <SignupIntro />
      <SignupForm />
      <AuthFooter currentPage="signup" />
    </S_Container>
  );
}

const S_Container = styled.div`
  padding-bottom: 3rem;

  background-color: ${({ theme }) => theme.BG.WHITE};
`;
export default Signup;
