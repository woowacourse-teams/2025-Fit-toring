import { css } from '@emotion/react';
import styled from '@emotion/styled';

import Button from '../../Button/Button';

interface ButtonSectionProps {
  onCancelButtonClick: () => void;
  submitButtonName: 'register' | 'update';
}

function ButtonSection({
  onCancelButtonClick,
  submitButtonName,
}: ButtonSectionProps) {
  return (
    <S_Container>
      <Button
        type="button"
        variant="secondary"
        onClick={onCancelButtonClick}
        size="full"
        customStyle={buttonStyle}
      >
        취소
      </Button>
      <Button type="submit" size="full" customStyle={buttonStyle}>
        {submitButtonName === 'register' ? '등록하기' : '수정하기'}
      </Button>
    </S_Container>
  );
}

export default ButtonSection;

const S_Container = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2rem;

  width: 100%;
  height: 100%;
`;

const buttonStyle = css`
  padding: 1.6rem 0;
`;
