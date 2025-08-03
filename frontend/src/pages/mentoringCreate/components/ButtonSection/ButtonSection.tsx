import { css } from '@emotion/react';
import styled from '@emotion/styled';

import Button from '../../../../common/components/Button/Button';

function ButtonSection() {
  return (
    <StyledContainer>
      <Button
        type="submit"
        variant="secondary"
        size="full"
        customStyle={css`
          padding: 1.6rem 0;
        `}
      >
        취소
      </Button>
      <Button
        type="submit"
        size="full"
        customStyle={css`
          padding: 1.6rem 0;
        `}
      >
        등록하기
      </Button>
    </StyledContainer>
  );
}

export default ButtonSection;

const StyledContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2rem;

  width: 100%;
  height: 100%;
`;
