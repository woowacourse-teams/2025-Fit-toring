import type { PropsWithChildren } from 'react';

import styled from '@emotion/styled';

interface FormFieldProps {
  label: string;
  errorMessage?: string;
}

function FormField({
  errorMessage,
  label,
  children,
}: PropsWithChildren<FormFieldProps>) {
  return (
    <StyledField>
      <StyledLabel>
        {label}
        {children}
      </StyledLabel>
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
  display: flex;
  flex-direction: column;
  gap: 1rem;
  color: ${({ theme }) => theme.FONT.B02};
  ${({ theme }) => theme.TYPOGRAPHY.B4_R};
`;
