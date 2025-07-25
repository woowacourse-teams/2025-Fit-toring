import styled from '@emotion/styled';

import CategoryTag from '../CategoryTag/CategoryTag';
interface CategoryTags {
  tagNames: string[];
}

function CategoryTags({ tagNames }: CategoryTags) {
  return (
    <StyledContainer>
      {tagNames.map((tagName) => (
        <CategoryTag tagName={tagName} key={tagName} />
      ))}
    </StyledContainer>
  );
}

export default CategoryTags;

const StyledContainer = styled.div`
  display: flex;
  gap: 0.7rem;
`;
