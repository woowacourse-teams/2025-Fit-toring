import styled from '@emotion/styled';

import chevronRightGray from '../../../../common/assets/images/chevron-right-gray.svg';
import MyPageMenuRow from '../MyPageMenuRow/MyPageMenuRow';

export interface MyPageSectionItem {
  label: string;
  onClick: () => void;
}

interface MyPageSectionProps {
  title: string;
  items: MyPageSectionItem[];
}

function MyPageSection({ title, items }: MyPageSectionProps) {
  return (
    <S_Container>
      <S_Title>{title}</S_Title>
      <S_List>
        {items.map((item) => (
          <li key={item.label}>
            <MyPageMenuRow
              iconSrc={chevronRightGray}
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

const S_Container = styled.section`
  display: flex;
  flex-direction: column;
  gap: 2.2rem;

  padding: 2.8rem 0 1.8rem;
  border-bottom: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
`;

const S_Title = styled.h3`
  color: ${({ theme }) => theme.SYSTEM.GRAY400};
  ${({ theme }) => theme.TYPOGRAPHY.B3_R}
`;

const S_List = styled.ul`
  display: flex;
  flex-direction: column;
  gap: 3rem;

  margin: 0;
  padding: 0;
  list-style: none;

  li {
    list-style: none;
  }
`;
