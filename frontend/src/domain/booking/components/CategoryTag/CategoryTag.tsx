import styled from '@emotion/styled';

interface CategoryTagItemProps {
  tagName: string;
}

function CategoryTag({ tagName }: CategoryTagItemProps) {
  return (
    <StyledContainer>
      <StyledTagName>{tagName}</StyledTagName>
    </StyledContainer>
  );
}

export default CategoryTag;

const StyledContainer = styled.div`
  border-radius: 0.675rem;
  border: ${({ theme }) => theme.SYSTEM.MAIN300} 0.1rem solid;
  padding: 0.4rem 0.8rem;
`;

const StyledTagName = styled.span`
  color: ${({ theme }) => theme.SYSTEM.MAIN800};
`;
