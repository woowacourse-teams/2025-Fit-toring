import styled from '@emotion/styled';

import MentoringCreateForm from './components/MentoringCreateForm/MentoringCreateForm';
import MentoringCreateHeader from './components/MentoringCreateHeader/MentoringCreateHeader';

function MentoringCreate() {
  return (
    <>
      <MentoringCreateHeader />
      <StyledWrapper>
        <MentoringCreateForm />
      </StyledWrapper>
    </>
  );
}

export default MentoringCreate;

const StyledWrapper = styled.div`
  padding: 3.2rem 1.6rem;
`;
