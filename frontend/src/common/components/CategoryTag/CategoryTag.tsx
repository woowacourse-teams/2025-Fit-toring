import styled from '@emotion/styled';

interface CategoryTagProps {
  tagName: string;
}

function CategoryTag({ tagName }: CategoryTagProps) {
  return <S_TagName>{`#${tagName}`}</S_TagName>;
}

export default CategoryTag;

const S_TagName = styled.span`
  flex-shrink: 0;

  color: ${({ theme }) => theme.SYSTEM.GRAY500};

  ${({ theme }) => theme.TYPOGRAPHY.C4_R};
`;
