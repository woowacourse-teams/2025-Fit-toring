import styled from '@emotion/styled';

import MentoringCreateForm from './components/MentoringCreateForm/MentoringCreateForm';
import MentoringCreateHeader from './components/MentoringCreateHeader/MentoringCreateHeader';

function MentoringCreate() {
  return (
    <>
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
