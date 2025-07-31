import styled from '@emotion/styled';

import BaseInfo from '../BaseInfo/BaseInfo';

function MentoringCreateForm() {
  return (
    <StyledContainer>
      <BaseInfo />
    </StyledContainer>
  );
}

export default MentoringCreateForm;

const StyledContainer = styled.form`
  display: flex;
  flex-direction: column;

  width: 100%;
  height: 100%;
  padding: 3.3rem;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  box-shadow: 0 4px 6px -1px rgb(0 0 0 / 10%);

  background: #fff;

  color: white;
`;
