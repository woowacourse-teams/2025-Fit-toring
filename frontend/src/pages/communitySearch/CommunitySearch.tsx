import styled from '@emotion/styled';

import CommunitySearchHeader from './components/CommunitySearchHeader/CommunitySearchHeader';

function CommunitySearch() {
  return (
    <S_Container>
      <CommunitySearchHeader autoFocus />
    </S_Container>
  );
}

export default CommunitySearch;

const S_Container = styled.div`
  min-height: 100dvh;

  background-color: ${({ theme }) => theme.BG.WHITE};
`;
