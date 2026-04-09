import React, { useState } from 'react';

import InputWithSubmitButton from '../../../../common/components/InputWithSubmitButton/InputWithSubmitButton';

function InputSection() {
  const [comment, setComment] = useState('');

  const handleCommentChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setComment(e.target.value);
  };

  const handleCommentSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    if (comment.trim().length === 0) {
      return;
    }

    e.preventDefault();
    setComment('');
  };

  return (
    <InputWithSubmitButton
      value={comment}
      placeholder="댓글을 입력하세요"
      onChange={handleCommentChange}
      onSubmit={handleCommentSubmit}
    />
  );
}

export default InputSection;
