import React from 'react';

import styled from '@emotion/styled';

import sendIcon from '../../assets/images/sendIcon.svg';

interface InputWithSubmitButtonProps {
  value: string;
  placeholder: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onSubmit: (e: React.FormEvent<HTMLFormElement>) => void;
}

function InputWithSubmitButton({
  value,
  placeholder,
  onChange,
  onSubmit,
}: InputWithSubmitButtonProps) {
  return (
    <S_Container onSubmit={onSubmit}>
      <S_Input placeholder={placeholder} value={value} onChange={onChange} />
      <S_SubmitButton type="submit">
        <S_SendIcon src={sendIcon} alt="전송 아이콘" />
      </S_SubmitButton>
    </S_Container>
  );
}

export default InputWithSubmitButton;

const S_Container = styled.form`
  display: flex;
  flex: 1;
  gap: 1rem;

  padding: 1.6rem;
  border-top: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const S_Input = styled.input`
  width: 100%;
  height: 3.6rem;
  padding: 1rem 1.3rem;
  border: none;
  border-radius: 50px;

  background-color: ${({ theme }) => theme.SYSTEM.GRAY50};

  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.B3_R};

  :focus {
    outline: none;
  }

  ::placeholder {
    color: ${({ theme }) => theme.SYSTEM.GRAY200};
  }
`;

const S_SubmitButton = styled.button`
  display: flex;
  align-items: center;
  justify-content: center;

  width: 3.6rem;
  height: 3.6rem;
  padding: 0;
  border: none;
  border-radius: 50%;

  cursor: pointer;

  background-color: ${({ theme }) => theme.SYSTEM.GRAY400};
  aspect-ratio: 1 / 1;
`;

const S_SendIcon = styled.img`
  width: 1.6rem;
  height: 1.6rem;
`;
