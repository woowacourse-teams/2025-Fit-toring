import styled from '@emotion/styled';

import menuIcon from '../../../../common/assets/images/menuBar.svg';

function MenuDropDown() {
  return (
    <StyledContainer>
      <StyledMenuButton>
        <StyledMenuIcon src={menuIcon} alt="메뉴 열기 아이콘" />
      </StyledMenuButton>

      <StyledMenuList opened={true}>
        <li>개설한 멘토링</li>
        <li>참여한 멘토링</li>
        <li>회원 정보</li>
        <li>로그아웃</li>
      </StyledMenuList>
    </StyledContainer>
  );
}

export default MenuDropDown;

const StyledContainer = styled.div`
  position: relative;
`;

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

const StyledMenuList = styled.ul<{ opened: boolean }>`
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
  position: absolute;
  top: 100%;
  right: 2rem;
  z-index: 50;

  width: 20rem;
  margin-top: 0.4rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 16px;
  box-shadow: 0 0.4rem 1.6rem rgb(0 0 0 / 10%);

  background-color: ${({ theme }) => theme.BG.WHITE};
  opacity: ${({ opened }) => (opened ? 1 : 0)};
  transform: ${({ opened }) =>
    opened ? 'translateY(0)' : 'translateY(-10px)'};
  transition: all 0.2s ease;
`;
