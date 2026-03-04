import React, { useState } from 'react';

import styled from '@emotion/styled';

import albumIcon from '../../../../common/assets/images/albumIcon.svg';
import InputSection from '../InputSection/InputSection';
import MenuToggleButton from '../MenuToggleButton/MenuToggleButton';

interface ChatRoomInputAreaProps {
  value: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onSubmit: (e: React.FormEvent<HTMLFormElement>) => void;
}

function ChatRoomInputArea({
  value,
  onChange,
  onSubmit,
}: ChatRoomInputAreaProps) {
  const [menuOpened, setMenuOpened] = useState(false);

  const toggleMenu = () => {
    setMenuOpened((prev) => !prev);
  };

  return (
    <>
      <S_InputWrapper>
        <MenuToggleButton opened={menuOpened} onClick={toggleMenu} />
        <InputSection value={value} onChange={onChange} onSubmit={onSubmit} />
      </S_InputWrapper>
    </>
  );
}
export default ChatRoomInputArea;

const S_InputWrapper = styled.div`
  display: flex;
`;
