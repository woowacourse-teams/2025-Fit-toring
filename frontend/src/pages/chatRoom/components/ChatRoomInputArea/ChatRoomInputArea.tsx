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
      {menuOpened && (
        <S_MenuPanel>
          <S_MenuItemWrapper>
            <S_MenuItemLabel>
              <S_HiddenInput type="file" accept="image/*" />
              <S_AlbumIcon src={albumIcon} alt="앨범" />
            </S_MenuItemLabel>
            <S_MenuItemDescription>앨범</S_MenuItemDescription>
          </S_MenuItemWrapper>
        </S_MenuPanel>
      )}
    </>
  );
}

export default ChatRoomInputArea;

const S_InputWrapper = styled.div`
  display: flex;
`;

const S_MenuPanel = styled.div`
  display: flex;
  justify-content: space-between;

  width: 100%;
  height: auto;
  padding: 1.6rem;
  border-top: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const S_MenuItemWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.4rem;
`;

const S_MenuItemLabel = styled.label`
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

const S_HiddenInput = styled.input`
  display: none;
`;

const S_MenuItemDescription = styled.span`
  ${({ theme }) => theme.TYPOGRAPHY.B3_R};
  color: ${({ theme }) => theme.FONT.B01};
`;

const S_AlbumIcon = styled.img`
  width: 1.6rem;
  height: 1.6rem;
`;
