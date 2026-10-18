import styled from '@emotion/styled';

import SEO from '../../common/components/SEO/SEO';
import { PAGE_URL } from '../../common/constants/url';

import MentoringCreateForm from './components/MentoringCreateForm/MentoringCreateForm';
import MentoringCreateHeader from './components/MentoringCreateHeader/MentoringCreateHeader';

function MentoringCreate() {
  return (
    <>
      <SEO
        title="멘토링 등록 - 핏토링 Fittoring"
        canonicalPath={PAGE_URL.MENTORING_CREATE}
      />
      <MentoringCreateHeader />
      <S_Wrapper>
        <MentoringCreateForm />
      </S_Wrapper>
    </>
  );
}

export default MentoringCreate;

const S_Wrapper = styled.div`
  padding: 3.2rem 1.6rem;
`;
