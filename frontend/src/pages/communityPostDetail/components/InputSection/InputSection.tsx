import React, { useState } from 'react';

import styled from '@emotion/styled';

import InputWithSubmitButton from '../../../../common/components/InputWithSubmitButton/InputWithSubmitButton';

function InputSection() {
  const [comment, setComment] = useState('');

  const handleCommentChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setComment(e.target.value);
  };

  const handleCommentSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (comment.trim().length === 0) {
      return;
    }

    setComment('');
  };

  return (
    <S_Container>
      <InputWithSubmitButton
        value={comment}
        placeholder="댓글을 입력하세요"
        onChange={handleCommentChange}
        onSubmit={handleCommentSubmit}
      />
    </S_Container>
  );
}

export default InputSection;

const S_Container = styled.div`
  position: fixed;
  bottom: 72px;
  left: 50%;
  z-index: 1;

  width: 48rem;
  border-right: 1px solid ${({ theme }) => theme.SYSTEM.GRAY300};
  border-left: 1px solid ${({ theme }) => theme.SYSTEM.GRAY300};

  background-color: ${({ theme }) => theme.BG.WHITE};
  transform: translateX(-50%);

  @media screen and (width <= 480px) {
    width: 100%;
    border: none;
  }
`;
