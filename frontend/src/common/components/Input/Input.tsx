import type { ComponentPropsWithRef } from 'react';

import styled from '@emotion/styled';

import type { SerializedStyles } from '@emotion/react';

interface InputProps {
  customStyle?: SerializedStyles;
  errored?: boolean;
}

function Input({
  customStyle,
  ...rest
}: ComponentPropsWithRef<'input'> & InputProps) {
  return <Container customStyle={customStyle} {...rest} />;
}

export default Input;

const Container = styled.input<InputProps>`
  width: 100%;
  padding: 0.7rem 1.1rem;
  border: ${({ theme, errored }) =>
      errored ? theme.FONT.ERROR : theme.BORDER.GRAY300}
    0.1rem solid;

  border-radius: 0.7rem;

  :focus {
    outline: none;
  }

  ${({ theme }) => theme.TYPOGRAPHY.B2_R};
  ${({ customStyle }) => customStyle};
`;
