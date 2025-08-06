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
  height: 100%;
  padding: 0.7rem 1.1rem;
  border: ${({ theme, errored }) =>
      errored ? theme.FONT.ERROR : theme.OUTLINE.DARK}
    1px solid;
  border-radius: 7px;

  color: ${({ theme }) => theme.FONT.B01};

  :focus {
    outline: none;
    border: 2px solid ${({ theme }) => theme.SYSTEM.MAIN600};
  }

  ::placeholder {
    color: ${({ theme }) => theme.SYSTEM.GRAY200};
  }

  ::placeholder {
    color: ${({ theme }) => theme.SYSTEM.GRAY200};
  }

  ${({ theme }) => theme.TYPOGRAPHY.B2_R};
  ${({ customStyle }) => customStyle};
`;
