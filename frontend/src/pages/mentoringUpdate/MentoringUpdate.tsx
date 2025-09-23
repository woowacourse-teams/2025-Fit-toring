import styled from '@emotion/styled';

import MentoringUpdateForm from './components/MentoringUpdateForm/MentoringUpdateForm';
import MentoringUpdateHeader from './components/MentoringUpdateHeader/MentoringUpdateHeader';

function MentoringUpdate() {
  return (
    <>
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
