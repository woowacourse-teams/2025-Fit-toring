import styled from '@emotion/styled';

import MyPageMenuRow from '../MyPageMenuRow/MyPageMenuRow';

export interface MyPageSectionItem {
  iconSrc: string;
  label: string;
  badgeLabel?: string;
  onClick: () => void;
}

interface MyPageSectionProps {
  title: string;
  items: MyPageSectionItem[];
  divided?: boolean;
}

function MyPageSection({ title, items, divided = true }: MyPageSectionProps) {
  return (
    <S_Container divided={divided}>
      <S_Title>{title}</S_Title>
      <S_List>
        {items.map((item) => (
          <li key={item.label}>
            <MyPageMenuRow
              badgeLabel={item.badgeLabel}
              iconSrc={item.iconSrc}
              label={item.label}
              onClick={item.onClick}
            />
          </li>
        ))}
      </S_List>
    </S_Container>
  );
}

export default MyPageSection;

const S_Container = styled.section<{ divided: boolean }>`
  display: flex;
  flex-direction: column;
  gap: 1.6rem;

  padding: 2rem;
  border-bottom: ${({ divided, theme }) =>
    divided ? `1px solid ${theme.OUTLINE.REGULAR}` : 'none'};
`;

const S_Title = styled.h3`
  color: ${({ theme }) => theme.FONT.B04};
  ${({ theme }) => theme.TYPOGRAPHY.B4_R}
`;

const S_List = styled.ul`
  display: flex;
  flex-direction: column;
  gap: 1.2rem;

  margin: 0;
  padding: 0;
`;
