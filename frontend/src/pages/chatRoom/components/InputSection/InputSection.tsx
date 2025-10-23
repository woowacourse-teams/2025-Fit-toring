import React from 'react';

import styled from '@emotion/styled';

import sendIcon from '../../../../common/assets/images/sendIcon.svg';

interface InputSectionProps {
  value: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onSubmit: (e: React.FormEvent<HTMLFormElement>) => void;
}

function InputSection({ value, onChange, onSubmit }: InputSectionProps) {
  return (
    <S_Container onSubmit={onSubmit}>
      <S_Input
        placeholder="메시지를 입력하세요"
        value={value}
        onChange={onChange}
      />
      <S_SendButton>
        <S_SendIcon src={sendIcon} alt="보내기 아이콘" />
      </S_SendButton>
    </S_Container>
  );
}

export default InputSection;

const S_Container = styled.form`
  display: flex;
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
  ${({ theme }) => theme.TYPOGRAPHY.B3_R};

  color: ${({ theme }) => theme.FONT.B01};

  :focus {
    outline: none;
  }

  ::placeholder {
    color: ${({ theme }) => theme.SYSTEM.GRAY200};
  }

  ${({ theme }) => theme.TYPOGRAPHY.B3_R};
`;

const S_SendButton = styled.button`
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
