import styled from '@emotion/styled';

interface CategoryTagProps {
  tagName: string;
}

function CategoryTag({ tagName }: CategoryTagProps) {
  return (
    <StyledContainer>
      <StyledTagName>{tagName}</StyledTagName>
    </StyledContainer>
  );
}

export default CategoryTag;

const StyledContainer = styled.div`
  width: fit-content;
  border-radius: 0.675rem;
  border: ${({ theme }) => theme.SYSTEM.MAIN400} 0.1rem solid;
  padding: 0.4rem 0.8rem;
`;

const StyledTagName = styled.span`
  color: ${({ theme }) => theme.SYSTEM.MAIN600};
`;
