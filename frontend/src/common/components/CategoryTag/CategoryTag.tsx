import styled from '@emotion/styled';

interface CategoryTagProps {
  tagName: string;
}

function CategoryTag({ tagName }: CategoryTagProps) {
  return <StyledTagName>{`# ${tagName}`}</StyledTagName>;
}

export default CategoryTag;

const StyledTagName = styled.span`
  color: ${({ theme }) => theme.SYSTEM.GRAY500};
  ${({ theme }) => theme.TYPOGRAPHY.C4_R};
  flex-shrink: 0;
`;
