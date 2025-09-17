import styled from '@emotion/styled';

import CategoryTag from '../CategoryTag/CategoryTag';
interface CategoryTags {
  tagNames: string[];
}

function CategoryTags({ tagNames }: CategoryTags) {
  return (
    <S_Container>
      {tagNames.map((tagName) => (
        <CategoryTag tagName={tagName} key={tagName} />
      ))}
    </S_Container>
  );
}

export default CategoryTags;

const S_Container = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: 0.4rem;
`;
