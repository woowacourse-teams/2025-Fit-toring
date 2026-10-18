import styled from '@emotion/styled';
import { useParams } from 'react-router-dom';

import SEO from '../../common/components/SEO/SEO';
import { PAGE_URL } from '../../common/constants/url';

import MentoringUpdateForm from './components/MentoringUpdateForm/MentoringUpdateForm';
import MentoringUpdateHeader from './components/MentoringUpdateHeader/MentoringUpdateHeader';

function MentoringUpdate() {
  const { mentoringId } = useParams();

  return (
    <>
      <SEO
        title="멘토링 수정 - 핏토링 Fittoring"
        canonicalPath={`${PAGE_URL.MENTORING_UPDATE}/${mentoringId}`}
      />
      <MentoringUpdateHeader />
      <S_Wrapper>
        <MentoringUpdateForm />
      </S_Wrapper>
    </>
  );
}

export default MentoringUpdate;

const S_Wrapper = styled.div`
  padding: 3.2rem 1.6rem;
`;
