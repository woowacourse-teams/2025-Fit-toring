import type { ComponentProps, PropsWithChildren } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';

import type { myTheme } from '../../types/theme';
import type { SerializedStyles } from '@emotion/react';

type Variant = 'primary' | 'secondary' | 'disabled';
type Size = 'full' | 'fit';

interface StyleVariant {
  primary: (theme: myTheme) => SerializedStyles;
  secondary: (theme: myTheme) => SerializedStyles;
  disabled: (theme: myTheme) => SerializedStyles;
}

interface ButtonProps {
  variant?: Variant;
  size?: Size;
  customStyle?: SerializedStyles;
}

function Button({
  onClick,
  variant = 'primary',
  size = 'fit',
  customStyle,
  children,
  ...rest
}: PropsWithChildren<ComponentProps<'button'> & ButtonProps>) {
  return (
    <StyledContainer
      customStyle={customStyle}
      variant={variant}
      size={size}
      onClick={onClick}
      {...rest}
    >
      {children}
    </StyledContainer>
  );
}

export default Button;

const StyledContainer = styled.button<ButtonProps>`
  width: ${(props) => (props.size === 'full' ? '100%' : 'fit-content')};
  padding: 0.6rem 1.1rem;
  border: none;
  border-radius: 0.7rem;
  cursor: pointer;

  font-size: 1.6rem;

  ${({ variant, theme }) => variant && styleVariant[variant](theme)};
  ${({ customStyle }) => customStyle};
`;

const styleVariant: StyleVariant = {
  primary: (theme: myTheme) => primaryStyles(theme),
  secondary: (theme: myTheme) => secondaryStyles(theme),
  disabled: (theme: myTheme) => disabledStyles(theme),
};

const primaryStyles = (theme: myTheme) => css`
  background-color: ${theme.SYSTEM.MAIN600};

  color: ${theme.FONT.W01};
`;

const secondaryStyles = (theme: myTheme) => css`
  border: 1px solid ${theme.OUTLINE.REGULAR};

  background-color: ${theme.BG.LIGHT};
`;

const disabledStyles = (theme: myTheme) => css`
  background-color: ${theme.BG.GRAY};

  color: ${theme.FONT.G01};
  pointer-events: none;
`;
