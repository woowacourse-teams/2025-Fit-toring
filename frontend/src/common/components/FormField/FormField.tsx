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
    <S_Field>
      <S_Label>
        {label}
        {children}
      </S_Label>
      {errorMessage && <S_ErrorText>{errorMessage}</S_ErrorText>}
    </S_Field>
  );
}

export default FormField;

const S_Field = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.7rem;

  width: 100%;
`;

const S_ErrorText = styled.span`
  color: ${({ theme }) => theme.FONT.ERROR};

  ${({ theme }) => theme.TYPOGRAPHY.B4_R};
`;

const S_Label = styled.label`
  display: flex;
  flex-direction: column;
  gap: 1rem;

  color: ${({ theme }) => theme.FONT.B02};
  ${({ theme }) => theme.TYPOGRAPHY.B4_R};
`;
