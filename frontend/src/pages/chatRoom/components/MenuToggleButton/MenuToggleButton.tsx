import styled from '@emotion/styled';

import plusIcon from '../../../../common/assets/images/plusIcon.svg';

interface MenuToggleButtonProps {
  opened: boolean;
  onClick: () => void;
}

function MenuToggleButton({ opened, onClick }: MenuToggleButtonProps) {
  return (
    <S_Container>
      <S_Button onClick={onClick} opened={opened}>
        <S_PlusIcon src={plusIcon} alt="메뉴 아이콘" />
      </S_Button>
    </S_Container>
  );
}

export default MenuToggleButton;

const S_Container = styled.div`
  display: flex;
  gap: 1rem;

  padding: 1.6rem;
  border-top: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const S_Button = styled.button<{ opened: boolean }>`
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
  transform: rotate(${(props) => (props.opened ? '45deg' : '0')});

  transition: transform 0.3s ease-in-out;
  aspect-ratio: 1 / 1;
`;

const S_PlusIcon = styled.img`
  width: 1.6rem;
  height: 1.6rem;
`;
