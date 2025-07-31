import type { PropsWithChildren } from 'react';

import styled from '@emotion/styled';

interface FormFieldProps {
  label: string;
  errorMessage?: string;
  htmlFor: string;
}

function FormField({
  errorMessage,
  htmlFor,
  label,
  children,
}: PropsWithChildren<FormFieldProps>) {
  return (
    <StyledField>
      <StyledLabel htmlFor={htmlFor}>{label}</StyledLabel>
      {children}
      {errorMessage && <StyledErrorText>{errorMessage}</StyledErrorText>}
    </StyledField>
  );
}

export default FormField;
const StyledField = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.7rem;

  width: 100%;
`;

const StyledErrorText = styled.span`
  color: ${({ theme }) => theme.FONT.ERROR};
`;

const StyledLabel = styled.label`
  ${({ theme }) => theme.TYPOGRAPHY.B4_R};
  color: ${({ theme }) => theme.FONT.B02};
`;
