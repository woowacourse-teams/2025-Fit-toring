import { useState } from 'react';

import styled from '@emotion/styled';

import downIcon from '../../../../common/assets/images/downIcon.svg';

import type { SortKey } from '../../hooks/useSortKey';
interface SortButtonProps {
  handleSortButtonClick: (option: SortKey) => void;
  currentSortKey: SortKey;
}

const SORT_KEYS = [
  { value: 'CREATED_AT', label: '기본순' },
  { value: 'RESERVATION_COUNT', label: '예약순' },
  { value: 'AVERAGE_RATING', label: '평점순' },
] as const;

function SortButton({
  handleSortButtonClick,
  currentSortKey,
}: SortButtonProps) {
  const [opened, setOpened] = useState(false);
  const handleMenuButtonClick = () => {
    setOpened((prev) => !prev);
  };

  const currentSortLabel =
    SORT_KEYS.find(({ value }) => value === currentSortKey)?.label || '기본순';

  return (
    <S_Container>
      <S_Button onClick={handleMenuButtonClick} type="button">
        <S_Text>{currentSortLabel}</S_Text>
        <S_GoIcon src={downIcon} alt="정렬 아이콘" />
      </S_Button>
      <S_SortList opened={opened}>
        {SORT_KEYS.map(({ value, label }) => (
          <S_SortItem
            key={value}
            onClick={() => handleSortButtonClick(value)}
            selected={currentSortKey === value}
          >
            {label}
          </S_SortItem>
        ))}
      </S_SortList>
    </S_Container>
  );
}

export default SortButton;

const S_Container = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
`;

const S_Button = styled.button`
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.6rem;

  width: 9.4rem;
  height: 3.4rem;
  padding: 1rem;
  border: 1px solid ${({ theme }) => theme.SYSTEM.GRAY300};
  border-radius: 6.75px;

  background: ${({ theme }) => theme.BG.WHITE};
  cursor: pointer;
`;

const S_Text = styled.span`
  ${({ theme }) => theme.TYPOGRAPHY.C4_R};
  color: ${({ theme }) => theme.SYSTEM.GRAY600};
`;

const S_GoIcon = styled.img`
  width: 1.4rem;
  aspect-ratio: 1 / 1;
`;

const S_SortList = styled.ul<{ opened: boolean }>`
  visibility: ${({ opened }) => (opened ? 'visible' : 'hidden')};
  position: absolute;
  top: 100%;
  z-index: 50;

  width: 10rem;
  margin-top: 0.4rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 16px;
  box-shadow: 0 4px 16px rgb(0 0 0 / 10%);

  background-color: ${({ theme }) => theme.BG.WHITE};
  opacity: ${({ opened }) => (opened ? 1 : 0)};
  transform: ${({ opened }) =>
    opened ? ' translateY(0)' : ' translateY(-1rem)'};
  transition: all 0.2s ease;
`;

const S_SortItem = styled.li<{ selected: boolean }>`
  width: 100%;
  padding: 1rem 1.2rem;

  background-color: ${({ selected, theme }) =>
    selected ? theme.SYSTEM.MAIN50 : 'transparent'};

  color: ${({ selected, theme }) =>
    selected ? theme.SYSTEM.MAIN700 : theme.FONT.B03};

  transition: all 0.2s ease;
  cursor: pointer;

  :first-of-type {
    border-radius: 16px 16px 0 0;
  }

  :last-of-type {
    border-radius: 0 0 16px 16px;
  }

  &:hover {
    background-color: ${({ theme }) => theme.SYSTEM.MAIN50};

    color: ${({ theme }) => theme.SYSTEM.MAIN700};
  }

  ${({ theme }) => theme.TYPOGRAPHY.C4_R};
`;
