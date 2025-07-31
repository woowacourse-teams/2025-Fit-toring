import styled from '@emotion/styled';

import menuIcon from '../../../../common/assets/images/menuBar.svg';

function MenuDropDown() {
  return (
    <>
      <StyledMenuButton>
        <StyledMenuIcon src={menuIcon} alt="메뉴 열기 아이콘" />
      </StyledMenuButton>
    </>
  );
}

export default MenuDropDown;

const StyledMenuButton = styled.button`
  display: flex;
  align-items: center;
  justify-content: center;

  width: 3.8rem;
  height: 3.8rem;
  margin-right: 2rem;
  padding: 0;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 50%;

  background: ${({ theme }) => theme.BG.WHITE};
  background-color: transparent;

  color: ${({ theme }) => theme.FONT.B03};
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover {
    background: ${({ theme }) => theme.SYSTEM.MAIN50};
    border-color: ${({ theme }) => theme.SYSTEM.MAIN300};

    color: ${({ theme }) => theme.SYSTEM.MAIN700};
  }
`;

const StyledMenuIcon = styled.img`
  width: 2.4rem;
`;
